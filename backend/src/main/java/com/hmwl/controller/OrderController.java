/**
 * 订单控制器，处理订单相关的HTTP请求
 * 
 * @author 系统生成
 * @date 2026-03-08
 */
package com.hmwl.controller;

import com.hmwl.entity.Order;
import com.hmwl.entity.Settlement;
import com.hmwl.service.OrderService;
import com.hmwl.service.QrCodeService;
import com.hmwl.service.DistanceCalculatorService;
import com.hmwl.service.SettlementService;
import com.hmwl.dto.DistanceCalculateRequest;
import com.hmwl.dto.DistanceCalculateResponse;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private SettlementService settlementService;

    @Autowired
    private QrCodeService qrCodeService;

    @Autowired
    private DistanceCalculatorService distanceCalculatorService;

    /**
     * 获取所有订单列表
     * 
     * @return 订单列表
     */
    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
    public List<Order> list() {
        return orderService.list();
    }

    /**
     * 分页获取订单列表
     * 
     * @param current 当前页码，默认1
     * @param size 每页大小，默认10
     * @return 分页后的订单列表
     */
    @GetMapping(value = "/page", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
    public IPage<Order> page(
            @RequestParam(defaultValue = "1") Integer current, 
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Order> page = new Page<>(current, size);
        return orderService.page(page);
    }

    /**
     * 根据ID获取订单详情
     * 
     * @param id 订单ID
     * @return 订单详情
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
    public Order getById(@PathVariable Long id) {
        return orderService.getById(id);
    }

    /**
     * 保存订单
     * 
     * @param order 订单对象
     * @return 保存后的订单对象
     */
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
    public Order save(@RequestBody Order order) {
        orderService.save(order);
        if (order.getOrderNo() != null && !order.getOrderNo().isEmpty()) {
            String qrCodeUrl = qrCodeService.generateAndUploadQrCode(order.getOrderNo());
            order.setQrCodeUrl(qrCodeUrl);
            orderService.updateById(order);
        }
        return order;
    }

    /**
     * 更新订单
     * 
     * @param order 订单对象
     * @return 更新后的订单对象
     */
    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
    public Order update(@RequestBody Order order) {
        Order existingOrder = orderService.getById(order.getId());
        orderService.updateById(order);
        if (order.getOrderNo() != null && !order.getOrderNo().isEmpty() && 
            (existingOrder.getQrCodeUrl() == null || existingOrder.getQrCodeUrl().isEmpty())) {
            String qrCodeUrl = qrCodeService.generateAndUploadQrCode(order.getOrderNo());
            order.setQrCodeUrl(qrCodeUrl);
            orderService.updateById(order);
        }
        if (order.getStatus() != null && order.getStatus() == 2 && existingOrder.getStatus() != 2) {
            createSettlement(order);
        }
        return order;
    }

    private void createSettlement(Order order) {
        Settlement settlement = new Settlement();
        settlement.setSettlementNo(generateSettlementNo());
        settlement.setType(0);
        settlement.setOrderId(order.getId());
        settlement.setCustomerId(order.getBusinessUserId());
        settlement.setAmount(order.getTotalFee());
        settlement.setStatus(0);
        settlement.setPaymentMethod(order.getPaymentMethod());
        settlement.setCreateTime(new Date());
        settlement.setUpdateTime(new Date());
        settlementService.save(settlement);
    }

    private String generateSettlementNo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return "SET" + sdf.format(new Date()) + (int) (Math.random() * 1000);
    }

    /**
     * 删除订单
     * 
     * @param id 订单ID
     * @return 是否删除成功
     */
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
    public boolean delete(@PathVariable Long id) {
        return orderService.removeById(id);
    }

    /**
     * 计算两点之间的距离
     * 
     * @param request 距离计算请求
     * @return 距离计算结果
     */
    @PostMapping(value = "/calculate-distance", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
    public DistanceCalculateResponse calculateDistance(@RequestBody DistanceCalculateRequest request) {
        if (request == null) {
            return DistanceCalculateResponse.fail("请求参数不能为空");
        }

        double[] startCoords = null;
        double[] endCoords = null;

        if (request.getStartAddress() != null && !request.getStartAddress().isEmpty()) {
            startCoords = distanceCalculatorService.getCoordinatesByAddress(request.getStartAddress());
        }

        if (request.getEndAddress() != null && !request.getEndAddress().isEmpty()) {
            endCoords = distanceCalculatorService.getCoordinatesByAddress(request.getEndAddress());
        }

        if (startCoords == null || endCoords == null) {
            return DistanceCalculateResponse.fail("无法获取地址坐标，请检查地址信息是否完整");
        }

        if (startCoords[0] == 0 && startCoords[1] == 0) {
            return DistanceCalculateResponse.fail("发货地址坐标无效");
        }

        if (endCoords[0] == 0 && endCoords[1] == 0) {
            return DistanceCalculateResponse.fail("收货地址坐标无效");
        }

        if (startCoords[0] == endCoords[0] && startCoords[1] == endCoords[1]) {
            return DistanceCalculateResponse.success(0.0);
        }

        double distance = distanceCalculatorService.calculateDistance(
                startCoords[0], startCoords[1],
                endCoords[0], endCoords[1]
        );

        return DistanceCalculateResponse.success(distance);
    }
}

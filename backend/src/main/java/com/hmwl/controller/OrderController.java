/**
 * 订单控制器，处理订单相关的HTTP请求
 * 
 * @author 系统生成
 * @date 2026-03-08
 */
package com.hmwl.controller;

import com.hmwl.entity.Order;
import com.hmwl.service.OrderService;
import com.hmwl.service.QrCodeService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private QrCodeService qrCodeService;

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
        return order;
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
}

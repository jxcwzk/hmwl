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
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

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
     * 获取订单列表，根据用户角色返回不同的订单
     * 
     * @param userId 用户ID
     * @param userType 用户类型
     * @param businessUserId 业务用户ID
     * @param driverId 司机ID
     * @return 订单列表
     */
    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
    public List<Order> list(
            @RequestParam Long userId,
            @RequestParam Integer userType,
            @RequestParam(required = false) Long businessUserId) {
        System.out.println("userId: " + userId);
        System.out.println("userType: " + userType);
        System.out.println("businessUserId: " + businessUserId);
        
        List<Order> orders = new ArrayList<>();
        
        if (userType == 1) { // 管理员
            System.out.println("Admin: returning all orders");
            orders = orderService.list();
        } else if (userType == 2) { // 客户
            System.out.println("Customer: returning orders for businessUserId: " + businessUserId);
            if (businessUserId != null) {
                // 直接遍历所有订单，手动过滤
                List<Order> allOrders = orderService.list();
                for (Order order : allOrders) {
                    if (order.getBusinessUserId() != null && order.getBusinessUserId().equals(businessUserId)) {
                        orders.add(order);
                    }
                }
                System.out.println("Customer: found " + orders.size() + " orders");
            } else {
                System.out.println("Customer: businessUserId is null, returning empty list");
            }
        } else if (userType == 3) { // 司机
            System.out.println("Driver: returning empty list (driverId not implemented yet)");
            // 暂时返回空列表，因为 driver_id 字段还没有添加到数据库表中
        } else {
            System.out.println("Unknown user type: returning empty list");
        }
        
        System.out.println("Final order count: " + orders.size());
        return orders;
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
        settlement.setOrderNo(order.getOrderNo());
        settlement.setCustomerId(order.getBusinessUserId());
        settlement.setOrderAmount(order.getTotalFee());
        double recommendedPrice = order.getTotalFee() != null ? order.getTotalFee() * 1.4286 : 0.0;
        settlement.setRecommendedPrice(Math.round(recommendedPrice * 100) / 100.0);
        settlement.setFinalAmount(Math.round(recommendedPrice * 100) / 100.0);
        settlement.setAmount(Math.round(recommendedPrice * 100) / 100.0);
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

    /**
     * 指派订单给司机
     * 
     * @param params 包含订单ID和司机ID
     * @return 操作结果
     */
    @PostMapping(value = "/assign-driver", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
    public Result assignDriver(@RequestBody Map<String, Object> params) {
        Long orderId = Long.valueOf(params.get("orderId").toString());
        Long driverId = Long.valueOf(params.get("driverId").toString());
        
        Order order = orderService.getById(orderId);
        if (order == null) {
            return Result.error("订单不存在");
        }
        
        order.setDriverId(driverId);
        order.setUpdateTime(new Date());
        boolean success = orderService.updateById(order);
        
        if (success) {
            return Result.success("指派成功");
        } else {
            return Result.error("指派失败");
        }
    }

    /**
     * 指派网点给订单
     * 
     * @param params 包含订单ID和网点ID
     * @return 操作结果
     */
    @PostMapping(value = "/assign-network", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
    public Result assignNetwork(@RequestBody Map<String, Object> params) {
        Long orderId = Long.valueOf(params.get("orderId").toString());
        Long networkPointId = Long.valueOf(params.get("networkPointId").toString());
        
        Order order = orderService.getById(orderId);
        if (order == null) {
            return Result.error("订单不存在");
        }
        
        order.setNetworkPointId(networkPointId);
        order.setUpdateTime(new Date());
        boolean success = orderService.updateById(order);
        
        if (success) {
            return Result.success("指派成功");
        } else {
            return Result.error("指派失败");
        }
    }

    /**
     * 更新物流进度
     * 
     * @param params 包含订单ID、物流状态和物流进度
     * @return 操作结果
     */
    @PostMapping(value = "/update-logistics", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
    public Result updateLogistics(@RequestBody Map<String, Object> params) {
        try {
            Long orderId = Long.valueOf(params.get("orderId").toString());
            String logisticsStatus = params.get("logisticsStatus") != null ? (String) params.get("logisticsStatus") : "";
            String logisticsProgress = params.get("logisticsProgress") != null ? (String) params.get("logisticsProgress") : "";
            
            Order order = orderService.getById(orderId);
            if (order == null) {
                return Result.error("订单不存在");
            }
            
            order.setLogisticsStatus(logisticsStatus);
            order.setLogisticsProgress(logisticsProgress);
            order.setUpdateTime(new Date());
            boolean success = orderService.updateById(order);
            
            if (success) {
                return Result.success("更新成功");
            } else {
                return Result.error("更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新失败: " + e.getMessage());
        }
    }

    /**
     * 网点提供报价
     * 
     * @param params 包含订单ID和底价
     * @return 操作结果
     */
    @PostMapping(value = "/provide-price", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
    public Result providePrice(@RequestBody Map<String, Object> params) {
        Long orderId = Long.valueOf(params.get("orderId").toString());
        Double baseFee = Double.valueOf(params.get("baseFee").toString());
        
        Order order = orderService.getById(orderId);
        if (order == null) {
            return Result.error("订单不存在");
        }
        
        order.setBaseFee(baseFee);
        // 默认系数为1.4286
        order.setCoefficient(1.4286);
        // 计算客户报价
        order.setTotalFee(baseFee * 1.4286);
        order.setUpdateTime(new Date());
        boolean success = orderService.updateById(order);
        
        if (success) {
            return Result.success("报价成功");
        } else {
            return Result.error("报价失败");
        }
    }

    /**
     * 管理员修改报价
     * 
     * @param params 包含订单ID和客户报价
     * @return 操作结果
     */
    @PostMapping(value = "/update-price", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
    public Result updatePrice(@RequestBody Map<String, Object> params) {
        Long orderId = Long.valueOf(params.get("orderId").toString());
        Double totalFee = Double.valueOf(params.get("totalFee").toString());
        
        Order order = orderService.getById(orderId);
        if (order == null) {
            return Result.error("订单不存在");
        }
        
        order.setTotalFee(totalFee);
        order.setUpdateTime(new Date());
        boolean success = orderService.updateById(order);
        
        if (success) {
            return Result.success("修改成功");
        } else {
            return Result.error("修改失败");
        }
    }

    /**
     * 获取司机的订单列表
     * 
     * @param driverId 司机ID
     * @return 订单列表
     */
    @GetMapping(value = "/driver-list", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
    public List<Order> getDriverOrderList(@RequestParam Long driverId) {
        return orderService.list(new QueryWrapper<Order>()
                .eq("driver_id", driverId));
    }

    /**
     * 获取网点的订单列表
     * 
     * @param networkPointId 网点ID
     * @return 订单列表
     */
    @GetMapping(value = "/network-list", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
    public List<Order> getNetworkOrderList(@RequestParam Long networkPointId) {
        return orderService.list(new QueryWrapper<Order>()
                .eq("network_point_id", networkPointId));
    }
}

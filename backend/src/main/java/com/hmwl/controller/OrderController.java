/**
 * 订单控制器，处理订单相关的HTTP请求
 * 
 * @author 系统生成
 * @date 2026-03-08
 */
package com.hmwl.controller;

import com.hmwl.entity.Order;
import com.hmwl.entity.Settlement;
import com.hmwl.entity.OrderAssignHistory;
import com.hmwl.entity.Driver;
import com.hmwl.entity.NetworkPoint;
import com.hmwl.entity.Route;
import com.hmwl.entity.NetworkQuote;
import com.hmwl.service.OrderService;
import com.hmwl.service.QrCodeService;
import com.hmwl.service.RouteService;
import com.hmwl.service.NetworkQuoteService;
import com.hmwl.service.DistanceCalculatorService;
import com.hmwl.service.SettlementService;
import com.hmwl.service.OrderAssignHistoryService;
import com.hmwl.service.DriverService;
import com.hmwl.service.NetworkPointService;
import com.hmwl.dto.DistanceCalculateRequest;
import com.hmwl.dto.DistanceCalculateResponse;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

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

    @Autowired
    private OrderAssignHistoryService orderAssignHistoryService;

    @Autowired
    private DriverService driverService;

    @Autowired
    private NetworkPointService networkPointService;

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
    public Result list(
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
        return Result.success(orders);
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
        if (params.get("orderId") == null || params.get("driverId") == null) {
            return Result.error("参数错误：缺少orderId或driverId");
        }
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
        if (params.get("orderId") == null || params.get("networkPointId") == null) {
            return Result.error("参数错误：缺少orderId或networkPointId");
        }
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
            if (params.get("orderId") == null) {
                return Result.error("参数错误：缺少orderId");
            }
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
        if (params.get("orderId") == null || params.get("baseFee") == null) {
            return Result.error("参数错误：缺少orderId或baseFee");
        }
        Long orderId = Long.valueOf(params.get("orderId").toString());
        Double baseFee = Math.round(Double.valueOf(params.get("baseFee").toString()) * 100.0) / 100.0;
        
        Order order = orderService.getById(orderId);
        if (order == null) {
            return Result.error("订单不存在");
        }
        
        order.setBaseFee(baseFee);
        order.setCoefficient(1.4286);
        order.setTotalFee(Math.round(baseFee * 1.4286 * 100.0) / 100.0);
        
        if (params.get("networkPaymentMethod") != null) {
            order.setNetworkPaymentMethod(Integer.valueOf(params.get("networkPaymentMethod").toString()));
        }
        
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
        if (params.get("orderId") == null || params.get("totalFee") == null) {
            return Result.error("参数错误：缺少orderId或totalFee");
        }
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

    // ========== 订单分配优化相关API ==========

    /**
     * 批量分配订单给司机
     * 
     * @param params 包含订单ID列表和司机ID
     * @return 操作结果
     */
    @PostMapping(value = "/batch-assign", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
    public Result batchAssign(@RequestBody Map<String, Object> params) {
        List<Long> orderIds = (List<Long>) params.get("orderIds");
        Long driverId = Long.valueOf(params.get("driverId").toString());
        Long operatorId = params.get("operatorId") != null ? Long.valueOf(params.get("operatorId").toString()) : null;
        String operatorName = params.get("operatorName") != null ? params.get("operatorName").toString() : "系统";
        
        Driver driver = driverService.getById(driverId);
        if (driver == null) {
            return Result.error("司机不存在");
        }
        
        int successCount = 0;
        int skipCount = 0;
        
        for (Long orderId : orderIds) {
            Order order = orderService.getById(orderId);
            if (order == null) {
                skipCount++;
                continue;
            }
            if (order.getDriverId() != null) {
                skipCount++;
                continue;
            }
            
            order.setDriverId(driverId);
            order.setStatus(1);
            order.setUpdateTime(new Date());
            boolean success = orderService.updateById(order);
            
            if (success) {
                orderAssignHistoryService.saveAssignRecord(
                    orderId, order.getOrderNo(), driverId, driver.getName(),
                    operatorId, operatorName, 1, "批量分配"
                );
                successCount++;
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("successCount", successCount);
        result.put("skipCount", skipCount);
        result.put("totalCount", orderIds.size());
        
        return Result.success(result);
    }

    /**
     * 获取推荐司机列表（智能推荐）
     * 
     * @param orderId 订单ID（可选，用于计算距离）
     * @return 推荐司机列表
     */
    @GetMapping(value = "/recommend-drivers", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
    public Result recommendDrivers(@RequestParam(required = false) Long orderId) {
        List<Driver> allDrivers = driverService.list();
        
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("status", 1, 3, 4);
        List<Order> allOrders = orderService.list(queryWrapper);
        
        Map<Long, Integer> driverWorkload = new HashMap<>();
        for (Order order : allOrders) {
            if (order.getDriverId() != null) {
                driverWorkload.put(order.getDriverId(), driverWorkload.getOrDefault(order.getDriverId(), 0) + 1);
            }
        }
        
        List<Map<String, Object>> recommendedList = new ArrayList<>();
        for (Driver driver : allDrivers) {
            Map<String, Object> item = new HashMap<>();
            item.put("driverId", driver.getId());
            item.put("driverName", driver.getName());
            item.put("driverPhone", driver.getPhone());
            
            int workload = driverWorkload.getOrDefault(driver.getId(), 0);
            item.put("currentWorkload", workload);
            
            double score = 100.0 - (workload * 10.0);
            if (score < 0) score = 0;
            item.put("score", score);
            
            item.put("status", 1);
            
            recommendedList.add(item);
        }
        
        recommendedList.sort((a, b) -> Double.compare((Double) b.get("score"), (Double) a.get("score")));
        
        return Result.success(recommendedList);
    }

    /**
     * 获取分配历史
     * 
     * @param orderId 订单ID（可选）
     * @param limit 返回数量限制
     * @return 分配历史列表
     */
    @GetMapping(value = "/assign-history", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
    public Result getAssignHistory(@RequestParam(required = false) Long orderId, 
                                   @RequestParam(defaultValue = "20") Integer limit) {
        List<OrderAssignHistory> history;
        if (orderId != null) {
            history = orderAssignHistoryService.getByOrderId(orderId);
        } else {
            history = orderAssignHistoryService.getRecent(limit);
        }
        return Result.success(history);
    }

    // ========== 司机端小程序相关API ==========

    /**
     * 司机接单
     * 
     * @param params 包含订单ID和司机ID
     * @return 操作结果
     */
    @PostMapping(value = "/driver/accept", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
    public Result driverAccept(@RequestBody Map<String, Object> params) {
        if (params.get("orderId") == null || params.get("driverId") == null) {
            return Result.error("参数错误：缺少orderId或driverId");
        }
        Long orderId = Long.valueOf(params.get("orderId").toString());
        Long driverId = Long.valueOf(params.get("driverId").toString());
        
        Order order = orderService.getById(orderId);
        if (order == null) {
            return Result.error("订单不存在");
        }
        
        if (order.getDriverId() != null && !order.getDriverId().equals(driverId)) {
            return Result.error("订单已分配给其他司机");
        }
        
        order.setDriverId(driverId);
        order.setStatus(1);
        order.setUpdateTime(new Date());
        
        boolean success = orderService.updateById(order);
        
        if (success) {
            return Result.success("接单成功");
        } else {
            return Result.error("接单失败");
        }
    }

    /**
     * 司机拒单
     * 
     * @param params 包含订单ID、司机ID和拒绝原因
     * @return 操作结果
     */
    @PostMapping(value = "/driver/reject", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
    public Result driverReject(@RequestBody Map<String, Object> params) {
        if (params.get("orderId") == null || params.get("driverId") == null) {
            return Result.error("参数错误：缺少orderId或driverId");
        }
        Long orderId = Long.valueOf(params.get("orderId").toString());
        Long driverId = Long.valueOf(params.get("driverId").toString());
        String reason = params.get("reason") != null ? params.get("reason").toString() : "司机拒单";
        
        Order order = orderService.getById(orderId);
        if (order == null) {
            return Result.error("订单不存在");
        }
        
        if (order.getDriverId() != null && !order.getDriverId().equals(driverId)) {
            return Result.error("订单已分配给其他司机");
        }
        
        order.setDriverId(null);
        order.setStatus(0);
        order.setLogisticsProgress(reason);
        order.setUpdateTime(new Date());
        
        boolean success = orderService.updateById(order);
        
        if (success) {
            return Result.success("拒单成功");
        } else {
            return Result.error("拒单失败");
        }
    }

    /**
     * 司机更新订单状态
     * 
     * @param params 包含订单ID、新状态和备注
     * @return 操作结果
     */
    @PostMapping(value = "/driver/update-status", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
    public Result driverUpdateStatus(@RequestBody Map<String, Object> params) {
        if (params.get("orderId") == null || params.get("status") == null) {
            return Result.error("参数错误：缺少orderId或status");
        }
        Long orderId = Long.valueOf(params.get("orderId").toString());
        Integer newStatus = Integer.valueOf(params.get("status").toString());
        String remark = params.get("remark") != null ? params.get("remark").toString() : "";
        
        Order order = orderService.getById(orderId);
        if (order == null) {
            return Result.error("订单不存在");
        }
        
        String statusText = "";
        switch (newStatus) {
            case 2:
                statusText = "已取货";
                break;
            case 3:
                statusText = "配送中";
                break;
            case 4:
                statusText = "已送达";
                break;
            case 5:
                statusText = "已完成";
                break;
            default:
                statusText = "状态更新";
        }
        
        if (!remark.isEmpty()) {
            order.setLogisticsProgress(statusText + "：" + remark);
        } else {
            order.setLogisticsProgress(statusText);
        }
        
        order.setStatus(newStatus);
        order.setUpdateTime(new Date());
        
        boolean success = orderService.updateById(order);
        
        if (success) {
            return Result.success("状态更新成功");
        } else {
            return Result.error("状态更新失败");
        }
    }

    @Autowired
    private RouteService routeService;
    
    @Autowired
    private NetworkQuoteService networkQuoteService;

    @PostMapping(value = "/request-quotes", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
    public Result requestQuotes(@RequestBody Map<String, Object> params) {
        try {
            if (params.get("orderId") == null) {
                return Result.error("参数错误：缺少orderId");
            }
            Long orderId = Long.valueOf(params.get("orderId").toString());
            
            Order order = orderService.getById(orderId);
            if (order == null) {
                return Result.error("订单不存在");
            }
            
            String receiverCity = extractCity(order.getReceiverAddress());
            List<Route> routes = routeService.findRoutesByDestination(receiverCity);
            
            if (routes == null || routes.isEmpty()) {
                return Result.error("未找到目的地的路线: " + receiverCity);
            }
            
            List<NetworkQuote> quotes = new ArrayList<>();
            
            for (Route route : routes) {
                Long networkId = route.getNetworkPointId();
                NetworkPoint network = networkPointService.getById(networkId);
                String networkName = network != null && network.getName() != null ? network.getName() : "网点" + networkId;
                
                double baseFee = Math.round((route.getBasePrice() + (order.getWeight() != null ? order.getWeight() * route.getPricePerKg() : 0)) * 100.0) / 100.0;
                double finalPrice = Math.round(baseFee * 1.4286 * 100.0) / 100.0;
                
                NetworkQuote quote = new NetworkQuote();
                quote.setNetworkPointId(networkId);
                quote.setNetworkName(networkName);
                quote.setBaseFee(baseFee);
                quote.setFinalPrice(finalPrice);
                quote.setTransitDays(route.getTransitDays());
                quote.setStatus(1);
                quote.setQuoteTime(new Date());
                quotes.add(quote);
            }
            
            if (!quotes.isEmpty()) {
                networkQuoteService.saveQuotes(orderId, quotes);
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("quotes", quotes);
            result.put("destinationCity", receiverCity);
            result.put("message", "已生成报价，请调度员选择最低价");
            
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取报价失败: " + e.getMessage());
        }
    }
    
    private String extractCity(String address) {
        if (address == null || address.isEmpty()) {
            return "";
        }
        if (address.contains("内蒙古")) return "内蒙古";
        if (address.contains("新疆")) return "新疆";
        if (address.contains("西藏")) return "西藏";
        if (address.contains("宁夏")) return "宁夏";
        if (address.contains("广西")) return "广西";
        if (address.contains("黑龙江")) return "黑龙江";
        if (address.contains("吉林")) return "吉林";
        if (address.contains("辽宁")) return "辽宁";
        if (address.contains("河北")) return "河北";
        if (address.contains("山西")) return "山西";
        if (address.contains("陕西")) return "陕西";
        if (address.contains("甘肃")) return "甘肃";
        if (address.contains("青海")) return "青海";
        if (address.contains("山东")) return "山东";
        if (address.contains("河南")) return "河南";
        if (address.contains("江苏")) return "江苏";
        if (address.contains("浙江")) return "浙江";
        if (address.contains("安徽")) return "安徽";
        if (address.contains("江西")) return "江西";
        if (address.contains("福建")) return "福建";
        if (address.contains("台湾")) return "台湾";
        if (address.contains("湖北")) return "湖北";
        if (address.contains("湖南")) return "湖南";
        if (address.contains("广东")) return "广东";
        if (address.contains("海南")) return "海南";
        if (address.contains("四川")) return "四川";
        if (address.contains("贵州")) return "贵州";
        if (address.contains("云南")) return "云南";
        if (address.contains("重庆")) return "重庆市";
        if (address.contains("天津")) return "天津市";
        if (address.contains("上海")) return "上海市";
        if (address.contains("北京")) return "北京市";
        if (address.contains("广州")) return "广州市";
        if (address.contains("深圳")) return "深圳市";
        if (address.contains("杭州")) return "杭州市";
        if (address.contains("南京")) return "南京市";
        if (address.contains("武汉")) return "武汉市";
        if (address.contains("成都")) return "成都市";
        if (address.contains("西安")) return "西安市";
        if (address.contains("苏州")) return "苏州市";
        return address;
    }
    
    @GetMapping("/dispatch/quotes")
    public Object getDispatchQuotes(@RequestParam(required = false) Long orderId,
                                   @RequestParam(required = false) Integer status) {
        List<NetworkQuote> quotes;
        
        if (orderId != null) {
            quotes = networkQuoteService.getQuotesByOrderId(orderId);
        } else if (status != null) {
            quotes = networkQuoteService.list();
            List<NetworkQuote> filtered = new ArrayList<>();
            for (NetworkQuote q : quotes) {
                if (q.getStatus() != null && q.getStatus().equals(status)) {
                    filtered.add(q);
                }
            }
            quotes = filtered;
        } else {
            quotes = networkQuoteService.list();
        }
        
        return Result.success(quotes);
    }
    
    @GetMapping("/dispatch/orders/pending-quote")
    public Object getOrdersPendingQuote() {
        List<Order> allOrders = orderService.list();
        List<Order> pendingOrders = new ArrayList<>();
        
        for (Order order : allOrders) {
            List<NetworkQuote> quotes = networkQuoteService.getQuotesByOrderId(order.getId());
            boolean hasQuotes = quotes != null && !quotes.isEmpty();
            boolean hasSelected = false;
            
            if (hasQuotes) {
                for (NetworkQuote q : quotes) {
                    if (q.getStatus() != null && q.getStatus() == 2) {
                        hasSelected = true;
                        break;
                    }
                }
            }
            
            if (hasQuotes && !hasSelected) {
                pendingOrders.add(order);
            }
        }
        
        return Result.success(pendingOrders);
    }
    
    @PostMapping("/dispatch/select-quote")
    public Object selectDispatchQuote(@RequestBody Map<String, Object> params) {
        try {
            if (params.get("quoteId") == null || params.get("orderId") == null) {
                return Result.error("参数错误：缺少quoteId或orderId");
            }
            
            Long quoteId = Long.valueOf(params.get("quoteId").toString());
            Long orderId = Long.valueOf(params.get("orderId").toString());
            
            NetworkQuote selectedQuote = networkQuoteService.getById(quoteId);
            if (selectedQuote == null) {
                return Result.error("报价不存在");
            }
            
            Order order = orderService.getById(orderId);
            if (order == null) {
                return Result.error("订单不存在");
            }
            
            order.setBaseFee(selectedQuote.getBaseFee());
            order.setTotalFee(selectedQuote.getFinalPrice());
            order.setNetworkPointId(selectedQuote.getNetworkPointId());
            order.setStatus(1);
            order.setLogisticsProgress("调度已确认报价，等待网点确认");
            order.setUpdateTime(new Date());
            orderService.updateById(order);
            
            networkQuoteService.selectBestQuote(orderId, quoteId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("selectedQuote", selectedQuote);
            result.put("order", order);
            result.put("message", "已选择报价，订单已更新");
            
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("选择报价失败: " + e.getMessage());
        }
    }
}

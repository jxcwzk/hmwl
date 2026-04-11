package com.hmwl.controller;

import com.hmwl.entity.Order;
import com.hmwl.entity.NetworkQuote;
import com.hmwl.entity.NetworkPoint;
import com.hmwl.service.OrderService;
import com.hmwl.service.NetworkQuoteService;
import com.hmwl.service.NetworkPointService;
import com.hmwl.service.OrderTimelineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/dispatch")
public class DispatchController {
    private static final Logger log = LoggerFactory.getLogger(DispatchController.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private NetworkQuoteService networkQuoteService;

    @Autowired
    private NetworkPointService networkPointService;

    @Autowired
    private OrderTimelineService orderTimelineService;

    @GetMapping("/test")
    public Map<String, String> test() {
        Map<String, String> result = new HashMap<>();
        result.put("status", "DispatchController is working!");
        return result;
    }

    @GetMapping("/orders/pricing")
    public List<Order> getOrdersPricing() {
        return orderService.lambdaQuery()
            .eq(Order::getPricingStatus, 0)
            .list();
    }

    @GetMapping("/quotes")
    public List<NetworkQuote> getQuotes(@RequestParam Long orderId) {
        return networkQuoteService.lambdaQuery()
            .eq(NetworkQuote::getOrderId, orderId)
            .list();
    }

    @PostMapping("/request-quotes")
    public Map<String, Object> requestQuotes(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        Long orderId = Long.valueOf(params.get("orderId").toString());
        @SuppressWarnings("unchecked")
        List<Long> networkPointIds = (List<Long>) params.get("networkPointIds");

        log.info("派发比价请求：orderId={}, networkPointIds={}", orderId, networkPointIds);

        Order order = orderService.getById(orderId);
        if (order == null) {
            result.put("success", false);
            result.put("message", "订单不存在");
            return result;
        }

        order.setStatus(1);
        order.setPricingStatus(1);
        orderService.updateById(order);

        orderTimelineService.recordTimeline(
            order.getOrderNo(),
            null,
            "DISPATCHER",
            "ORDER_DISPATCHED",
            "已派发比价",
            "调度派发订单进行比价"
        );

        if (networkPointIds != null && !networkPointIds.isEmpty()) {
            List<NetworkQuote> quotes = new ArrayList<>();
            for (Number networkIdNum : networkPointIds) {
                Long networkId = networkIdNum.longValue();
                NetworkPoint network = networkPointService.getById(networkId);
                String networkName = network != null && network.getName() != null ? network.getName() : "网点" + networkId;

                double baseFee = 100.0;
                double finalPrice = 150.0;
                if (order.getWeight() != null) {
                    baseFee = Math.round((50.0 + order.getWeight() * 2.0) * 100.0) / 100.0;
                    finalPrice = Math.round(baseFee * 1.4286 * 100.0) / 100.0;
                }

                NetworkQuote quote = new NetworkQuote();
                quote.setNetworkPointId(networkId);
                quote.setNetworkName(networkName);
                quote.setBaseFee(baseFee);
                quote.setFinalPrice(finalPrice);
                quote.setTransitDays(3);
                quote.setStatus(1);
                quote.setQuoteTime(new Date());
                quotes.add(quote);
            }

            if (!quotes.isEmpty()) {
                networkQuoteService.saveQuotes(orderId, quotes);
            }
        }

        log.info("订单状态已更新为待确认报价：orderId={}", orderId);
        result.put("success", true);
        result.put("message", "派发比价成功");
        return result;
    }

    @GetMapping("/orders/pending-pickup")
    public List<Order> getPendingPickupOrders() {
        return orderService.lambdaQuery()
            .eq(Order::getPricingStatus, 3)
            .list();
    }

    @GetMapping("/orders/pending-delivery")
    public List<Order> getPendingDeliveryOrders() {
        return orderService.lambdaQuery()
            .eq(Order::getPricingStatus, 5)
            .list();
    }

    @GetMapping("/orders/receipt-pending")
    public List<Order> getReceiptPendingOrders() {
        return orderService.lambdaQuery()
            .isNotNull(Order::getReceiptPhotos)
            .eq(Order::getReceiptConfirmed, 0)
            .list();
    }

    @PostMapping("/confirm-receipt")
    public Map<String, Object> confirmReceipt(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long orderId = Long.valueOf(params.get("orderId").toString());
            Order order = orderService.getById(orderId);
            
            if (order == null) {
                result.put("success", false);
                result.put("message", "订单不存在");
                return result;
            }
            
            order.setReceiptConfirmed(1);
            order.setStatus(12);
            order.setDeliveryCompletedTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            order.setLogisticsProgress("回单已确认，订单完成");
            order.setUpdateTime(new Date());
            orderService.updateById(order);

            orderTimelineService.recordTimeline(
                order.getOrderNo(),
                null,
                "DISPATCHER",
                "RECEIPT_CONFIRMED",
                "回单已确认",
                "调度确认回单，订单完成"
            );
            
            result.put("success", true);
            result.put("message", "回单确认成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "确认失败: " + e.getMessage());
        }
        return result;
    }

    @PostMapping("/select-quote")
    public Map<String, Object> selectQuote(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        Long quoteId = Long.valueOf(params.get("quoteId").toString());
        Long orderId = Long.valueOf(params.get("orderId").toString());

        NetworkQuote quote = networkQuoteService.getById(quoteId);
        if (quote != null) {
            quote.setStatus(2);
            networkQuoteService.updateById(quote);
        }

        Order order = orderService.getById(orderId);
        if (order != null) {
            order.setNetworkPointId(quote.getNetworkPointId());
            order.setPricingStatus(2);
            orderService.updateById(order);

            orderTimelineService.recordTimeline(
                order.getOrderNo(),
                null,
                "DISPATCHER",
                "QUOTE_SELECTED",
                "已选择报价",
                "调度选择最优报价"
            );
        }

        result.put("success", true);
        result.put("message", "Quote selected successfully");
        return result;
    }

    @PostMapping("/confirm-pickup")
    public Map<String, Object> confirmPickup(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        Long orderId = Long.valueOf(params.get("orderId").toString());

        Order order = orderService.getById(orderId);
        if (order != null) {
            order.setPricingStatus(3);
            orderService.updateById(order);

            orderTimelineService.recordTimeline(
                order.getOrderNo(),
                null,
                "DISPATCHER",
                "PICKUP_CONFIRMED",
                "已确认提货",
                "调度确认提货安排"
            );
        }

        result.put("success", true);
        result.put("message", "Pickup confirmed");
        return result;
    }

    @PostMapping("/assign-pickup-driver")
    public Map<String, Object> assignPickupDriver(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        Long orderId = Long.valueOf(params.get("orderId").toString());
        Long driverId = params.get("driverId") != null ?
            Long.valueOf(params.get("driverId").toString()) : null;

        Order order = orderService.getById(orderId);
        if (order != null) {
            order.setDriverId(driverId);
            order.setPricingStatus(4);
            orderService.updateById(order);

            orderTimelineService.recordTimeline(
                order.getOrderNo(),
                driverId,
                "DISPATCHER",
                "PICKUP_DRIVER_ASSIGNED",
                "已安排提货司机",
                "调度安排提货司机"
            );
        }

        result.put("success", true);
        result.put("message", "Pickup driver assigned");
        return result;
    }

    @PostMapping("/push-quote")
    public Object pushQuote(@RequestBody Map<String, Object> params) {
        try {
            if (params.get("orderId") == null) {
                return Result.error("参数错误：缺少orderId");
            }

            Long orderId = Long.valueOf(params.get("orderId").toString());
            Order order = orderService.getById(orderId);
            if (order == null) {
                return Result.error("订单不存在");
            }

            order.setStatus(4);
            order.setLogisticsProgress("调度已推送报价，等待客户确认价格");
            order.setUpdateTime(new Date());
            orderService.updateById(order);

            orderTimelineService.recordTimeline(
                order.getOrderNo(),
                null,
                "DISPATCHER",
                "QUOTE_PUSHED",
                "已推送报价",
                "调度推送报价给客户确认"
            );

            Map<String, Object> result = new HashMap<>();
            result.put("orderId", orderId);
            result.put("status", 4);
            result.put("message", "报价已推送给客户");

            return Result.success(result);
        } catch (Exception e) {
            log.error("推送报价失败", e);
            return Result.error("推送报价失败: " + e.getMessage());
        }
    }

    @PostMapping("/assign-delivery-driver")
    public Map<String, Object> assignDeliveryDriver(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();

        if (params.get("orderId") == null) {
            result.put("success", false);
            result.put("message", "参数错误：缺少orderId");
            return result;
        }

        if (params.get("driverId") == null) {
            result.put("success", false);
            result.put("message", "参数错误：缺少driverId");
            return result;
        }

        Long orderId = Long.valueOf(params.get("orderId").toString());
        Long driverId = Long.valueOf(params.get("driverId").toString());

        Order order = orderService.getById(orderId);
        if (order != null) {
            order.setDeliveryDriverId(driverId);
            order.setPricingStatus(6);
            orderService.updateById(order);

            orderTimelineService.recordTimeline(
                order.getOrderNo(),
                driverId,
                "DISPATCHER",
                "DELIVERY_DRIVER_ASSIGNED",
                "已安排配送司机",
                "调度安排配送司机"
            );
        }

        result.put("success", true);
        result.put("message", "Delivery driver assigned");
        return result;
    }

    @PostMapping("/confirm-price")
    public Map<String, Object> confirmPrice(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();

        if (params.get("orderId") == null) {
            result.put("success", false);
            result.put("message", "参数错误：缺少orderId");
            return result;
        }

        Long orderId = Long.valueOf(params.get("orderId").toString());
        Order order = orderService.getById(orderId);

        if (order == null) {
            result.put("success", false);
            result.put("message", "订单不存在");
            return result;
        }

        order.setPricingStatus(3);
        order.setStatus(5);
        order.setPriceConfirmedTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        order.setLogisticsProgress("客户已确认价格，等待安排提货");
        order.setUpdateTime(new Date());
        orderService.updateById(order);

        orderTimelineService.recordTimeline(
            order.getOrderNo(),
            order.getBusinessUserId(),
            "CUSTOMER",
            "PRICE_CONFIRMED",
            "已确认价格",
            "客户确认价格"
        );

        result.put("success", true);
        result.put("message", "价格确认成功");
        return result;
    }
}
package com.hmwl.controller;

import com.hmwl.entity.Order;
import com.hmwl.entity.NetworkQuote;
import com.hmwl.service.OrderService;
import com.hmwl.service.NetworkQuoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/dispatch")
public class DispatchController {
    private static final Logger log = LoggerFactory.getLogger(DispatchController.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private NetworkQuoteService networkQuoteService;

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
        List<Long> networkIds = (List<Long>) params.get("networkIds");

        Order order = orderService.getById(orderId);
        if (order != null) {
            order.setPricingStatus(1);
            orderService.updateById(order);
        }

        result.put("success", true);
        result.put("message", "Quote request submitted successfully");
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
            order.setPricingStatus(2);
            orderService.updateById(order);
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
        }

        result.put("success", true);
        result.put("message", "Delivery driver assigned");
        return result;
    }
}
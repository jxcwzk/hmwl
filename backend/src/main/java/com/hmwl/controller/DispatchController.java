package com.hmwl.controller;

import com.hmwl.entity.Order;
import com.hmwl.entity.NetworkQuote;
import com.hmwl.service.OrderService;
import com.hmwl.service.NetworkQuoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/dispatch")
public class DispatchController {

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

    @PostMapping("/assign-delivery-driver")
    public Map<String, Object> assignDeliveryDriver(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        Long orderId = Long.valueOf(params.get("orderId").toString());

        Order order = orderService.getById(orderId);
        if (order != null) {
            order.setPricingStatus(6);
            orderService.updateById(order);
        }

        result.put("success", true);
        result.put("message", "Delivery driver assigned");
        return result;
    }
}
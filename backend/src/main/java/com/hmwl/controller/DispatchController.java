package com.hmwl.controller;

import com.hmwl.entity.NetworkQuote;
import com.hmwl.entity.Order;
import com.hmwl.service.NetworkQuoteService;
import com.hmwl.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/dispatch")
public class DispatchController {
    
    @GetMapping("/test")
    public Object test() {
        return "DispatchController is working";
    }
    
    @Autowired
    private NetworkQuoteService networkQuoteService;
    
    @Autowired
    private OrderService orderService;
    
    @GetMapping("/quotes")
    public Object getQuotes(@RequestParam(required = false) Long orderId,
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
    
    @GetMapping("/orders/pending-quote")
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
    
    @PostMapping("/select-quote")
    public Object selectQuote(@RequestBody Map<String, Object> params) {
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
    
    @PostMapping("/reject-quote")
    public Object rejectQuote(@RequestBody Map<String, Object> params) {
        try {
            if (params.get("quoteId") == null) {
                return Result.error("参数错误：缺少quoteId");
            }
            
            Long quoteId = Long.valueOf(params.get("quoteId").toString());
            String reason = params.get("reason") != null ? params.get("reason").toString() : "调度拒绝";
            
            NetworkQuote quote = networkQuoteService.getById(quoteId);
            if (quote == null) {
                return Result.error("报价不存在");
            }
            
            quote.setStatus(3);
            quote.setRemark(reason);
            quote.setUpdateTime(new Date());
            networkQuoteService.updateById(quote);
            
            return Result.success("报价已拒绝");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("拒绝报价失败: " + e.getMessage());
        }
    }
}

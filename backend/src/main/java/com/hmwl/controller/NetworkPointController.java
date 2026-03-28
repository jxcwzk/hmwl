package com.hmwl.controller;

import com.hmwl.entity.NetworkPoint;
import com.hmwl.entity.NetworkQuote;
import com.hmwl.entity.Order;
import com.hmwl.service.NetworkPointService;
import com.hmwl.service.NetworkQuoteService;
import com.hmwl.service.OrderService;
import com.hmwl.service.OrderTimelineService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/network")
public class NetworkPointController {

    @Autowired
    private NetworkPointService networkPointService;

    @Autowired
    private NetworkQuoteService networkQuoteService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderTimelineService orderTimelineService;

    @GetMapping("/list")
    public List<NetworkPoint> list() {
        return networkPointService.list();
    }

    @GetMapping("/page")
    public IPage<NetworkPoint> page(@RequestParam(defaultValue = "1") Integer current, @RequestParam(defaultValue = "10") Integer size) {
        Page<NetworkPoint> page = new Page<>(current, size);
        return networkPointService.page(page);
    }

    @GetMapping("/{id}")
    public NetworkPoint getById(@PathVariable Long id) {
        return networkPointService.getById(id);
    }

    @PostMapping
    public boolean save(@RequestBody NetworkPoint networkPoint) {
        return networkPointService.save(networkPoint);
    }

    @PutMapping
    public boolean update(@RequestBody NetworkPoint networkPoint) {
        return networkPointService.updateById(networkPoint);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return networkPointService.removeById(id);
    }

    @PostMapping("/quote")
    public Object submitQuote(@RequestBody Map<String, Object> params) {
        try {
            if (params.get("orderId") == null || params.get("networkId") == null) {
                return Result.error("参数错误：缺少orderId或networkId");
            }

            Long orderId = Long.valueOf(params.get("orderId").toString());
            Long networkId = Long.valueOf(params.get("networkId").toString());
            Double baseFee = params.get("baseFee") != null ?
                Double.valueOf(params.get("baseFee").toString()) : 0.0;
            Double finalPrice = params.get("finalPrice") != null ?
                Double.valueOf(params.get("finalPrice").toString()) : 0.0;
            Integer transitDays = params.get("transitDays") != null ?
                Integer.valueOf(params.get("transitDays").toString()) : 1;

            Order order = orderService.getById(orderId);
            if (order == null) {
                return Result.error("订单不存在");
            }

            NetworkQuote quote = new NetworkQuote();
            quote.setOrderId(orderId);
            quote.setNetworkPointId(networkId);
            quote.setBaseFee(baseFee);
            quote.setFinalPrice(finalPrice);
            quote.setTransitDays(transitDays);
            quote.setStatus(1);
            quote.setCreateTime(new Date());
            quote.setUpdateTime(new Date());
            networkQuoteService.save(quote);

            order.setPricingStatus(1);
            order.setUpdateTime(new Date());
            orderService.updateById(order);

            Map<String, Object> result = new HashMap<>();
            result.put("quoteId", quote.getId());
            result.put("orderId", orderId);
            result.put("networkId", networkId);
            result.put("message", "报价已提交");

            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("提交报价失败: " + e.getMessage());
        }
    }

    @PostMapping("/confirm-receive")
    public Object confirmReceive(@RequestBody Map<String, Object> params) {
        try {
            if (params.get("orderId") == null || params.get("networkId") == null) {
                return Result.error("参数错误：缺少orderId或networkId");
            }

            Long orderId = Long.valueOf(params.get("orderId").toString());
            Long networkId = Long.valueOf(params.get("networkId").toString());
            String checkResult = params.get("checkResult") != null ?
                params.get("checkResult").toString() : "ok";
            String remark = params.get("remark") != null ?
                params.get("remark").toString() : "";

            Order order = orderService.getById(orderId);
            if (order == null) {
                return Result.error("订单不存在");
            }

            order.setWarehouseStatus(1);
            order.setStatus(9);
            order.setWarehouseConfirmTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            order.setLogisticsProgress("网点已确认收货，货物已入库");
            order.setUpdateTime(new Date());
            orderService.updateById(order);

            orderTimelineService.recordTimeline(
                order.getOrderNo(),
                networkId,
                "NETWORK",
                "NETWORK_CONFIRMED",
                "网点已确认",
                "网点确认收货"
            );

            Map<String, Object> result = new HashMap<>();
            result.put("orderId", orderId);
            result.put("networkId", networkId);
            result.put("checkResult", checkResult);
            result.put("message", "网点已确认收货");
            result.put("warehouseStatus", "已入库");

            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("确认收货失败: " + e.getMessage());
        }
    }

    @GetMapping("/orders")
    public Object getNetworkOrders(@RequestParam Long networkId,
                                   @RequestParam(required = false) Integer status) {
        try {
            List<Order> allOrders = orderService.list();
            List<Order> networkOrders = new ArrayList<>();

            for (Order order : allOrders) {
                boolean matchNetwork = order.getSelectedNetworkId() != null &&
                    order.getSelectedNetworkId().equals(networkId);
                boolean matchStatus = status == null ||
                    (order.getStatus() != null && order.getStatus().equals(status));

                if (matchNetwork && matchStatus) {
                    networkOrders.add(order);
                }
            }

            return Result.success(networkOrders);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取网点订单失败: " + e.getMessage());
        }
    }

    @GetMapping("/quotes/pending")
    public Object getPendingQuotes(@RequestParam Long networkId) {
        try {
            List<NetworkQuote> allQuotes = networkQuoteService.list();
            List<NetworkQuote> pendingQuotes = new ArrayList<>();

            for (NetworkQuote quote : allQuotes) {
                if (quote.getNetworkPointId() != null &&
                    quote.getNetworkPointId().equals(networkId) &&
                    quote.getStatus() != null && quote.getStatus() == 0) {
                    pendingQuotes.add(quote);
                }
            }

            return Result.success(pendingQuotes);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取待报价订单失败: " + e.getMessage());
        }
    }
}
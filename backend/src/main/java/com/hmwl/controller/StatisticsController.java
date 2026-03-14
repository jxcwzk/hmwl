package com.hmwl.controller;

import com.hmwl.entity.Driver;
import com.hmwl.entity.OperationStatistics;
import com.hmwl.entity.Order;
import com.hmwl.service.DriverService;
import com.hmwl.service.OperationStatisticsService;
import com.hmwl.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    @Autowired
    private OperationStatisticsService operationStatisticsService;
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private DriverService driverService;

    @GetMapping("/daily")
    public Map<String, Object> getDailyStatistics(@RequestParam(required = false) String date) {
        Map<String, Object> result = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String queryDate = date != null ? date : sdf.format(new Date());
        
        OperationStatistics statistics = operationStatisticsService.getByDate(queryDate);
        if (statistics == null) {
            operationStatisticsService.generateDailyStatistics(queryDate);
            statistics = operationStatisticsService.getByDate(queryDate);
        }
        
        result.put("date", queryDate);
        result.put("statistics", statistics);
        return result;
    }

    @GetMapping("/daily/refresh")
    public Map<String, Object> refreshDailyStatistics(@RequestParam(required = false) String date) {
        Map<String, Object> result = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String queryDate = date != null ? date : sdf.format(new Date());
        
        operationStatisticsService.generateDailyStatistics(queryDate);
        OperationStatistics statistics = operationStatisticsService.getByDate(queryDate);
        
        result.put("date", queryDate);
        result.put("statistics", statistics);
        result.put("message", "统计已更新");
        return result;
    }

    @GetMapping("/driver-ranking")
    public Map<String, Object> getDriverRanking(@RequestParam(defaultValue = "order_count") String type) {
        Map<String, Object> result = new HashMap<>();
        
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNotNull("driver_id");
        List<Order> orders = orderService.list(queryWrapper);
        
        Map<Long, Integer> orderCountMap = new HashMap<>();
        Map<Long, Double> distanceMap = new HashMap<>();
        Map<Long, Double> amountMap = new HashMap<>();
        
        for (Order order : orders) {
            Long driverId = order.getDriverId();
            if (driverId != null) {
                orderCountMap.put(driverId, orderCountMap.getOrDefault(driverId, 0) + 1);
                if (order.getDistance() != null) {
                    distanceMap.put(driverId, distanceMap.getOrDefault(driverId, 0.0) + order.getDistance());
                }
                if (order.getTotalFee() != null) {
                    amountMap.put(driverId, amountMap.getOrDefault(driverId, 0.0) + order.getTotalFee());
                }
            }
        }
        
        List<Map<String, Object>> rankingList = new ArrayList<>();
        List<Driver> drivers = driverService.list();
        
        for (Driver driver : drivers) {
            Map<String, Object> item = new HashMap<>();
            item.put("driverId", driver.getId());
            item.put("driverName", driver.getName());
            item.put("orderCount", orderCountMap.getOrDefault(driver.getId(), 0));
            item.put("totalDistance", distanceMap.getOrDefault(driver.getId(), 0.0));
            item.put("totalAmount", amountMap.getOrDefault(driver.getId(), 0.0));
            rankingList.add(item);
        }
        
        if ("distance".equals(type)) {
            rankingList.sort((a, b) -> Double.compare((Double) b.get("totalDistance"), (Double) a.get("totalDistance")));
        } else if ("amount".equals(type)) {
            rankingList.sort((a, b) -> Double.compare((Double) b.get("totalAmount"), (Double) a.get("totalAmount")));
        } else {
            rankingList.sort((a, b) -> Integer.compare((Integer) b.get("orderCount"), (Integer) a.get("orderCount")));
        }
        
        result.put("type", type);
        result.put("ranking", rankingList);
        return result;
    }

    @GetMapping("/customer-ranking")
    public Map<String, Object> getCustomerRanking(@RequestParam(defaultValue = "order_count") String type) {
        Map<String, Object> result = new HashMap<>();
        
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNotNull("business_user_id");
        List<Order> orders = orderService.list(queryWrapper);
        
        Map<Long, Integer> orderCountMap = new HashMap<>();
        Map<Long, Double> amountMap = new HashMap<>();
        
        for (Order order : orders) {
            Long businessUserId = order.getBusinessUserId();
            if (businessUserId != null) {
                orderCountMap.put(businessUserId, orderCountMap.getOrDefault(businessUserId, 0) + 1);
                if (order.getTotalFee() != null) {
                    amountMap.put(businessUserId, amountMap.getOrDefault(businessUserId, 0.0) + order.getTotalFee());
                }
            }
        }
        
        List<Map<String, Object>> rankingList = new ArrayList<>();
        
        for (Long businessUserId : orderCountMap.keySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("businessUserId", businessUserId);
            item.put("orderCount", orderCountMap.get(businessUserId));
            item.put("totalAmount", amountMap.getOrDefault(businessUserId, 0.0));
            rankingList.add(item);
        }
        
        if ("amount".equals(type)) {
            rankingList.sort((a, b) -> Double.compare((Double) b.get("totalAmount"), (Double) a.get("totalAmount")));
        } else {
            rankingList.sort((a, b) -> Integer.compare((Integer) b.get("orderCount"), (Integer) a.get("orderCount")));
        }
        
        result.put("type", type);
        result.put("ranking", rankingList);
        return result;
    }

    @GetMapping("/revenue")
    public Map<String, Object> getRevenueStatistics(@RequestParam(defaultValue = "daily") String period) {
        Map<String, Object> result = new HashMap<>();
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        List<Map<String, Object>> trendData = new ArrayList<>();
        
        int days = "monthly".equals(period) ? 30 : ("weekly".equals(period) ? 7 : 30);
        
        for (int i = days - 1; i >= 0; i--) {
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_MONTH, -i);
            String date = sdf.format(calendar.getTime());
            
            OperationStatistics statistics = operationStatisticsService.getByDate(date);
            if (statistics == null) {
                operationStatisticsService.generateDailyStatistics(date);
                statistics = operationStatisticsService.getByDate(date);
            }
            
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", date);
            dayData.put("totalAmount", statistics != null ? statistics.getTotalAmount() : 0);
            dayData.put("orderCount", statistics != null ? statistics.getTotalOrders() : 0);
            trendData.add(dayData);
        }
        
        double totalRevenue = trendData.stream()
            .mapToDouble(m -> (Double) m.get("totalAmount"))
            .sum();
        
        result.put("period", period);
        result.put("trendData", trendData);
        result.put("totalRevenue", totalRevenue);
        result.put("avgDailyRevenue", totalRevenue / days);
        return result;
    }
}

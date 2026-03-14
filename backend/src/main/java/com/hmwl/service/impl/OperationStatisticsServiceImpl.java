package com.hmwl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmwl.entity.Driver;
import com.hmwl.entity.OperationStatistics;
import com.hmwl.entity.Order;
import com.hmwl.entity.User;
import com.hmwl.mapper.OperationStatisticsMapper;
import com.hmwl.service.OperationStatisticsService;
import com.hmwl.service.OrderService;
import com.hmwl.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class OperationStatisticsServiceImpl extends ServiceImpl<OperationStatisticsMapper, OperationStatistics> implements OperationStatisticsService {

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private UserService userService;

    @Override
    public OperationStatistics getByDate(String date) {
        return baseMapper.selectByDate(date);
    }

    @Override
    public void generateDailyStatistics(String date) {
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date queryDate;
        try {
            queryDate = sdf.parse(date);
        } catch (Exception e) {
            queryDate = new Date();
        }
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(queryDate);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Date yesterday = calendar.getTime();
        String yesterdayStr = sdf.format(yesterday);
        
        queryWrapper.eq("DATE(create_time)", date);
        List<Order> todayOrders = orderService.list(queryWrapper);
        
        QueryWrapper<Order> yesterdayWrapper = new QueryWrapper<>();
        yesterdayWrapper.eq("DATE(create_time)", yesterdayStr);
        List<Order> yesterdayOrders = orderService.list(yesterdayWrapper);
        
        OperationStatistics statistics = getByDate(date);
        if (statistics == null) {
            statistics = new OperationStatistics();
            statistics.setStatDate(date);
            statistics.setStatType("daily");
        }
        
        statistics.setTotalOrders(todayOrders.size());
        
        int completed = 0;
        int pending = 0;
        int delivering = 0;
        double totalAmount = 0;
        
        for (Order order : todayOrders) {
            if (order.getStatus() != null) {
                if (order.getStatus() == 5) {
                    completed++;
                } else if (order.getStatus() == 0) {
                    pending++;
                } else if (order.getStatus() == 3 || order.getStatus() == 4) {
                    delivering++;
                }
            }
            if (order.getTotalFee() != null) {
                totalAmount += order.getTotalFee();
            }
        }
        
        statistics.setCompletedOrders(completed);
        statistics.setPendingOrders(pending);
        statistics.setDeliveringOrders(delivering);
        statistics.setTotalAmount(totalAmount);
        statistics.setAvgOrderAmount(todayOrders.size() > 0 ? totalAmount / todayOrders.size() : 0.0);
        
        Set<Long> customerIds = new HashSet<>();
        for (Order order : todayOrders) {
            if (order.getBusinessUserId() != null) {
                customerIds.add(order.getBusinessUserId());
            }
        }
        statistics.setActiveCustomers(customerIds.size());
        
        Set<Long> driverIds = new HashSet<>();
        for (Order order : todayOrders) {
            if (order.getDriverId() != null) {
                driverIds.add(order.getDriverId());
            }
        }
        statistics.setActiveDrivers(driverIds.size());
        
        if (yesterdayOrders.size() > 0) {
            double growth = ((double) todayOrders.size() - yesterdayOrders.size()) / yesterdayOrders.size() * 100;
            statistics.setDayOverDayGrowth(Math.round(growth * 100.0) / 100.0);
        } else {
            statistics.setDayOverDayGrowth(0.0);
        }
        
        saveOrUpdate(statistics);
    }
}

package com.hmwl.controller;

import com.hmwl.entity.CustomerProfile;
import com.hmwl.entity.Order;
import com.hmwl.service.CustomerProfileService;
import com.hmwl.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/customer-profile")
public class CustomerProfileController {

    @Autowired
    private CustomerProfileService customerProfileService;
    
    @Autowired
    private OrderService orderService;

    @GetMapping("/list")
    public List<CustomerProfile> list() {
        return customerProfileService.list();
    }

    @GetMapping("/{id}")
    public CustomerProfile getById(@PathVariable Long id) {
        return customerProfileService.getById(id);
    }

    @GetMapping("/by-business-user/{businessUserId}")
    public CustomerProfile getByBusinessUserId(@PathVariable Long businessUserId) {
        return customerProfileService.getByBusinessUserId(businessUserId);
    }

    @PostMapping
    public boolean save(@RequestBody CustomerProfile customerProfile) {
        if (customerProfile.getTotalOrders() == null) {
            customerProfile.setTotalOrders(0);
        }
        if (customerProfile.getTotalAmount() == null) {
            customerProfile.setTotalAmount(0.0);
        }
        if (customerProfile.getCreditRating() == null) {
            customerProfile.setCreditRating(3);
        }
        if (customerProfile.getStatus() == null) {
            customerProfile.setStatus(1);
        }
        return customerProfileService.save(customerProfile);
    }

    @PutMapping
    public boolean update(@RequestBody CustomerProfile customerProfile) {
        return customerProfileService.updateById(customerProfile);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return customerProfileService.removeById(id);
    }

    @GetMapping("/{id}/history")
    public Map<String, Object> getHistory(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        CustomerProfile profile = customerProfileService.getById(id);
        if (profile != null && profile.getBusinessUserId() != null) {
            QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("business_user_id", profile.getBusinessUserId());
            queryWrapper.orderByDesc("create_time");
            List<Order> orders = orderService.list(queryWrapper);
            result.put("orders", orders);
            result.put("totalCount", orders.size());
        } else {
            result.put("orders", List.of());
            result.put("totalCount", 0);
        }
        return result;
    }

    @GetMapping("/{id}/statistics")
    public Map<String, Object> getStatistics(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        CustomerProfile profile = customerProfileService.getById(id);
        if (profile != null) {
            customerProfileService.updateStatistics(profile.getBusinessUserId());
            profile = customerProfileService.getById(id);
        }
        result.put("profile", profile);
        return result;
    }

    @GetMapping("/ranking/by-orders")
    public List<CustomerProfile> rankingByOrders() {
        return customerProfileService.listOrderByTotalOrders();
    }

    @GetMapping("/ranking/by-amount")
    public List<CustomerProfile> rankingByAmount() {
        return customerProfileService.listOrderByTotalAmount();
    }

    @PostMapping("/{id}/update-statistics")
    public boolean updateStatistics(@PathVariable Long id) {
        CustomerProfile profile = customerProfileService.getById(id);
        if (profile != null && profile.getBusinessUserId() != null) {
            customerProfileService.updateStatistics(profile.getBusinessUserId());
            return true;
        }
        return false;
    }
}

package com.hmwl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmwl.entity.CustomerProfile;
import com.hmwl.entity.Order;
import com.hmwl.mapper.CustomerProfileMapper;
import com.hmwl.mapper.OrderMapper;
import com.hmwl.service.CustomerProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CustomerProfileServiceImpl extends ServiceImpl<CustomerProfileMapper, CustomerProfile> implements CustomerProfileService {

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public CustomerProfile getByBusinessUserId(Long businessUserId) {
        return baseMapper.selectByBusinessUserId(businessUserId);
    }

    @Override
    public List<CustomerProfile> listOrderByTotalAmount() {
        return baseMapper.selectOrderByTotalAmount();
    }

    @Override
    public List<CustomerProfile> listOrderByTotalOrders() {
        return baseMapper.selectOrderByTotalOrders();
    }

    @Override
    public void updateStatistics(Long businessUserId) {
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("business_user_id", businessUserId);
        List<Order> orders = orderMapper.selectList(queryWrapper);
        
        CustomerProfile profile = getByBusinessUserId(businessUserId);
        if (profile != null) {
            profile.setTotalOrders(orders.size());
            double totalAmount = 0;
            for (Order order : orders) {
                if (order.getTotalFee() != null) {
                    totalAmount += order.getTotalFee();
                }
            }
            profile.setTotalAmount(totalAmount);
            updateById(profile);
        }
    }
}

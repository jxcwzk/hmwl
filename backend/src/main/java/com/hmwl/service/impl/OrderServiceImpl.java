package com.hmwl.service.impl;

import com.hmwl.entity.Order;
import com.hmwl.mapper.OrderMapper;
import com.hmwl.service.OrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Override
    @Transactional
    public boolean save(Order order) {
        // 生成订单编号
        if (order.getOrderNo() == null || order.getOrderNo().isEmpty()) {
            String orderNo = generateOrderNo();
            order.setOrderNo(orderNo);
        }
        return super.save(order);
    }

    /**
     * 生成订单编号
     * 格式：HM + YYYYMMDD + 6位序列号
     */
    private String generateOrderNo() {
        // 1. 生成日期部分
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String datePart = sdf.format(new Date());
        
        // 2. 生成序列号部分
        // 构建查询条件：当天的订单
        String prefix = "HM" + datePart;
        
        // 查询当天已存在的订单数量
        List<Order> todayOrders = baseMapper.selectList(
            new QueryWrapper<Order>()
                .likeRight("order_no", prefix)
        );
        
        int sequence = todayOrders.size() + 1;
        String sequencePart = String.format("%06d", sequence);
        
        // 3. 组合订单编号
        return prefix + sequencePart;
    }
}

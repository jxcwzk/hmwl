package com.hmwl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hmwl.entity.Order;
import com.hmwl.entity.Settlement;
import com.hmwl.mapper.SettlementMapper;
import com.hmwl.service.OrderService;
import com.hmwl.service.SettlementService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

@Service
public class SettlementServiceImpl extends ServiceImpl<SettlementMapper, Settlement> implements SettlementService {

    private static final double PROFIT_RATE = 0.30;
    private static final double PRICE_MULTIPLIER = 1.4286;

    @Autowired
    private OrderService orderService;

    @Override
    @Transactional
    public Settlement createFromOrder(Long orderId) {
        Order order = orderService.getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在: " + orderId);
        }

        QueryWrapper<Settlement> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id", orderId);
        Long existCount = this.count(wrapper);
        if (existCount > 0) {
            throw new RuntimeException("该订单已存在结算记录");
        }

        Settlement settlement = new Settlement();
        settlement.setOrderId(orderId);
        settlement.setOrderNo(order.getOrderNo());
        settlement.setCustomerId(order.getBusinessUserId());
        settlement.setOrderAmount(order.getTotalFee());
        settlement.setRecommendedPrice(calculateRecommended(order.getTotalFee()));
        settlement.setFinalAmount(calculateRecommended(order.getTotalFee()));
        settlement.setAmount(calculateRecommended(order.getTotalFee()));
        settlement.setStatus(Settlement.STATUS_PENDING);
        settlement.setType(0);
        settlement.setSettlementNo("ST" + System.currentTimeMillis());
        settlement.setCreateTime(new Date());
        settlement.setUpdateTime(new Date());

        this.save(settlement);
        return settlement;
    }

    private Double calculateRecommended(Double orderAmount) {
        if (orderAmount == null || orderAmount <= 0) {
            return 0.0;
        }
        return orderAmount * PRICE_MULTIPLIER;
    }

    @Override
    public Settlement calculateRecommendedPrice(Settlement settlement) {
        if (settlement.getOrderAmount() != null && settlement.getOrderAmount() > 0) {
            Double recommended = calculateRecommended(settlement.getOrderAmount());
            settlement.setRecommendedPrice(recommended);
            if (settlement.getFinalAmount() == null) {
                settlement.setFinalAmount(recommended);
                settlement.setAmount(recommended);
            }
        }
        return settlement;
    }

    @Override
    public boolean updateFinalAmount(Long settlementId, Double finalAmount) {
        Settlement settlement = this.getById(settlementId);
        if (settlement == null) {
            return false;
        }
        settlement.setFinalAmount(finalAmount);
        settlement.setAmount(finalAmount);
        settlement.setUpdateTime(new Date());
        return this.updateById(settlement);
    }

    @Override
    public boolean updateStatus(Long settlementId, Integer status) {
        Settlement settlement = this.getById(settlementId);
        if (settlement == null) {
            return false;
        }
        settlement.setStatus(status);
        settlement.setUpdateTime(new Date());
        return this.updateById(settlement);
    }
}

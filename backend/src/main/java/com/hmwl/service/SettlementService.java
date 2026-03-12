package com.hmwl.service;

import com.hmwl.entity.Settlement;
import com.baomidou.mybatisplus.extension.service.IService;

public interface SettlementService extends IService<Settlement> {
    Settlement createFromOrder(Long orderId);
    
    Settlement calculateRecommendedPrice(Settlement settlement);
    
    boolean updateFinalAmount(Long settlementId, Double finalAmount);
    
    boolean updateStatus(Long settlementId, Integer status);
}

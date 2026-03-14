package com.hmwl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hmwl.entity.CustomerProfile;
import java.util.List;

public interface CustomerProfileService extends IService<CustomerProfile> {
    
    CustomerProfile getByBusinessUserId(Long businessUserId);
    
    List<CustomerProfile> listOrderByTotalAmount();
    
    List<CustomerProfile> listOrderByTotalOrders();
    
    void updateStatistics(Long businessUserId);
}

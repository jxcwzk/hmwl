package com.hmwl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hmwl.entity.OrderAssignHistory;
import java.util.List;

public interface OrderAssignHistoryService extends IService<OrderAssignHistory> {
    
    List<OrderAssignHistory> getByOrderId(Long orderId);
    
    List<OrderAssignHistory> getRecent(Integer limit);
    
    boolean saveAssignRecord(Long orderId, String orderNo, Long driverId, String driverName, 
                            Long operatorId, String operatorName, Integer assignType, String assignReason);
}

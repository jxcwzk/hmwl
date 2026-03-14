package com.hmwl.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmwl.entity.OrderAssignHistory;
import com.hmwl.mapper.OrderAssignHistoryMapper;
import com.hmwl.service.OrderAssignHistoryService;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

@Service
public class OrderAssignHistoryServiceImpl extends ServiceImpl<OrderAssignHistoryMapper, OrderAssignHistory> implements OrderAssignHistoryService {

    @Override
    public List<OrderAssignHistory> getByOrderId(Long orderId) {
        return baseMapper.selectByOrderId(orderId);
    }

    @Override
    public List<OrderAssignHistory> getRecent(Integer limit) {
        return baseMapper.selectRecent(limit);
    }

    @Override
    public boolean saveAssignRecord(Long orderId, String orderNo, Long driverId, String driverName,
                                   Long operatorId, String operatorName, Integer assignType, String assignReason) {
        OrderAssignHistory record = new OrderAssignHistory();
        record.setOrderId(orderId);
        record.setOrderNo(orderNo);
        record.setDriverId(driverId);
        record.setDriverName(driverName);
        record.setOperatorId(operatorId);
        record.setOperatorName(operatorName);
        record.setAssignType(assignType);
        record.setAssignReason(assignReason);
        record.setCreateTime(new Date());
        return save(record);
    }
}

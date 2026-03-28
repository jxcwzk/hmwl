package com.hmwl.service.impl;

import com.hmwl.entity.OrderTimeline;
import com.hmwl.mapper.OrderTimelineMapper;
import com.hmwl.service.OrderTimelineService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Date;

@Service
public class OrderTimelineServiceImpl extends ServiceImpl<OrderTimelineMapper, OrderTimeline> implements OrderTimelineService {

    @Override
    public void recordTimeline(String orderNo, Long operatorId, String operatorType, String statusCode, String statusName, String remark) {
        OrderTimeline timeline = new OrderTimeline();
        timeline.setOrderNo(orderNo);
        timeline.setOperatorId(operatorId != null ? operatorId : 0L);
        timeline.setOperatorType(operatorType != null ? operatorType : "SYSTEM");
        timeline.setStatusCode(statusCode);
        timeline.setStatusName(statusName);
        timeline.setOperateTime(new Date());
        timeline.setRemark(remark);
        timeline.setCreateTime(new Date());
        this.save(timeline);
    }

    @Override
    public List<OrderTimeline> getTimelineByOrderNo(String orderNo) {
        QueryWrapper<OrderTimeline> wrapper = new QueryWrapper<>();
        wrapper.eq("order_no", orderNo);
        wrapper.orderBy(true, true, "operate_time");
        return this.list(wrapper);
    }
}

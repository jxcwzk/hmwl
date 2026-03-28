package com.hmwl.service;

import com.hmwl.entity.OrderTimeline;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

public interface OrderTimelineService extends IService<OrderTimeline> {
    void recordTimeline(String orderNo, Long operatorId, String operatorType, String statusCode, String statusName, String remark);
    List<OrderTimeline> getTimelineByOrderNo(String orderNo);
}

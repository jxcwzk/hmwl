package com.hmwl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hmwl.entity.OperationStatistics;

public interface OperationStatisticsService extends IService<OperationStatistics> {
    
    OperationStatistics getByDate(String date);
    
    void generateDailyStatistics(String date);
}

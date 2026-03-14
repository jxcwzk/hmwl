package com.hmwl.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hmwl.entity.OperationStatistics;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface OperationStatisticsMapper extends BaseMapper<OperationStatistics> {
    
    @Select("SELECT * FROM operation_statistics WHERE stat_date = #{date} LIMIT 1")
    OperationStatistics selectByDate(@Param("date") String date);
}

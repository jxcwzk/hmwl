package com.hmwl.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hmwl.entity.OrderAssignHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface OrderAssignHistoryMapper extends BaseMapper<OrderAssignHistory> {
    
    @Select("SELECT * FROM order_assign_history WHERE order_id = #{orderId} ORDER BY create_time DESC")
    List<OrderAssignHistory> selectByOrderId(@Param("orderId") Long orderId);
    
    @Select("SELECT * FROM order_assign_history ORDER BY create_time DESC LIMIT #{limit}")
    List<OrderAssignHistory> selectRecent(@Param("limit") Integer limit);
}

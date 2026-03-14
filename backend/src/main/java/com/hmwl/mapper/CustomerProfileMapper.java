package com.hmwl.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hmwl.entity.CustomerProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CustomerProfileMapper extends BaseMapper<CustomerProfile> {
    
    @Select("SELECT * FROM customer_profile WHERE business_user_id = #{businessUserId}")
    CustomerProfile selectByBusinessUserId(@Param("businessUserId") Long businessUserId);
    
    @Select("SELECT * FROM customer_profile ORDER BY total_amount DESC")
    List<CustomerProfile> selectOrderByTotalAmount();
    
    @Select("SELECT * FROM customer_profile ORDER BY total_orders DESC")
    List<CustomerProfile> selectOrderByTotalOrders();
}

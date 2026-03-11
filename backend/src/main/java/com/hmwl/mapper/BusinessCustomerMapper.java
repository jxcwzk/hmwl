package com.hmwl.mapper;

import com.hmwl.entity.BusinessCustomer;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface BusinessCustomerMapper extends BaseMapper<BusinessCustomer> {
    List<BusinessCustomer> selectByBusinessUserId(Long businessUserId);
    List<BusinessCustomer> selectByBusinessUserIdAndType(Long businessUserId, Integer type);
}
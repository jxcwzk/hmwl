package com.hmwl.service.impl;

import com.hmwl.entity.BusinessCustomer;
import com.hmwl.mapper.BusinessCustomerMapper;
import com.hmwl.service.BusinessCustomerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BusinessCustomerServiceImpl extends ServiceImpl<BusinessCustomerMapper, BusinessCustomer> implements BusinessCustomerService {
    @Override
    public List<BusinessCustomer> listByBusinessUserId(Long businessUserId) {
        return baseMapper.selectByBusinessUserId(businessUserId);
    }
    
    @Override
    public List<BusinessCustomer> listByBusinessUserIdAndType(Long businessUserId, Integer type) {
        return baseMapper.selectByBusinessUserIdAndType(businessUserId, type);
    }
}
package com.hmwl.service;

import com.hmwl.entity.BusinessCustomer;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface BusinessCustomerService extends IService<BusinessCustomer> {
    List<BusinessCustomer> listByBusinessUserId(Long businessUserId);
    List<BusinessCustomer> listByBusinessUserIdAndType(Long businessUserId, Integer type);
}
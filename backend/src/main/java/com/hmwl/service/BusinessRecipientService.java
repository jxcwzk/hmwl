package com.hmwl.service;

import com.hmwl.entity.BusinessRecipient;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface BusinessRecipientService extends IService<BusinessRecipient> {
    List<BusinessRecipient> listByBusinessUserId(Long businessUserId);
}
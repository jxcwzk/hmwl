package com.hmwl.mapper;

import com.hmwl.entity.BusinessRecipient;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface BusinessRecipientMapper extends BaseMapper<BusinessRecipient> {
    List<BusinessRecipient> selectByBusinessUserId(Long businessUserId);
}
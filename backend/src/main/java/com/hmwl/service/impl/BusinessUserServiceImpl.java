package com.hmwl.service.impl;

import com.hmwl.entity.BusinessUser;
import com.hmwl.mapper.BusinessUserMapper;
import com.hmwl.service.BusinessUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class BusinessUserServiceImpl extends ServiceImpl<BusinessUserMapper, BusinessUser> implements BusinessUserService {
}
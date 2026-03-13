package com.hmwl.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmwl.entity.Role;
import com.hmwl.mapper.RoleMapper;
import com.hmwl.service.RoleService;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
}

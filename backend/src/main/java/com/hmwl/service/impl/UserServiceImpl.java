package com.hmwl.service.impl;

import com.hmwl.entity.User;
import com.hmwl.mapper.UserMapper;
import com.hmwl.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
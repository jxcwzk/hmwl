package com.hmwl.service;

import com.hmwl.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

public interface UserService extends IService<User> {

    User wxLogin(String code);

    User getUserByOpenid(String openid);

    boolean updateUserRole(Long userId, Integer userType, Integer status);
}

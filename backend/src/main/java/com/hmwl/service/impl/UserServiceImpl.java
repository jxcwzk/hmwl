package com.hmwl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmwl.entity.BusinessUser;
import com.hmwl.entity.Driver;
import com.hmwl.entity.User;
import com.hmwl.mapper.BusinessUserMapper;
import com.hmwl.mapper.DriverMapper;
import com.hmwl.mapper.UserMapper;
import com.hmwl.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Value("${wechat.miniprogram.appid}")
    private String appid;

    @Value("${wechat.miniprogram.secret}")
    private String secret;

    @Autowired
    private BusinessUserMapper businessUserMapper;

    @Autowired
    private DriverMapper driverMapper;

    @Override
    public User login(String username, String password) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        wrapper.eq(User::getStatus, 1);
        wrapper.last("LIMIT 1");
        User user = this.getOne(wrapper);
        
        if (user == null) {
            wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getUsername, username);
            wrapper.last("LIMIT 1");
            user = this.getOne(wrapper);
        }
        
        if (user == null) {
            return null;
        }
        
        if (user.getPassword() != null && user.getPassword().equals(password)) {
            return user;
        }
        
        return null;
    }

    @Override
    public User wxLogin(String code) {
        System.out.println("========== 微信登录开始 ==========");
        System.out.println("AppID: " + appid);
        System.out.println("Secret: " + (secret != null && !secret.isEmpty() ? "已配置" : "未配置"));
        System.out.println("Code: " + code);
        
        String openid = getOpenidFromWechat(code);

        if (openid == null) {
            System.out.println("========== 获取openid失败 ==========");
            return null;
        }

        System.out.println("Openid: " + openid);
        
        User user = getUserByOpenid(openid);

        if (user == null) {
            user = new User();
            user.setOpenid(openid);
            user.setUsername("wx_user_" + openid.substring(0, 8));
            user.setCreateTime(new Date());
            user.setUpdateTime(new Date());
            user.setStatus(0);
            user.setUserType(0);
            this.save(user);
        }

        return user;
    }

    @Override
    public User getUserByOpenid(String openid) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getOpenid, openid);
        return this.getOne(wrapper);
    }

    @Override
    @Transactional
    public boolean updateUserRole(Long userId, Integer userType, Integer status) {
        User user = this.getById(userId);
        if (user == null) {
            return false;
        }

        if (userType == 2 && status == 1) {
            if (user.getBusinessUserId() == null) {
                BusinessUser businessUser = new BusinessUser();
                businessUser.setUsername(user.getUsername());
                businessUser.setPhone(user.getPhone());
                businessUser.setWechat(user.getWechat());
                businessUser.setRemark("小程序用户自动创建");
                businessUser.setCreateTime(new Date());
                businessUser.setUpdateTime(new Date());
                businessUserMapper.insert(businessUser);
                
                user.setBusinessUserId(businessUser.getId());
            }
        } else if (userType == 3 && status == 1) {
            if (user.getDriverId() == null) {
                Driver driver = new Driver();
                driver.setName(user.getUsername());
                driver.setPhone(user.getPhone());
                driver.setCreateTime(new Date());
                driver.setUpdateTime(new Date());
                driverMapper.insert(driver);
                
                user.setDriverId(driver.getId());
            }
        } else if (userType == 4 && status == 1) {
            // 网点角色，不需要自动创建网点记录，因为网点应该在系统中已经存在
            // 网点管理员需要手动关联到具体的网点
        }

        user.setUserType(userType);
        user.setStatus(status);
        user.setUpdateTime(new Date());
        return this.updateById(user);
    }

    private String getOpenidFromWechat(String code) {
        try {
            String url = String.format(
                "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                appid, secret, code
            );

            System.out.println("微信API URL: " + url);

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
            String responseStr = restTemplate.getForObject(url, String.class);
            
            System.out.println("微信返回原始: " + responseStr);
            
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            Map<String, Object> response = mapper.readValue(responseStr, Map.class);

            System.out.println("微信返回: " + response);

            if (response != null && response.get("openid") != null) {
                return (String) response.get("openid");
            } else if (response != null) {
                System.out.println("微信登录失败: " + response.get("errcode") + " - " + response.get("errmsg"));
            }
        } catch (Exception e) {
            System.out.println("调用微信API异常: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}

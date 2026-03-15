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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import org.mindrot.jbcrypt.BCrypt;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

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

        if (user.getPassword() != null && BCrypt.checkpw(password, user.getPassword())) {
            return user;
        }
        
        return null;
    }

    @Override
    public User wxLogin(String code) {
        logger.info("========== 微信登录开始 ==========");
        
        String openid = getOpenidFromWechat(code);

        if (openid == null) {
            logger.warn("========== 获取openid失败 ==========");
            return null;
        }

        logger.info("微信登录成功, openid: {}", openid.substring(0, 8) + "****");
        
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
                "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=******&js_code=%s&grant_type=authorization_code",
                appid, code);

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
            String responseStr = restTemplate.getForObject(url, String.class);
            
            logger.debug("微信返回原始: [已脱敏]");
            
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            Map<String, Object> response = mapper.readValue(responseStr, Map.class);

            if (response != null && response.get("openid") != null) {
                return (String) response.get("openid");
            } else if (response != null) {
                logger.warn("微信登录失败: errcode={}, errmsg={}", response.get("errcode"), response.get("errmsg"));
            }
        } catch (Exception e) {
            logger.error("调用微信API异常", e);
        }
        return null;
    }
}

package com.hmwl.controller;

import com.hmwl.entity.BusinessCustomer;
import com.hmwl.entity.BusinessUser;
import com.hmwl.entity.User;
import com.hmwl.mapper.BusinessCustomerMapper;
import com.hmwl.mapper.BusinessUserMapper;
import com.hmwl.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/miniprogram")
public class MiniprogramController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BusinessUserMapper businessUserMapper;

    @Autowired
    private BusinessCustomerMapper businessCustomerMapper;

    @GetMapping("/myInfo")
    public Result getMyInfo(@RequestParam Long userId) {
        Map<String, Object> data = new HashMap<>();
        
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        data.put("userId", user.getId());
        data.put("username", user.getUsername());
        data.put("userType", user.getUserType());
        data.put("status", user.getStatus());
        data.put("businessUserId", user.getBusinessUserId());
        data.put("driverId", user.getDriverId());
        
        if (user.getBusinessUserId() != null) {
            BusinessUser businessUser = businessUserMapper.selectById(user.getBusinessUserId());
            if (businessUser != null) {
                data.put("businessUser", businessUser);
            }
        }
        
        return Result.success(data);
    }

    @GetMapping("/senders")
    public Result getSenders(@RequestParam Long businessUserId) {
        List<BusinessCustomer> senders = businessCustomerMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<BusinessCustomer>()
                .eq(BusinessCustomer::getBusinessUserId, businessUserId)
                .eq(BusinessCustomer::getType, 0)
        );
        
        return Result.success(senders);
    }

    @GetMapping("/recipients")
    public Result getRecipients(@RequestParam Long businessUserId) {
        List<BusinessCustomer> recipients = businessCustomerMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<BusinessCustomer>()
                .eq(BusinessCustomer::getBusinessUserId, businessUserId)
                .eq(BusinessCustomer::getType, 1)
        );
        
        return Result.success(recipients);
    }

    @PostMapping("/sender")
    public Result addSender(@RequestBody BusinessCustomer sender) {
        sender.setType(0);
        sender.setCreateTime(new Date());
        sender.setUpdateTime(new Date());
        businessCustomerMapper.insert(sender);
        
        return Result.success(sender);
    }

    @PostMapping("/recipient")
    public Result addRecipient(@RequestBody BusinessCustomer recipient) {
        recipient.setType(1);
        recipient.setCreateTime(new Date());
        recipient.setUpdateTime(new Date());
        businessCustomerMapper.insert(recipient);
        
        return Result.success(recipient);
    }

    @PutMapping("/contact")
    public Result updateContact(@RequestBody BusinessCustomer contact) {
        contact.setUpdateTime(new Date());
        businessCustomerMapper.updateById(contact);
        
        return Result.success(contact);
    }

    @DeleteMapping("/contact/{id}")
    public Result deleteContact(@PathVariable Long id) {
        businessCustomerMapper.deleteById(id);
        
        return Result.success(null);
    }
}

package com.hmwl.controller;

import com.hmwl.entity.BusinessRecipient;
import com.hmwl.service.BusinessRecipientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/test-insert")
public class TestInsertController {
    
    @Autowired
    private BusinessRecipientService businessRecipientService;
    
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
    public BusinessRecipient test() {
        // 创建一个带有中文字符的收件人对象
        BusinessRecipient recipient = new BusinessRecipient();
        recipient.setBusinessUserId(1L);
        recipient.setName("测试收件人");
        recipient.setPhone("13800138000");
        recipient.setAddress("北京市朝阳区");
        recipient.setCreateTime(LocalDateTime.now());
        recipient.setUpdateTime(LocalDateTime.now());
        
        // 保存到数据库
        businessRecipientService.save(recipient);
        
        // 从数据库读取并返回
        return businessRecipientService.getById(recipient.getId());
    }
}

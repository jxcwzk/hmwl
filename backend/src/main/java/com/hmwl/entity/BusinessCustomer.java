package com.hmwl.entity;

import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

@Data
@TableName("business_customer")
public class BusinessCustomer {
    // 类型常量定义
    public static final Integer TYPE_SENDER = 0; // 发件人信息
    public static final Integer TYPE_RECIPIENT = 1; // 收件人信息
    
    private Long id;
    private String customerName;
    private String phone;
    private String address;
    private String remark;
    private Long businessUserId;
    private Integer type;
    private Date createTime;
    private Date updateTime;
}
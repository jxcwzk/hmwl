package com.hmwl.entity;

import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

@Data
@TableName("customer")
public class Customer {
    private Long id;
    private String customerName;
    private String contact;
    private String address;
    private String remark;
    private Long userId;
    private Integer type;
    private Date createTime;
    private Date updateTime;
}
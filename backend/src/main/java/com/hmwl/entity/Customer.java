package com.hmwl.entity;

import lombok.Data;
import java.util.Date;

@Data
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
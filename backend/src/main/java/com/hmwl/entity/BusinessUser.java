package com.hmwl.entity;

import lombok.Data;
import java.util.Date;

@Data
public class BusinessUser {
    private Long id;
    private String username;
    private String phone;
    private String address;
    private String wechat;
    private String remark;
    private Date createTime;
    private Date updateTime;
}
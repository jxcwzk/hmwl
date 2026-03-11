package com.hmwl.entity;

import lombok.Data;
import java.util.Date;

@Data
public class User {
    private Long id;
    private String username;
    private Integer userType;
    private String remark;
    private String phone;
    private String wechat;
    private String password;
    private Date createTime;
    private Date updateTime;
}
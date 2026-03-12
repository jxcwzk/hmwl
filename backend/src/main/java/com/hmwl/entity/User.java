package com.hmwl.entity;

import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

@Data
@TableName("user")
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
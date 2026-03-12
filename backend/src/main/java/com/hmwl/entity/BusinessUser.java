package com.hmwl.entity;

import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

@Data
@TableName("business_user")
public class BusinessUser {
    private Long id;
    private String username;
    private String phone;
    private String wechat;
    private String remark;
    private Date createTime;
    private Date updateTime;
}
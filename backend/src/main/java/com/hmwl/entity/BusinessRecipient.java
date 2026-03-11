package com.hmwl.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("business_recipient")
public class BusinessRecipient {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long businessUserId;
    private String name;
    private String phone;
    private String address;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
package com.hmwl.entity;

import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

@Data
@TableName("network_point")
public class NetworkPoint {
    private Long id;
    private String code;
    private String name;
    private String city;
    private String contactPerson;
    private String phone;
    private String address;
    private Date createTime;
    private Date updateTime;
}

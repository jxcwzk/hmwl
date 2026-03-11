package com.hmwl.entity;

import lombok.Data;
import java.util.Date;

@Data
public class NetworkPoint {
    private Long id;
    private String code;
    private String name;
    private String contactPerson;
    private String phone;
    private String address;
    private Date createTime;
    private Date updateTime;
}

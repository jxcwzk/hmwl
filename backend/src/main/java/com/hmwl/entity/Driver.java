package com.hmwl.entity;

import lombok.Data;
import java.util.Date;

@Data
public class Driver {
    private Long id;
    private String name;
    private String phone;
    private String idCard;
    private Long vehicleId;
    private Date createTime;
    private Date updateTime;
}

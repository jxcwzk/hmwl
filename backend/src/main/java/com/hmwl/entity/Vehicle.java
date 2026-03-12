package com.hmwl.entity;

import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

@Data
@TableName("vehicle")
public class Vehicle {
    private Long id;
    private String licensePlate;
    private String vehicleType;
    private Double loadCapacity;
    private Date createTime;
    private Date updateTime;
}

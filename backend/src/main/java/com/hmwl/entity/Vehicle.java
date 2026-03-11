package com.hmwl.entity;

import lombok.Data;
import java.util.Date;

@Data
public class Vehicle {
    private Long id;
    private String licensePlate;
    private String vehicleType;
    private Double loadCapacity;
    private Date createTime;
    private Date updateTime;
}

package com.hmwl.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
@TableName("network_route")
public class Route {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String startCity;
    private String destinationCity;
    private Double basePrice;
    private Double pricePerKg;
    private Integer transitDays;
    private Date createTime;
    private Date updateTime;

    @TableField(exist = false)
    private List<NetworkPoint> networkPoints;
}

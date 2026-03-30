package com.hmwl.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("route_network_point")
public class RouteNetworkPoint {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long routeId;
    private Long networkPointId;
    private Integer sequence;
    private Date createTime;
    private Date updateTime;
}

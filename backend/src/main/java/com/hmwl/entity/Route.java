package com.hmwl.entity;

import lombok.Data;
import java.util.Date;

@Data
public class Route {
    private Long id;
    private Long startPointId;
    private Long endPointId;
    private Boolean isTrunk;
    private Date createTime;
    private Date updateTime;
}

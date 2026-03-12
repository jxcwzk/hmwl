package com.hmwl.entity;

import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

@Data
@TableName("route")
public class Route {
    private Long id;
    private Long startPointId;
    private Long endPointId;
    private Boolean isTrunk;
    private Date createTime;
    private Date updateTime;
}

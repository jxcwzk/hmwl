package com.hmwl.entity;

import lombok.Data;
import java.util.Date;

@Data
public class BranchSettle {
    private Long id;
    private String settlementNo;
    private Long startNetworkId;
    private Long endNetworkId;
    private Double transferFee;
    private Double trunkFee;
    private Integer status;
    private Date createTime;
    private Date updateTime;
}

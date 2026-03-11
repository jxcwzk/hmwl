package com.hmwl.entity;

import lombok.Data;
import java.util.Date;

@Data
public class Settlement {
    private Long id;
    private String settlementNo;
    private Integer type;
    private Long orderId;
    private Long customerId;
    private Long driverId;
    private Long startNetworkId;
    private Long endNetworkId;
    private Double amount;
    private Integer status;
    private Integer paymentMethod;
    private Double commission;
    private Double transferFee;
    private Double trunkFee;
    private Date createTime;
    private Date updateTime;
}

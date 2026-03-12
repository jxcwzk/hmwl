package com.hmwl.entity;

import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

@Data
@TableName("settlement")
public class Settlement {
    private Long id;
    private String settlementNo;
    private Integer type;
    private Long orderId;
    private String orderNo;
    private Long customerId;
    private String customerName;
    private Long driverId;
    private Long startNetworkId;
    private Long endNetworkId;
    private Double orderAmount;
    private Double recommendedPrice;
    private Double finalAmount;
    private Double amount;
    private Integer status;
    private Integer paymentMethod;
    private Double commission;
    private Double transferFee;
    private Double trunkFee;
    private String invoiceNo;
    private Date createTime;
    private Date updateTime;

    public static final Integer STATUS_PENDING = 0;
    public static final Integer STATUS_CONFIRMED = 1;
    public static final Integer STATUS_INVOICED = 2;
    public static final Integer STATUS_PAID = 3;
}

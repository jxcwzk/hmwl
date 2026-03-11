package com.hmwl.entity;

import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

@Data
@TableName("orders")
public class Order {
    private Long id;
    private String orderNo;
    private Integer orderType;
    private Long startNetworkId;
    private Long endNetworkId;
    private Long businessUserId;
    private Long senderId;
    private Long recipientId;
    private String senderName;
    private String senderPhone;
    private String senderAddress;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private String goodsName;
    private Integer quantity;
    private Double weight;
    private Double volume;
    private Double distance;
    private Double totalFee;
    private Integer paymentMethod;
    private Integer networkPaymentMethod;
    private String qrCodeUrl;
    private Integer status;
    private Date createTime;
    private Date updateTime;
}

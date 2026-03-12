package com.hmwl.entity;

import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

@Data
@TableName("invoice")
public class Invoice {
    private Long id;
    private String invoiceNo;
    private Long settlementId;
    private Long orderId;
    private Long customerId;
    private String customerName;
    private Double amount;
    private Integer status;
    private Date invoiceDate;
    private Date createTime;
    private Date updateTime;
}

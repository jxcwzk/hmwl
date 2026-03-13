package com.hmwl.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSettlementNo() {
        return settlementNo;
    }

    public void setSettlementNo(String settlementNo) {
        this.settlementNo = settlementNo;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public Long getStartNetworkId() {
        return startNetworkId;
    }

    public void setStartNetworkId(Long startNetworkId) {
        this.startNetworkId = startNetworkId;
    }

    public Long getEndNetworkId() {
        return endNetworkId;
    }

    public void setEndNetworkId(Long endNetworkId) {
        this.endNetworkId = endNetworkId;
    }

    public Double getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(Double orderAmount) {
        this.orderAmount = orderAmount;
    }

    public Double getRecommendedPrice() {
        return recommendedPrice;
    }

    public void setRecommendedPrice(Double recommendedPrice) {
        this.recommendedPrice = recommendedPrice;
    }

    public Double getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(Double finalAmount) {
        this.finalAmount = finalAmount;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(Integer paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Double getCommission() {
        return commission;
    }

    public void setCommission(Double commission) {
        this.commission = commission;
    }

    public Double getTransferFee() {
        return transferFee;
    }

    public void setTransferFee(Double transferFee) {
        this.transferFee = transferFee;
    }

    public Double getTrunkFee() {
        return trunkFee;
    }

    public void setTrunkFee(Double trunkFee) {
        this.trunkFee = trunkFee;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}

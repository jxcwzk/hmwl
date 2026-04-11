package com.hmwl.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

@TableName("orders")
public class Order {
    /**
     * 订单状态定义:
     * status - 主状态
     *   0: 订单创建
     *   1: 已派发比价
     *   4: 报价已推送客户
     *   5: 价格已确认
     *   9: 网点已确认收货
     *   13: 订单已完成
     *
     * pricingStatus - 定价状态
     *   0: 待定价
     *   1: 待网点报价
     *   2: 调度已选报价
     *   3: 客户已确认价格
     *   4: 已安排提货
     *   5: 已分配配送
     *   6: 配送中
     */
    private Long id;
    private String orderNo;
    private Integer orderType;
    private Long startNetworkId;
    private Long endNetworkId;
    private Long businessUserId;
    private Long driverId;
    private Long networkPointId;
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
    private Double baseFee;
    private Double coefficient;
    private Double totalFee;
    private Integer paymentMethod;
    private Integer networkPaymentMethod;
    private String qrCodeUrl;
    private Integer status;
    private String logisticsStatus;
    private String logisticsProgress;
    private Date createTime;
    private Date updateTime;
    private Long pickupDriverId;
    private Long deliveryDriverId;
    private Long selectedNetworkId;
    private Integer pricingStatus;
    private Integer warehouseStatus;
    private String priceConfirmedTime;
    private String pickedUpTime;
    private String deliveredToNetworkTime;
    private String warehouseConfirmTime;
    private String deliveryCompletedTime;
    private String receiptPhotos;
    private Integer settlementStatus;
    private Integer receiptConfirmed;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
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

    public Long getBusinessUserId() {
        return businessUserId;
    }

    public void setBusinessUserId(Long businessUserId) {
        this.businessUserId = businessUserId;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public Long getNetworkPointId() {
        return networkPointId;
    }

    public void setNetworkPointId(Long networkPointId) {
        this.networkPointId = networkPointId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(Long recipientId) {
        this.recipientId = recipientId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderPhone() {
        return senderPhone;
    }

    public void setSenderPhone(String senderPhone) {
        this.senderPhone = senderPhone;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Double getBaseFee() {
        return baseFee;
    }

    public void setBaseFee(Double baseFee) {
        this.baseFee = baseFee;
    }

    public Double getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(Double coefficient) {
        this.coefficient = coefficient;
    }

    public Double getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(Double totalFee) {
        this.totalFee = totalFee;
    }

    public Integer getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(Integer paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Integer getNetworkPaymentMethod() {
        return networkPaymentMethod;
    }

    public void setNetworkPaymentMethod(Integer networkPaymentMethod) {
        this.networkPaymentMethod = networkPaymentMethod;
    }

    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getLogisticsStatus() {
        return logisticsStatus;
    }

    public void setLogisticsStatus(String logisticsStatus) {
        this.logisticsStatus = logisticsStatus;
    }

    public String getLogisticsProgress() {
        return logisticsProgress;
    }

    public void setLogisticsProgress(String logisticsProgress) {
        this.logisticsProgress = logisticsProgress;
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

    public Long getPickupDriverId() {
        return pickupDriverId;
    }

    public void setPickupDriverId(Long pickupDriverId) {
        this.pickupDriverId = pickupDriverId;
    }

    public Long getDeliveryDriverId() {
        return deliveryDriverId;
    }

    public void setDeliveryDriverId(Long deliveryDriverId) {
        this.deliveryDriverId = deliveryDriverId;
    }

    public Long getSelectedNetworkId() {
        return selectedNetworkId;
    }

    public void setSelectedNetworkId(Long selectedNetworkId) {
        this.selectedNetworkId = selectedNetworkId;
    }

    public Integer getPricingStatus() {
        return pricingStatus;
    }

    public void setPricingStatus(Integer pricingStatus) {
        this.pricingStatus = pricingStatus;
    }

    public Integer getWarehouseStatus() {
        return warehouseStatus;
    }

    public void setWarehouseStatus(Integer warehouseStatus) {
        this.warehouseStatus = warehouseStatus;
    }

    public String getPriceConfirmedTime() {
        return priceConfirmedTime;
    }

    public void setPriceConfirmedTime(String priceConfirmedTime) {
        this.priceConfirmedTime = priceConfirmedTime;
    }

    public String getPickedUpTime() {
        return pickedUpTime;
    }

    public void setPickedUpTime(String pickedUpTime) {
        this.pickedUpTime = pickedUpTime;
    }

    public String getDeliveredToNetworkTime() {
        return deliveredToNetworkTime;
    }

    public void setDeliveredToNetworkTime(String deliveredToNetworkTime) {
        this.deliveredToNetworkTime = deliveredToNetworkTime;
    }

    public String getWarehouseConfirmTime() {
        return warehouseConfirmTime;
    }

    public void setWarehouseConfirmTime(String warehouseConfirmTime) {
        this.warehouseConfirmTime = warehouseConfirmTime;
    }

    public String getDeliveryCompletedTime() {
        return deliveryCompletedTime;
    }

    public void setDeliveryCompletedTime(String deliveryCompletedTime) {
        this.deliveryCompletedTime = deliveryCompletedTime;
    }

    public String getReceiptPhotos() {
        return receiptPhotos;
    }

    public void setReceiptPhotos(String receiptPhotos) {
        this.receiptPhotos = receiptPhotos;
    }

    public Integer getSettlementStatus() {
        return settlementStatus;
    }

    public void setSettlementStatus(Integer settlementStatus) {
        this.settlementStatus = settlementStatus;
    }

    public Integer getReceiptConfirmed() {
        return receiptConfirmed;
    }

    public void setReceiptConfirmed(Integer receiptConfirmed) {
        this.receiptConfirmed = receiptConfirmed;
    }
}
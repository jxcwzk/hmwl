package com.hmwl.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

@TableName("operation_statistics")
public class OperationStatistics {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String statDate;
    private String statType;
    private Integer totalOrders;
    private Integer completedOrders;
    private Integer pendingOrders;
    private Integer deliveringOrders;
    private Double totalAmount;
    private Double avgOrderAmount;
    private Integer activeCustomers;
    private Integer activeDrivers;
    private Double dayOverDayGrowth;
    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatDate() {
        return statDate;
    }

    public void setStatDate(String statDate) {
        this.statDate = statDate;
    }

    public String getStatType() {
        return statType;
    }

    public void setStatType(String statType) {
        this.statType = statType;
    }

    public Integer getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Integer totalOrders) {
        this.totalOrders = totalOrders;
    }

    public Integer getCompletedOrders() {
        return completedOrders;
    }

    public void setCompletedOrders(Integer completedOrders) {
        this.completedOrders = completedOrders;
    }

    public Integer getPendingOrders() {
        return pendingOrders;
    }

    public void setPendingOrders(Integer pendingOrders) {
        this.pendingOrders = pendingOrders;
    }

    public Integer getDeliveringOrders() {
        return deliveringOrders;
    }

    public void setDeliveringOrders(Integer deliveringOrders) {
        this.deliveringOrders = deliveringOrders;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Double getAvgOrderAmount() {
        return avgOrderAmount;
    }

    public void setAvgOrderAmount(Double avgOrderAmount) {
        this.avgOrderAmount = avgOrderAmount;
    }

    public Integer getActiveCustomers() {
        return activeCustomers;
    }

    public void setActiveCustomers(Integer activeCustomers) {
        this.activeCustomers = activeCustomers;
    }

    public Integer getActiveDrivers() {
        return activeDrivers;
    }

    public void setActiveDrivers(Integer activeDrivers) {
        this.activeDrivers = activeDrivers;
    }

    public Double getDayOverDayGrowth() {
        return dayOverDayGrowth;
    }

    public void setDayOverDayGrowth(Double dayOverDayGrowth) {
        this.dayOverDayGrowth = dayOverDayGrowth;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}

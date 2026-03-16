package com.hmwl.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("network_quote")
public class NetworkQuote {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orderId;
    private Long networkPointId;
    private String networkName;
    private Double baseFee;
    private Double finalPrice;
    private Integer transitDays;
    private String remark;
    private Date quoteTime;
    private Integer status;
    private Date createTime;
    private Date updateTime;
}

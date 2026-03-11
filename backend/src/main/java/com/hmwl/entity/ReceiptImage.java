package com.hmwl.entity;

import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

@Data
@TableName("receipt_image")
public class ReceiptImage {
    private Long id;
    private Long orderId;
    private String imageUrl;
    private Integer status;
    private Date createTime;
    private Date updateTime;
}

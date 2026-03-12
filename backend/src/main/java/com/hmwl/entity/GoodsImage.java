package com.hmwl.entity;

import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

@Data
@TableName("goods_image")
public class GoodsImage {
    private Long id;
    private Long orderId;
    private String imageUrl;
    private Integer status;
    private Date createTime;
    private Date updateTime;
}

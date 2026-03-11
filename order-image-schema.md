# 订单图片存储方案

## 数据库表结构设计

### 订单图片表（order_image）

| 字段名 | 数据类型 | 约束 | 描述 |
|--------|----------|------|------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 图片ID |
| order_id | BIGINT | NOT NULL, FOREIGN KEY | 关联的订单ID |
| image_url | VARCHAR(255) | NOT NULL | 图片URL地址 |
| image_type | INT | NOT NULL | 图片类型：1-回单，2-发货单，3-其他 |
| status | INT | NOT NULL DEFAULT 1 | 状态：1-有效，0-无效 |
| create_time | DATETIME | NOT NULL | 创建时间 |
| update_time | DATETIME | NOT NULL | 更新时间 |

### 索引设计

- **order_id**：添加索引，用于快速查询订单的所有图片
- **image_type**：添加索引，用于按类型查询图片

### SQL建表语句

```sql
CREATE TABLE `order_image` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `order_id` BIGINT NOT NULL,
  `image_url` VARCHAR(255) NOT NULL,
  `image_type` INT NOT NULL,
  `status` INT NOT NULL DEFAULT 1,
  `create_time` DATETIME NOT NULL,
  `update_time` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_order_id` (`order_id`),
  INDEX `idx_image_type` (`image_type`),
  FOREIGN KEY (`order_id`) REFERENCES `order` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

## 实体类设计

### OrderImage.java

```java
package com.hmwl.entity;

import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

@Data
@TableName("order_image")
public class OrderImage {
    private Long id;
    private Long orderId;
    private String imageUrl;
    private Integer imageType;
    private Integer status;
    private Date createTime;
    private Date updateTime;
}
```

## 图片类型枚举

```java
public enum ImageType {
    RECEIPT(1, "回单"),
    INVOICE(2, "发货单"),
    OTHER(3, "其他");

    private final int code;
    private final String name;

    ImageType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
```
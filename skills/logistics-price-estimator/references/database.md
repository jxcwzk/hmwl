# 物流估价数据库配置

## 现有表结构

### network_point (网点表)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键 |
| code | varchar | 网点编码 |
| name | varchar | 网点名称 |
| contact_person | varchar | 联系人 |
| phone | varchar | 电话 |
| address | varchar | 地址 |

### route (线路表)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键 |
| start_point_id | bigint | 起始网点ID |
| end_point_id | bigint | 目的网点ID |
| is_trunk | boolean | 是否为干线 |
| distance | int | 距离(公里) |

### orders (订单表)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键 |
| order_no | varchar | 订单编号 |
| order_type | int | 订单类型(0零担/1整车) |
| start_network_id | bigint | 起始网点 |
| end_network_id | bigint | 目的网点 |
| weight | double | 重量(kg) |
| volume | double | 体积(m³) |
| total_fee | decimal | 总费用 |

## 建议新增表

### price_config (价格配置表)
```sql
CREATE TABLE IF NOT EXISTS `price_config` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL COMMENT '配置名称',
  `base_price` decimal(10,2) DEFAULT 10 COMMENT '起步价',
  `price_per_kg` decimal(10,2) DEFAULT 1.5 COMMENT '每公斤单价',
  `price_per_volume` decimal(10,2) DEFAULT 80 COMMENT '每立方米单价',
  `price_per_km` decimal(10,2) DEFAULT 0.5 COMMENT '每公里单价',
  `min_price` decimal(10,2) DEFAULT 20 COMMENT '最低收费',
  `is_trunk` tinyint DEFAULT 0 COMMENT '是否干线',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### route_distance (线路距离表)
```sql
CREATE TABLE IF NOT EXISTS `route_distance` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `start_network_id` bigint DEFAULT NULL,
  `end_network_id` bigint DEFAULT NULL,
  `distance` int DEFAULT 0 COMMENT '距离(公里)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_start` (`start_network_id`),
  KEY `idx_end` (`end_network_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

## API 端点

- `GET /api/network-point/list` - 获取网点列表
- `GET /api/route/list` - 获取线路列表
- `POST /api/price-calculate` - 计算价格

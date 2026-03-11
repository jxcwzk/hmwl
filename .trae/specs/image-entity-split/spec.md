# 订单图片实体拆分规格说明

## Why
前端订单详情页面新增了两个图片上传卡片：
1. 回单图片（imageType=1）- 已有后端对应
2. 发货单图片（imageType=2）- 缺少后端对应实体

需要拆分现有的 OrderImage 实体为两个独立实体：ReceiptImage（回单图片）和 SenderImage（发货单图片）。

## What Changes
- OrderImage → ReceiptImage（重命名，保留 imageType=1 的功能）
- 新增 SenderImage 实体类（对应 imageType=2 的发货单图片）
- 对应更新数据库表结构

## Impact
- 受影响的实体类：
  - `com.hmwl.entity.OrderImage` → `com.hmwl.entity.ReceiptImage`
  - `com.hmwl.entity.SenderImage`（新增）
- 受影响的数据库表：
  - `order_image` → `receipt_image`
  - `sender_image`（新增）

---

## ADDED Requirements

### Requirement: 发货单图片实体

#### Scenario: 上传发货单图片
- **WHEN** 用户在订单详情页面上传发货单图片
- **THEN** 图片信息应保存到 sender_image 表

#### Scenario: 查看发货单图片
- **WHEN** 用户在订单详情页面查看已上传的发货单图片
- **THEN** 应从 sender_image 表查询并展示

---

## 实体设计

### ReceiptImage（回单图片）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 主键 |
| orderId | Long | 订单ID |
| imageUrl | String | 图片URL |
| status | Integer | 状态 |
| createTime | Date | 创建时间 |
| updateTime | Date | 更新时间 |

### SenderImage（发货单图片）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 主键 |
| orderId | Long | 订单ID |
| imageUrl | String | 图片URL |
| status | Integer | 状态 |
| createTime | Date | 创建时间 |
| updateTime | Date | 更新时间 |

---

## MODIFIED Requirements

### Requirement: 回单图片实体
- OrderImage 重命名为 ReceiptImage
- 继续支持 imageType=1 的回单图片功能

# 订单表更新规格说明

## Why
前端订单详情页面（Order.vue）新增了"距离(km)"字段用于运费计算和货物信息管理，但后端Order实体类缺少该字段，导致数据传输不完整。

## What Changes
- 后端 Order 实体类新增 `distance` 字段（距离，单位：km）
- 数据库 orders 表新增 `distance` 字段

## Impact
- 受影响的实体类：`com.hmwl.entity.Order`
- 受影响的数据库表：`orders`
- 受影响的 API：订单创建/更新接口

---

## ADDED Requirements

### Requirement: 订单距离字段

#### Scenario: 创建订单时填写距离
- **WHEN** 用户在订单详情页面填写货物距离并保存
- **THEN** 距离值应正确保存到数据库，并在订单列表和详情中显示

#### Scenario: 编辑订单时修改距离
- **WHEN** 用户修改已有订单的距离信息
- **THEN** 更新后的距离值应正确保存到数据库

---

## 字段设计

| 字段名 | 类型 | 说明 | 前端对应 |
|--------|------|------|----------|
| distance | Double | 运输距离(km) | form.distance |

---

## MODIFIED Requirements

### Requirement: 订单费用计算
- **WHEN** 用户点击"估算"按钮计算运费时
- **THEN** 距离(distance)应作为费用计算参数之一参与运算

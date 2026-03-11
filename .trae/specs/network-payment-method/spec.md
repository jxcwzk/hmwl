# 费用信息卡片增加网点付款方式规格

## Why
费用信息卡片中需要增加网点付款方式选项，目前在费用卡片外已有基础付款方式，但需要增加专门的"网点付款方式"选项，包含：现付、欠付、到付、回单付。

## What Changes
- 在前端费用信息卡片中增加网点付款方式下拉选择框
- 同步更新后端Order实体和数据库字段支持新付款方式
- 新增的网点付款方式为独立字段，与现有paymentMethod区分

## Impact
- Affected specs: 订单管理功能
- Affected code: 
  - 前端: Order.vue 费用信息卡片
  - 后端: Order.java 实体类
  - 数据库: orders表增加networkPaymentMethod字段

## ADDED Requirements
### Requirement: 网点付款方式功能
系统应在费用信息卡片中提供网点付款方式选择功能。

#### Scenario: 选择网点付款方式
- **WHEN** 用户在费用信息卡片中选择网点付款方式
- **THEN** 网点付款方式字段被正确保存到订单数据中
- **AND** 网点付款方式选项包括：现付(0)、欠付(1)、到付(2)、回单付(3)

#### Scenario: 查看网点付款方式
- **WHEN** 用户编辑已有订单
- **THEN** 订单的网点付款方式应正确回显在费用信息卡片中

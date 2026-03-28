# 物流订单时间戳自动记录系统

## Why
当前系统缺少完整的订单状态时间戳记录功能。调度人员在追踪订单时无法看到每个状态完成的时间节点，无法准确掌握物流进度。此外，各角色在进行订单操作时需要系统自动记录时间，而不是手动填写。

## What Changes
- 在订单实体中添加各状态节点的时间字段
- 实现后端自动填充时间戳的机制（使用MyBatis Plus MetaObjectHandler）
- 在调度页面显示订单创建时间
- 各状态变更接口自动记录时间戳

## Impact
- Affected specs: 无
- Affected code:
  - backend/src/main/java/com/hmwl/entity/Order.java
  - backend/src/main/java/com/hmwl/handler/TimeMetaObjectHandler.java
  - backend/src/main/java/com/hmwl/controller/DispatchController.java
  - backend/src/main/java/com/hmwl/controller/OrderController.java
  - backend/src/main/java/com/hmwl/controller/NetworkController.java
  - backend/src/main/java/com/hmwl/controller/DriverController.java
  - frontend/src/views/Dispatch.vue

## ADDED Requirements

### Requirement: 订单时间戳字段
订单 SHALL 包含以下时间戳字段：
- createTime: 订单创建时间
- priceConfirmedTime: 价格确认时间
- pickedUpTime: 提货完成时间
- deliveredToNetworkTime: 送达网点时间
- warehouseConfirmTime: 网点确认收货时间
- deliveryCompletedTime: 配送完成时间

### Requirement: 自动时间戳填充
系统 SHALL 在订单创建时自动填充createTime字段。

#### Scenario: 创建订单时
- **WHEN** 新订单创建
- **THEN** createTime自动设置为当前时间

### Requirement: 状态变更时自动记录时间
系统 SHALL 在订单状态变更时自动记录对应的时间戳。

#### Scenario: 价格确认时
- **WHEN** 客户确认价格（调用confirmPrice接口）
- **THEN** priceConfirmedTime自动设置为当前时间

#### Scenario: 提货完成时
- **WHEN** 司机确认提货（调用updateStatus设置status=7）
- **THEN** pickedUpTime自动设置为当前时间

#### Scenario: 送达网点时
- **WHEN** 货物送达网点（状态变更）
- **THEN** deliveredToNetworkTime自动设置为当前时间

#### Scenario: 网点确认收货时
- **WHEN** 网点确认收货（状态变更）
- **THEN** warehouseConfirmTime自动设置为当前时间

#### Scenario: 配送完成时
- **WHEN** 客户确认签收（状态变更）
- **THEN** deliveryCompletedTime自动设置为当前时间

### Requirement: 调度页面显示创建时间
调度页面的订单列表 SHALL 显示订单的创建时间。

#### Scenario: 查看订单列表
- **WHEN** 调度人员查看调度页面的订单列表
- **THEN** 每个订单显示创建时间列

## MODIFIED Requirements
无

## REMOVED Requirements
无

## Acceptance Criteria

### AC-1: 创建订单时自动填充createTime
- **Given** 客户创建新订单
- **WHEN** 调用POST /order接口
- **THEN** 订单的createTime字段自动设置为当前时间

### AC-2: 价格确认时自动记录时间
- **Given** 客户确认价格
- **WHEN** 调用confirmPrice接口
- **THEN** priceConfirmedTime自动记录当前时间

### AC-3: 提货完成时自动记录时间
- **Given** 司机提货完成
- **WHEN** 调用updateStatus接口设置status=7
- **THEN** pickedUpTime自动记录当前时间

### AC-4: 送达网点时自动记录时间
- **Given** 货物送达网点
- **WHEN** 状态变更为送达网点
- **THEN** deliveredToNetworkTime自动记录当前时间

### AC-5: 网点确认收货时自动记录时间
- **Given** 网点确认收货
- **WHEN** 调用确认收货接口
- **THEN** warehouseConfirmTime自动记录当前时间

### AC-6: 配送完成时自动记录时间
- **Given** 客户确认签收
- **WHEN** 调用updateStatus接口设置status=13
- **THEN** deliveryCompletedTime自动记录当前时间

### AC-7: 调度页面显示创建时间
- **Given** 调度页面加载订单列表
- **WHEN** 查看任意订单标签页
- **THEN** 订单列表中显示"创建时间"列

# 调度页面订单列表添加创建时间

## Why
调度页面的订单列表中缺少创建时间字段，调度人员无法快速了解订单的创建时间顺序，影响调度决策效率。

## What Changes
- 在调度页面的5个订单列表表格中添加"创建时间"列

## Impact
- Affected specs: 无
- Affected code: frontend/src/views/Dispatch.vue

## ADDED Requirements

### Requirement: 订单列表显示创建时间
调度页面的所有订单列表表格 SHALL 显示订单的创建时间。

#### Scenario: 查看订单列表
- **WHEN** 调度人员查看调度页面的订单列表
- **THEN** 每个订单显示创建时间列

## MODIFIED Requirements
无

## REMOVED Requirements
无

## Acceptance Criteria

### AC-1: 创建时间列显示
- **Given** 调度页面加载订单列表
- **When** 查看任意订单标签页
- **Then** 订单列表中显示"创建时间"列

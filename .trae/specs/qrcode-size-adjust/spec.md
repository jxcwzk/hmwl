# 二维码大小调整规格

## Why
当前二维码区域大小不合适，在订单基本信息卡片中显示时出现滚动，需要调整二维码大小使其正好fit在卡片内，无需滚动。

## What Changes
- 调整二维码Canvas的大小
- 调整二维码容器的布局，确保不超出卡片范围
- 优化订单基本信息的布局占比

## Impact
- Affected specs: 订单二维码功能
- Affected code: frontend/src/views/Order.vue

## MODIFIED Requirements
### Requirement: 二维码显示布局
二维码应正好展现在订单基本信息卡片内，不出现滚动。

#### Scenario: 查看订单编辑对话框
- **WHEN** 用户打开订单编辑对话框
- **THEN** 二维码完整显示在订单基本信息卡片的右侧区域内
- **AND** 不出现任何滚动条

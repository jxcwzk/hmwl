# 货物信息布局调整规格

## Why
货物信息卡片中的表单项需要更紧凑的布局，目前是垂直排列，需要调整为：数量和重量并排，体积和距离并排。

## What Changes
- 调整货物信息卡片中的表单项布局
- 第一行：数量 + 重量 并排
- 第二行：体积 + 距离 并排

## Impact
- Affected specs: 订单管理功能
- Affected code: frontend/src/views/Order.vue 货物信息卡片

## MODIFIED Requirements
### Requirement: 货物信息布局
货物信息卡片中的表单项应以2列并排方式展示。

#### Scenario: 查看货物信息表单
- **WHEN** 用户打开订单编辑对话框
- **THEN** 数量和重量并排显示在同一行
- **AND** 体积和距离并排显示在同一行

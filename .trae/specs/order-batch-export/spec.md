# 订单批量导出Excel规格

## Why
用户需要批量选择订单记录并导出Excel文件，方便离线查看和处理订单数据。

## What Changes
- 在订单管理表格添加多选功能（批量选择）
- 添加"批量导出"按钮，导出选中的订单数据到Excel
- 导出内容不包括图片字段，仅包含文本数据

## Impact
- Affected specs: 订单管理功能
- Affected code: frontend/src/views/Order.vue

## ADDED Requirements
### Requirement: 批量导出功能
系统应支持批量选择订单并导出Excel文件。

#### Scenario: 批量导出订单
- **WHEN** 用户勾选多条订单记录，点击"批量导出"按钮
- **THEN** 导出选中的订单数据为Excel文件
- **AND** Excel包含订单编号、发件人、收件人、货物信息、费用等字段
- **AND** 不包含图片字段

#### Scenario: 未选择订单
- **WHEN** 用户点击"批量导出"按钮但未选择任何订单
- **THEN** 提示用户“请先选择要导出的订单”

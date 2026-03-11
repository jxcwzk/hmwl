# 订单二维码功能规格

## Why
订单需要生成二维码，二维码包含订单编号信息，用于物流追踪和扫描识别。订单创建后，二维码需要上传到云存储保存。

## What Changes
- 在订单基本信息卡片右侧添加二维码显示区域
- 后端添加二维码生成和上传到COS的功能
- 订单创建时自动生成二维码并上传到云存储
- 数据库orders表增加qrCodeUrl字段存储二维码云存储地址

## Impact
- Affected specs: 订单管理功能
- Affected code:
  - 前端: Order.vue 订单基本信息卡片
  - 后端: OrderController.java, 二维码服务
  - 数据库: orders表增加qr_code_url字段

## ADDED Requirements
### Requirement: 订单二维码功能
系统应能生成包含订单编号的二维码，并在订单创建后上传到云存储。

#### Scenario: 生成订单二维码
- **WHEN** 订单创建成功后
- **THEN** 系统自动生成包含订单编号的二维码
- **AND** 二维码上传到云存储并保存URL到数据库

#### Scenario: 显示订单二维码
- **WHEN** 用户打开订单编辑对话框
- **THEN** 在订单基本信息卡片右侧显示二维码
- **AND** 二维码内容为订单编号

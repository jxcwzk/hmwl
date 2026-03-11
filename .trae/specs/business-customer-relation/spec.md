# 业务客户关系管理 - 产品需求文档

## Overview
- **Summary**: 明确业务用户、业务客户、发件人信息、收件人信息之间的关系，实现业务用户发起订单时可以选择存储在业务客户表中的发件人和收件人信息的功能。
- **Purpose**: 解决业务用户在创建订单时需要频繁输入发件人和收件人信息的问题，提高订单创建效率，同时统一管理发件人和收件人信息。
- **Target Users**: 物流系统的业务用户和管理员。

## Goals
- 明确业务用户、业务客户、发件人信息、收件人信息之间的关系
- 实现业务客户表中存储发件人信息和收件人信息，并通过属性标识区分
- 实现业务用户管理发件人信息和收件人信息的功能
- 实现订单创建时选择发件人和收件人信息的功能
- 确保数据结构清晰，便于查询和管理

## Non-Goals (Out of Scope)
- 不修改现有的订单处理流程
- 不涉及权限管理的变更
- 不改变现有的业务用户登录和认证机制

## Background & Context
- 现有系统中已经存在BusinessUser、BusinessCustomer和BusinessRecipient实体
- 业务用户可以发起订单，在发起订单时需要选择发件人信息和收件人信息
- 发件人信息和收件人信息都存储在业务客户表中
- 需要在业务客户表中添加属性，备注为发件人信息还是收件人信息，方便创建订单时选择

## Functional Requirements
- **FR-1**: 扩展BusinessCustomer实体，添加类型标识字段，区分发件人信息和收件人信息
- **FR-2**: 实现业务用户管理发件人信息的功能
- **FR-3**: 实现业务用户管理收件人信息的功能
- **FR-4**: 实现订单创建时选择发件人和收件人信息的功能
- **FR-5**: 确保发件人和收件人信息与业务用户关联，只能看到自己的信息

## Non-Functional Requirements
- **NFR-1**: 数据存储结构清晰，便于查询和管理
- **NFR-2**: 操作界面友好，操作流程简单
- **NFR-3**: 性能稳定，响应速度快

## Constraints
- **Technical**: 基于现有的Spring Boot + MyBatis Plus架构
- **Business**: 保持与现有系统的兼容性
- **Dependencies**: 依赖现有的用户认证和权限系统

## Assumptions
- 业务用户已经存在于系统中
- 业务用户可以创建多个发件人和收件人信息
- 发件人和收件人信息都属于业务客户的范畴

## Acceptance Criteria

### AC-1: 业务客户类型标识
- **Given**: 系统中存在BusinessCustomer实体
- **When**: 扩展BusinessCustomer实体，添加type字段用于标识发件人或收件人
- **Then**: 业务客户表中能够区分发件人信息和收件人信息
- **Verification**: `programmatic`

### AC-2: 发件人信息管理
- **Given**: 业务用户登录系统
- **When**: 业务用户添加、编辑、删除发件人信息
- **Then**: 操作成功，发件人信息正确存储
- **Verification**: `programmatic`

### AC-3: 收件人信息管理
- **Given**: 业务用户登录系统
- **When**: 业务用户添加、编辑、删除收件人信息
- **Then**: 操作成功，收件人信息正确存储
- **Verification**: `programmatic`

### AC-4: 订单创建时选择发件人和收件人
- **Given**: 业务用户创建订单
- **When**: 业务用户选择发件人和收件人信息
- **Then**: 订单中正确显示发件人和收件人信息
- **Verification**: `programmatic`

### AC-5: 数据隔离
- **Given**: 多个业务用户存在
- **When**: 业务用户查看发件人和收件人信息
- **Then**: 只能看到自己创建的信息
- **Verification**: `programmatic`

## Open Questions
- [ ] BusinessCustomer表中的type字段具体取值如何定义？
- [ ] 是否需要在前端添加专门的发件人和收件人管理界面？
- [ ] 订单创建时如何关联发件人和收件人信息？
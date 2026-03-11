# 业务客户收件人管理 - 产品需求文档

## Overview
- **Summary**: 解决业务客户表中收件人信息返回空的问题，确保业务用户能够在业务客户表中选择收件人信息。
- **Purpose**: 确保业务用户在创建订单时能够选择存储在业务客户表中的收件人信息，提高订单创建效率。
- **Target Users**: 物流系统的业务用户和管理员。

## Goals
- 确保业务客户表中能够存储收件人信息
- 实现业务用户管理收件人信息的功能
- 确保订单创建时能够选择收件人信息
- 确保API接口能够正确返回收件人信息

## Non-Goals (Out of Scope)
- 不修改现有的发件人信息管理功能
- 不修改现有的订单处理流程
- 不涉及权限管理的变更

## Background & Context
- 现有系统中已经存在BusinessCustomer实体，并且已经添加了type字段用于区分发件人信息和收件人信息
- 发件人信息（type=0）能够正确返回，但是收件人信息（type=1）返回空列表
- 业务用户需要在创建订单时选择收件人信息

## Functional Requirements
- **FR-1**: 确保BusinessCustomer表中能够存储type=1的收件人信息
- **FR-2**: 实现业务用户添加、编辑、删除收件人信息的功能
- **FR-3**: 确保API接口能够正确返回type=1的收件人信息
- **FR-4**: 确保订单创建时能够选择收件人信息

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
- 业务用户可以创建多个收件人信息
- 收件人信息属于业务客户的范畴，type=1

## Acceptance Criteria

### AC-1: 业务客户表存储收件人信息
- **Given**: 系统中存在BusinessCustomer实体
- **When**: 业务用户添加收件人信息
- **Then**: 收件人信息正确存储在BusinessCustomer表中，type=1
- **Verification**: `programmatic`

### AC-2: API接口返回收件人信息
- **Given**: BusinessCustomer表中存在type=1的收件人信息
- **When**: 调用API接口查询收件人信息
- **Then**: API接口返回type=1的收件人信息列表
- **Verification**: `programmatic`

### AC-3: 订单创建时选择收件人信息
- **Given**: 业务用户创建订单
- **When**: 业务用户选择收件人信息
- **Then**: 订单中正确显示收件人信息
- **Verification**: `programmatic`

## Open Questions
- [ ] 为什么BusinessCustomer表中没有type=1的收件人信息？
- [ ] 业务用户是否能够添加收件人信息？
- [ ] 前端页面是否正确调用API接口查询收件人信息？
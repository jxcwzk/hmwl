# 订单创建功能修复 - 实现计划

## [ ] Task 1: 检查和修复订单表结构
- **Priority**: P0
- **Depends On**: None
- **Description**: 
  - 检查订单表结构，确保包含所有必要的字段，特别是business_user_id和recipient_id字段
  - 如果缺少字段，添加相应的字段
- **Acceptance Criteria Addressed**: AC-1
- **Test Requirements**:
  - `programmatic` TR-1.1: 订单表包含所有必要的字段，包括business_user_id和recipient_id
  - `programmatic` TR-1.2: 订单保存API能够成功保存订单数据
- **Notes**: 使用ALTER TABLE语句添加缺失的字段

## [ ] Task 2: 检查和修复数据库字符集设置
- **Priority**: P0
- **Depends On**: None
- **Description**: 
  - 检查数据库字符集设置，确保支持中文
  - 检查连接字符串中的字符集设置
- **Acceptance Criteria Addressed**: AC-3
- **Test Requirements**:
  - `programmatic` TR-2.1: 数据库字符集设置为utf8mb4
  - `programmatic` TR-2.2: 连接字符串中指定字符集为utf8mb4
  - `human-judgment` TR-2.3: 中文信息显示正常，无乱码
- **Notes**: 修改application.yml中的数据库连接字符串，添加characterEncoding=utf8mb4参数

## [ ] Task 3: 测试订单保存功能
- **Priority**: P0
- **Depends On**: Task 1, Task 2
- **Description**: 
  - 测试订单保存API，确保能够成功保存订单
  - 测试订单创建页面的保存功能
- **Acceptance Criteria Addressed**: AC-1
- **Test Requirements**:
  - `programmatic` TR-3.1: 调用POST /api/order接口成功保存订单
  - `human-judgment` TR-3.2: 在订单创建页面填写信息并点击保存按钮，订单成功保存
- **Notes**: 使用Postman或curl测试API，然后在前端页面测试

## [ ] Task 4: 测试订单列表显示功能
- **Priority**: P0
- **Depends On**: Task 1, Task 2, Task 3
- **Description**: 
  - 测试订单列表API，确保能够正确返回订单数据
  - 测试订单管理页面的显示功能
- **Acceptance Criteria Addressed**: AC-2
- **Test Requirements**:
  - `programmatic` TR-4.1: 调用GET /api/order/page接口成功返回订单列表
  - `human-judgment` TR-4.2: 订单管理页面显示所有已保存的订单记录
- **Notes**: 使用Postman或curl测试API，然后在前端页面测试

## [ ] Task 5: 测试中文显示功能
- **Priority**: P0
- **Depends On**: Task 1, Task 2, Task 3, Task 4
- **Description**: 
  - 测试订单中的中文信息是否能够正确显示
  - 测试业务用户和收件人信息中的中文是否能够正确显示
- **Acceptance Criteria Addressed**: AC-3
- **Test Requirements**:
  - `human-judgment` TR-5.1: 订单管理页面中中文信息显示正常，无乱码
  - `human-judgment` TR-5.2: 业务用户和收件人信息中的中文显示正常，无乱码
- **Notes**: 在前端页面测试中文显示效果

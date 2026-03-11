# 业务客户关系管理 - 实现计划

## [ ] Task 1: 定义BusinessCustomer类型字段取值
- **Priority**: P0
- **Depends On**: None
- **Description**: 
  - 定义BusinessCustomer表中type字段的具体取值
  - 0表示发件人信息，1表示收件人信息
  - 更新相关文档和注释
- **Acceptance Criteria Addressed**: AC-1
- **Test Requirements**:
  - `programmatic` TR-1.1: 确认type字段取值定义清晰
  - `human-judgement` TR-1.2: 检查代码注释是否清晰说明type字段的用途
- **Notes**: 确保所有相关代码都使用统一的常量定义

## [ ] Task 2: 修改BusinessCustomer实体和Mapper
- **Priority**: P0
- **Depends On**: Task 1
- **Description**: 
  - 在BusinessCustomer实体中添加常量定义
  - 更新BusinessCustomerMapper，添加按类型查询的方法
  - 确保数据库表结构正确
- **Acceptance Criteria Addressed**: AC-1
- **Test Requirements**:
  - `programmatic` TR-2.1: 确认BusinessCustomer实体中添加了类型常量
  - `programmatic` TR-2.2: 确认Mapper中添加了按类型查询的方法
- **Notes**: 保持与现有代码风格一致

## [ ] Task 3: 实现业务客户服务层
- **Priority**: P0
- **Depends On**: Task 2
- **Description**: 
  - 更新BusinessCustomerService，添加按类型查询的方法
  - 实现业务用户管理发件人和收件人信息的逻辑
  - 确保数据隔离，只能访问自己的信息
- **Acceptance Criteria Addressed**: AC-2, AC-3, AC-5
- **Test Requirements**:
  - `programmatic` TR-3.1: 测试添加发件人信息功能
  - `programmatic` TR-3.2: 测试添加收件人信息功能
  - `programmatic` TR-3.3: 测试数据隔离功能
- **Notes**: 注意权限控制，确保业务用户只能管理自己的信息

## [ ] Task 4: 更新业务客户控制器
- **Priority**: P0
- **Depends On**: Task 3
- **Description**: 
  - 更新BusinessCustomerController，添加按类型查询的接口
  - 确保接口返回正确的数据
  - 添加必要的错误处理
- **Acceptance Criteria Addressed**: AC-2, AC-3
- **Test Requirements**:
  - `programmatic` TR-4.1: 测试获取发件人列表接口
  - `programmatic` TR-4.2: 测试获取收件人列表接口
- **Notes**: 保持API风格与现有接口一致

## [ ] Task 5: 修改订单实体和相关代码
- **Priority**: P1
- **Depends On**: Task 4
- **Description**: 
  - 在Order实体中添加发件人和收件人信息的关联字段
  - 更新OrderMapper，添加相关查询方法
  - 更新OrderService，处理发件人和收件人信息的关联
- **Acceptance Criteria Addressed**: AC-4
- **Test Requirements**:
  - `programmatic` TR-5.1: 确认Order实体添加了必要的关联字段
  - `programmatic` TR-5.2: 测试订单创建时关联发件人和收件人信息
- **Notes**: 确保与现有订单处理逻辑兼容

## [ ] Task 6: 更新前端订单创建页面
- **Priority**: P1
- **Depends On**: Task 5
- **Description**: 
  - 在订单创建页面添加发件人和收件人信息的选择功能
  - 实现下拉选择框，显示业务用户的发件人和收件人信息
  - 确保选择后自动填充相关信息
- **Acceptance Criteria Addressed**: AC-4
- **Test Requirements**:
  - `programmatic` TR-6.1: 测试订单创建页面能够显示发件人列表
  - `programmatic` TR-6.2: 测试订单创建页面能够显示收件人列表
  - `human-judgement` TR-6.3: 检查界面操作是否流畅，用户体验是否良好
- **Notes**: 保持前端界面风格与现有页面一致

## [ ] Task 7: 测试和验证
- **Priority**: P1
- **Depends On**: Task 6
- **Description**: 
  - 测试所有功能是否正常工作
  - 验证数据隔离是否有效
  - 检查边界情况和错误处理
- **Acceptance Criteria Addressed**: AC-1, AC-2, AC-3, AC-4, AC-5
- **Test Requirements**:
  - `programmatic` TR-7.1: 测试所有API接口是否正常返回
  - `programmatic` TR-7.2: 测试数据隔离是否有效
  - `human-judgement` TR-7.3: 检查整体功能是否符合需求
- **Notes**: 确保测试覆盖所有场景
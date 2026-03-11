# 订单创建功能改进 - 实现计划

## [/] Task 1: 创建业务收件人表
- **Priority**: P0
- **Depends On**: None
- **Description**: 
  - 创建业务收件人表，用于存储基于业务用户的收件人信息
  - 表结构包含：id, business_user_id, name, phone, address, remark, create_time, update_time
- **Success Criteria**:
  - 业务收件人表创建成功
  - 表结构符合要求
- **Test Requirements**:
  - `programmatic` TR-1.1: 执行SQL语句创建业务收件人表
  - `programmatic` TR-1.2: 表结构正确，包含所有必要字段
- **Notes**: 业务收件人表需要与业务用户表建立关联

## [ ] Task 2: 实现业务收件人后端API
- **Priority**: P0
- **Depends On**: Task 1
- **Description**: 
  - 创建BusinessRecipient实体类
  - 创建BusinessRecipientMapper接口
  - 创建BusinessRecipientService接口和实现类
  - 创建BusinessRecipientController控制器
- **Success Criteria**:
  - 业务收件人后端API实现完成
  - API接口能正常工作
- **Test Requirements**:
  - `programmatic` TR-2.1: 业务收件人列表API返回200状态码
  - `programmatic` TR-2.2: 业务收件人创建API返回200状态码
- **Notes**: API接口需要支持根据业务用户ID查询收件人列表

## [ ] Task 3: 修改订单创建表单
- **Priority**: P0
- **Depends On**: Task 2
- **Description**: 
  - 在订单创建表单中添加业务用户选择下拉框
  - 添加"新增业务用户"按钮
  - 在订单创建表单中添加发件人选择下拉框（基于业务用户）
  - 在订单创建表单中添加收件人选择下拉框（基于业务用户ID）
  - 添加"新增收件人"按钮
- **Success Criteria**:
  - 订单创建表单中添加了所有必要的选择框和按钮
  - 表单布局合理，用户体验良好
- **Test Requirements**:
  - `human-judgement` TR-3.1: 表单布局合理，按钮位置明显
  - `human-judgement` TR-3.2: 选择框功能正常，能显示正确的选项
- **Notes**: 需要确保表单验证逻辑正确

## [ ] Task 4: 实现业务用户和业务收件人列表获取功能
- **Priority**: P0
- **Depends On**: Task 3
- **Description**: 
  - 在订单组件中添加获取业务用户列表的方法
  - 在订单组件中添加获取业务收件人列表的方法（基于业务用户ID）
  - 实现业务用户选择后自动更新发件人信息
  - 实现业务用户选择后自动加载对应的收件人列表
- **Success Criteria**:
  - 业务用户列表能正常获取和显示
  - 业务收件人列表能基于业务用户ID正常获取和显示
  - 选择业务用户后，发件人信息自动填充
- **Test Requirements**:
  - `programmatic` TR-4.1: 调用`/api/business-user/list`接口获取业务用户列表
  - `programmatic` TR-4.2: 调用`/api/business-recipient/list`接口获取业务收件人列表
  - `human-judgement` TR-4.3: 选择业务用户后，发件人信息自动填充
- **Notes**: 确保接口调用错误时的异常处理

## [ ] Task 5: 实现跳转到新增业务用户和新增收件人功能
- **Priority**: P0
- **Depends On**: Task 3
- **Description**: 
  - 实现"新增业务用户"按钮的点击事件，跳转到业务用户创建页面
  - 实现"新增收件人"按钮的点击事件，跳转到收件人创建页面
  - 实现从业务用户和收件人创建页面返回后，刷新对应列表
- **Success Criteria**:
  - 点击"新增业务用户"按钮成功跳转到业务用户创建页面
  - 点击"新增收件人"按钮成功跳转到收件人创建页面
  - 创建完成后返回订单创建页面并刷新对应列表
- **Test Requirements**:
  - `programmatic` TR-5.1: 点击"新增业务用户"按钮后路由跳转到`/customer/business-user`
  - `programmatic` TR-5.2: 点击"新增收件人"按钮后路由跳转到收件人创建页面
  - `human-judgement` TR-5.3: 跳转流程流畅，用户体验良好
- **Notes**: 可以使用路由参数或本地存储来记录返回地址

## [ ] Task 6: 实现收件人选择功能
- **Priority**: P0
- **Depends On**: Task 4, Task 5
- **Description**: 
  - 实现收件人选择后自动填充收件人信息
  - 确保收件人列表基于选定的业务用户ID进行筛选
- **Success Criteria**:
  - 选择收件人后，收件人信息自动填充
  - 收件人列表正确基于业务用户ID进行筛选
- **Test Requirements**:
  - `human-judgement` TR-6.1: 选择收件人后，收件人信息自动填充
  - `human-judgement` TR-6.2: 收件人列表正确基于业务用户ID进行筛选
- **Notes**: 确保收件人列表在业务用户变更时及时更新

## [ ] Task 7: 测试订单创建功能
- **Priority**: P1
- **Depends On**: Task 1, Task 2, Task 3, Task 4, Task 5, Task 6
- **Description**: 
  - 测试订单创建功能是否正常
  - 测试业务用户选择功能是否正常
  - 测试发件人信息自动填充功能是否正常
  - 测试收件人选择功能是否正常
  - 测试跳转到新增业务用户和新增收件人功能是否正常
- **Success Criteria**:
  - 订单创建功能正常工作
  - 业务用户选择功能正常工作
  - 发件人信息自动填充功能正常工作
  - 收件人选择功能正常工作
  - 跳转到新增业务用户和新增收件人功能正常工作
- **Test Requirements**:
  - `programmatic` TR-7.1: 成功创建订单并关联业务用户和收件人
  - `human-judgement` TR-7.2: 整个流程操作顺畅，用户体验良好
- **Notes**: 确保所有功能都能正常工作，无错误发生
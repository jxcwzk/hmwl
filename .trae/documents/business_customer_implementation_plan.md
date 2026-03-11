# 业务客户创建功能改进 - 实现计划

## \[x] Task 1: 修改业务客户创建表单

* **Priority**: P0

* **Depends On**: None

* **Description**:

  * 将业务客户创建表单中的"业务用户ID"输入框改为下拉选择框

  * 添加"新增业务用户"按钮，用于跳转到业务用户创建页面

* **Success Criteria**:

  * 业务客户创建表单中的业务用户字段变为下拉选择框

  * 下拉选择框中显示所有业务用户的列表

  * 表单中添加"新增业务用户"按钮

* **Test Requirements**:

  * `programmatic` TR-1.1: 页面加载时，下拉选择框应显示业务用户列表

  * `human-judgement` TR-1.2: 表单布局合理，按钮位置明显

* **Notes**: 需要先获取业务用户列表数据

## \[x] Task 2: 添加业务用户列表获取功能

* **Priority**: P0

* **Depends On**: Task 1

* **Description**:

  * 在业务客户组件中添加获取业务用户列表的方法

  * 页面加载时自动获取业务用户列表

  * 业务用户列表更新时，下拉选择框应同步更新

* **Success Criteria**:

  * 页面加载时成功获取业务用户列表

  * 业务用户列表数据正确显示在下拉选择框中

* **Test Requirements**:

  * `programmatic` TR-2.1: 调用`/api/business-user/list`接口获取业务用户列表

  * `programmatic` TR-2.2: 接口调用失败时显示错误提示

* **Notes**: 确保接口调用错误时的异常处理

## [x] Task 3: 实现跳转到新增业务用户功能
- **Priority**: P0
- **Depends On**: Task 1
- **Description**: 
  - 实现"新增业务用户"按钮的点击事件
  - 点击按钮时跳转到业务用户创建页面
  - 业务用户创建完成后，返回业务客户创建页面并刷新业务用户列表
- **Success Criteria**:
  - 点击"新增业务用户"按钮成功跳转到业务用户创建页面
  - 业务用户创建完成后返回业务客户创建页面
  - 业务用户列表自动刷新
- **Test Requirements**:
  - `programmatic` TR-3.1: 点击按钮后路由跳转到`/customer/business-user`
  - `human-judgement` TR-3.2: 跳转流程流畅，用户体验良好
- **Notes**: 可以考虑使用路由参数或本地存储来记录返回地址

## [x] Task 4: 测试业务客户创建功能
- **Priority**: P1
- **Depends On**: Task 1, Task 2, Task 3
- **Description**: 
  - 测试业务客户创建功能是否正常
  - 测试业务用户选择功能是否正常
  - 测试跳转到新增业务用户功能是否正常
- **Success Criteria**:
  - 业务客户创建功能正常工作
  - 业务用户选择功能正常工作
  - 跳转到新增业务用户功能正常工作
- **Test Requirements**:
  - `programmatic` TR-4.1: 成功创建业务客户并关联业务用户
  - `human-judgement` TR-4.2: 整个流程操作顺畅，用户体验良好
- **Notes**: 确保所有功能都能正常工作，无错误发生


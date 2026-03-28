# Tasks

- [x] Task 1: 修改 CustomerOrder.vue - 设置电话和地址字段为只读（disabled）
  - [x] SubTask 1.1: 将 addForm.senderPhone 和 addForm.senderAddress 的输入框添加 disabled 属性
  - [x] SubTask 1.2: 将 addForm.receiverPhone 和 addForm.receiverAddress 的输入框添加 disabled 属性

- [x] Task 2: 修改 CustomerOrder.vue - 添加新增联系人功能
  - [x] SubTask 2.1: 在发件人选择框下方添加"新增发件人"按钮（始终显示）
  - [x] SubTask 2.2: 在收件人选择框下方添加"新增收件人"按钮（始终显示）
  - [x] SubTask 2.3: 创建卡片形式的联系人编辑表单
  - [x] SubTask 2.4: 实现保存联系人后自动填充订单表单的逻辑

- [x] Task 3: 修改 CustomerOrder.vue - 实现新增联系人API调用
  - [x] SubTask 3.1: 添加 addSender 方法调用 POST /miniprogram/sender
  - [x] SubTask 3.2: 添加 addRecipient 方法调用 POST /miniprogram/recipient
  - [x] SubTask 3.3: 添加联系人保存成功后刷新联系人列表的逻辑

- [x] Task 4: 修改 CustomerOrder.vue - 添加编辑联系人功能
  - [x] SubTask 4.1: 添加编辑按钮（当已选择联系人时显示）
  - [x] SubTask 4.2: 实现编辑时预填充联系人现有信息到卡片表单
  - [x] SubTask 4.3: 添加 updateSender/updateReceiver 方法调用 PUT /miniprogram/contact
  - [x] SubTask 4.4: 实现取消编辑时收起表单并保持信息不变

# Task Dependencies
- Task 2 依赖于 Task 1 的完成（确保字段只读后再添加新增功能）
- Task 3 依赖于 Task 2 的完成（新增联系人功能需要表单结构）
- Task 4 依赖于 Task 2 和 Task 3 的完成（复用卡片表单结构）

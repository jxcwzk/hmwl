# 业务客户关系管理 - 验证清单

- [ ] 检查BusinessCustomer表是否添加了type字段用于区分发件人信息和收件人信息
- [ ] 验证BusinessCustomer实体中是否添加了类型常量定义
- [ ] 检查BusinessCustomerMapper是否添加了按类型查询的方法
- [ ] 验证BusinessCustomerService是否实现了按类型查询的方法
- [ ] 检查BusinessCustomerController是否添加了按类型查询的接口
- [ ] 验证Order实体是否添加了发件人和收件人信息的关联字段
- [ ] 检查订单创建页面是否添加了发件人和收件人信息的选择功能
- [ ] 验证业务用户只能看到自己的发件人和收件人信息
- [ ] 检查所有API接口是否正常返回
- [ ] 验证订单创建时能够正确选择和关联发件人和收件人信息
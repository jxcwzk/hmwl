# Tasks

- [x] Task 1: 更新后端 Order 实体类，添加 distance 字段
  - [x] 在 com.hmwl.entity.Order 类中添加 private Double distance 字段
  - [x] 确保使用 Lombok @Data 注解自动生成 getter/setter

- [x] Task 2: 更新数据库 orders 表结构
  - [x] 执行 ALTER TABLE 语句添加 distance 字段
  - [x] 验证字段添加成功

- [x] Task 3: 验证前后端集成
  - [x] 测试创建订单时 distance 字段的保存
  - [x] 测试编辑订单时 distance 字段的更新
  - [x] 测试订单列表中 distance 字段的显示

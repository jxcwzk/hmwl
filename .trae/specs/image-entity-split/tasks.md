# Tasks

- [x] Task 1: 重命名 OrderImage 为 ReceiptImage
  - [x] 创建新的 ReceiptImage.java 实体类
  - [x] 修改 @TableName 为 receipt_image
  - [x] 删除原有的 OrderImage.java

- [x] Task 2: 新增 SenderImage 实体类
  - [x] 创建 SenderImage.java 实体类
  - [x] 设置 @TableName 为 sender_image

- [x] Task 3: 更新数据库表结构
  - [x] 创建 receipt_image 表
  - [x] 创建 sender_image 表

- [x] Task 4: 更新 Mapper 层
  - [x] 创建 ReceiptImageMapper
  - [x] 创建 SenderImageMapper
  - [x] 删除原有 OrderImageMapper

- [x] Task 5: 更新 Service 层
  - [x] 创建 ReceiptImageService 和 ReceiptImageServiceImpl
  - [x] 创建 SenderImageService 和 SenderImageServiceImpl
  - [x] 删除原有 OrderImageService

- [x] Task 6: 更新 Controller 层
  - [x] 创建 ReceiptImageController
  - [x] 创建 SenderImageController
  - [x] 删除原有 OrderImageController

- [x] Task 7: 验证编译
  - [x] 执行 mvn compile 验证无错误

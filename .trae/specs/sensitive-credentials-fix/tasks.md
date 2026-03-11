# Tasks
- [x] Task 1: 创建 application-dev.yml 本地开发配置文件
  - [x] SubTask 1.1: 在 backend/src/main/resources/ 下创建 application-dev.yml
  - [x] SubTask 1.2: 配置本地 COS 凭证（secretId, secretKey, bucketName, region, baseUrl）
- [x] Task 2: 修改 application.yml 使用环境变量占位符
  - [x] SubTask 2.1: 将 cos.secretId 改为 ${COS_SECRET_ID}
  - [x] SubTask 2.2: 将 cos.secretKey 改为 ${COS_SECRET_KEY}
  - [x] SubTask 2.3: 移除实际的凭证值，只保留 bucketName, region, baseUrl
- [x] Task 3: 修改 CosConfig.java 支持环境变量优先
  - [x] SubTask 3.1: 修改 @Value 注解支持环境变量回退机制
- [x] Task 4: 修改 CosTest.java 使用占位符和参数传入
  - [x] SubTask 4.1: 从系统属性或环境变量读取凭证
  - [x] SubTask 4.2: 如果未传入则使用占位符抛出明确异常
- [x] Task 5: 更新 .gitignore 添加本地配置文件排除规则
  - [x] SubTask 5.1: 添加 application-dev.yml 到 .gitignore

# Task Dependencies
- Task 2 依赖 Task 1（先创建 dev 配置文件）
- Task 3 可与 Task 2 并行执行
- Task 4 独立于 Task 1-3
- Task 5 依赖 Task 1（确保 dev 文件被排除）

# Checklist
- [x] application-dev.yml 已创建并包含本地 COS 凭证配置
- [x] application.yml 中 COS 凭证使用环境变量占位符格式
- [x] CosConfig.java 支持环境变量优先读取
- [x] CosTest.java 使用占位符，运行时通过参数传入
- [x] .gitignore 已添加 application-dev.yml 排除规则
- [x] 验证 application-dev.yml 不在 Git 仓库中
- [x] 验证 application.yml 中无真实凭证
- [x] 验证 CosTest.java 中无硬编码凭证

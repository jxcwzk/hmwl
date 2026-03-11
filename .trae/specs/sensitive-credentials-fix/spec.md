# 敏感凭证管理规范 Spec

## Why
GitHub 机密扫描保护机制触发，代码中包含腾讯云 COS 的 Secret ID/Secret Key 等敏感凭证，直接提交到公共仓库会造成严重的安全风险。

## What Changes
- 创建 application-dev.yml 配置文件用于本地开发，本地凭证写入此文件（加入 .gitignore）
- 修改 application.yml 使用环境变量占位符格式（cos.secretId=${COS_SECRET_ID}）
- 修改 CosConfig.java 支持环境变量注入
- 修改 CosTest.java 测试代码使用占位符，运行时通过参数传入
- 添加 .gitignore 规则防止本地配置文件被提交

## Impact
- Affected specs: 无
- Affected code:
  - backend/src/main/resources/application.yml
  - backend/src/main/resources/application-dev.yml (新建)
  - backend/src/main/java/com/hmwl/config/CosConfig.java
  - backend/src/test/java/com/hmwl/test/CosTest.java
  - .gitignore

## ADDED Requirements
### Requirement: 本地开发配置文件
系统 SHALL 提供 application-dev.yml 配置文件用于本地开发环境。

#### Scenario: 本地开发环境
- **GIVEN** 开发人员进行本地开发
- **WHEN** 启动 Spring Boot 应用
- **THEN** 自动加载 application-dev.yml 中的本地 COS 凭证

### Requirement: 生产环境环境变量注入
系统 SHALL 支持通过环境变量注入敏感凭证。

#### Scenario: 生产环境部署
- **GIVEN** 生产环境部署时设置环境变量 COS_SECRET_ID 和 COS_SECRET_KEY
- **WHEN** 启动 Spring Boot 应用
- **THEN** 使用环境变量中的凭证连接 COS

### Requirement: 测试代码敏感凭证处理
测试代码 SHALL 使用占位符，运行时通过参数或系统属性传入真实凭证。

#### Scenario: 运行单元测试
- **GIVEN** 运行 CosTest 测试类
- **WHEN** 通过 -D 参数传入 secretId 和 secretKey
- **THEN** 使用传入的凭证执行测试

## MODIFIED Requirements
### Requirement: application.yml 配置
原：直接写入 COS 凭证
改：使用环境变量占位符格式

### Requirement: CosConfig.java
原：通过 @Value("${cos.secretId}") 直接读取
改：支持环境变量优先，无环境变量时读取配置文件

## REMOVED Requirements
### Requirement: 无
无移除需求。

## 实施步骤
1. 创建 application-dev.yml（本地开发用，加入 .gitignore）
2. 修改 application.yml 使用环境变量占位符
3. 修改 CosConfig.java 支持环境变量优先
4. 修改 CosTest.java 使用占位符和参数传入
5. 更新 .gitignore 添加本地配置文件排除规则

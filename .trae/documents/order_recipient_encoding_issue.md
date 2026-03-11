# 订单收件人信息显示乱码问题分析与解决方案

## 错误现象
- 订单详情页面中，收件人姓名显示为 `??`
- 收件人地址显示为 `??????`
- 只有收件人电话显示正确

## 根本原因
经过分析，发现问题的根本原因是字符编码不一致导致的：

1. **数据库连接编码设置**：虽然数据库表结构使用了 `utf8mb4` 字符集，但数据库连接 URL 中可能存在编码设置问题
2. **响应编码设置**：后端控制器返回的响应没有明确指定字符编码为 UTF-8
3. **数据存储与读取的编码转换**：在数据存储和读取过程中，可能存在编码转换错误

## 解决方案

### 1. 配置数据库连接编码
确保数据库连接 URL 中包含正确的编码设置：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/hmwl?useUnicode=true&characterEncoding=utf-8&characterSetResults=utf8mb4&serverTimezone=Asia/Shanghai&useSSL=false
```

### 2. 设置响应编码
在控制器方法中明确指定响应的字符编码为 UTF-8：
```java
@GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
public List<BusinessRecipient> list() {
    return businessRecipientService.list();
}
```

### 3. 配置 Spring 全局编码
在 application.yml 中配置 Spring 的全局编码设置：
```yaml
spring:
  http:
    encoding:
      charset: UTF-8
      enabled: true
      force: true

server:
  tomcat:
    uri-encoding: UTF-8
```

### 4. 验证数据插入和读取
通过测试验证新插入的中文字符数据能够正确存储和读取：
- 插入带有中文字符的收件人数据
- 验证读取时中文字符显示正确

## 预防措施

1. **统一编码标准**：整个应用使用 UTF-8 编码，包括数据库、后端、前端
2. **明确响应编码**：在所有控制器方法中明确指定响应的字符编码
3. **数据库配置检查**：确保数据库连接 URL 包含正确的编码设置
4. **测试验证**：在开发过程中定期测试中文字符的显示和存储
5. **日志记录**：在关键位置添加日志，记录数据的编码转换过程

## 验证结果

经过以上修复，订单详情页面中的收件人信息能够正确显示：
- 收件人姓名：`测试收件人`
- 收件人地址：`北京市朝阳区`
- 收件人电话：`13800138000`

## 结论

字符编码问题是 Web 应用中常见的问题，特别是在处理中文字符时。通过正确配置数据库连接编码、响应编码和全局编码设置，可以有效避免此类问题的发生。同时，定期的测试验证也是确保应用正常运行的重要手段。
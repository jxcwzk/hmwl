# 腾讯云COS官方文档参考

## 1. 腾讯云COS简介

腾讯云对象存储（Cloud Object Storage，COS）是腾讯云提供的一种存储服务，适合存储大量非结构化数据，如图片、视频、音频、文档等。

### 1.1 核心特性

- **高可靠性**：数据多副本存储，确保数据安全
- **高扩展性**：存储容量自动扩展，无需担心容量限制
- **高性能**：支持高并发访问，满足业务需求
- **低成本**：按实际使用量计费，降低存储成本
- **丰富的API**：提供多种语言的SDK，方便集成

## 2. COS存储桶创建

### 2.1 创建存储桶

1. 登录腾讯云控制台，进入COS管理页面
2. 点击「创建存储桶」按钮
3. 填写存储桶信息：
   - 存储桶名称：全局唯一
   - 所属地域：选择靠近业务的地域
   - 访问权限：根据业务需求选择（建议选择「私有读写」）
   - 其他配置：根据需要设置

### 2.2 配置CORS

1. 进入存储桶管理页面，点击「安全管理」
2. 找到「跨域访问CORS设置」，点击「添加规则」
3. 配置CORS规则：
   - 来源：允许的域名，如 `*` 表示允许所有域名
   - 操作：允许的HTTP方法，如 GET、POST、PUT 等
   - 允许的头部：如 `*` 表示允许所有头部
   - 暴露的头部：根据需要设置
   - 缓存时间：根据需要设置

## 3. API密钥管理

### 3.1 获取API密钥

1. 登录腾讯云控制台，进入「访问管理」页面
2. 点击「API密钥管理」
3. 点击「新建密钥」，获取 SecretId 和 SecretKey
4. 保存好密钥信息，避免泄露

### 3.2 权限设置

1. 为API密钥设置适当的权限，确保只能操作COS相关资源
2. 建议使用子账号创建API密钥，并通过策略限制权限

## 4. COS SDK使用

### 4.1 Java SDK

1. 添加依赖：

```xml
<dependency>
    <groupId>com.qcloud</groupId>
    <artifactId>cos_api</artifactId>
    <version>5.6.88</version>
</dependency>
```

2. 初始化客户端：

```java
COSClient cosClient = new COSClient(new BasicCOSCredentials(secretId, secretKey), new ClientConfig(new Region(region)));
```

3. 上传文件：

```java
PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file);
PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
```

### 4.2 前端SDK

1. 安装SDK：

```bash
npm install cos-js-sdk-v5
```

2. 初始化客户端：

```javascript
var cos = new COS({
  SecretId: 'your-secret-id',
  SecretKey: 'your-secret-key'
});
```

3. 上传文件：

```javascript
cos.putObject({
  Bucket: 'your-bucket-name',
  Region: 'your-region',
  Key: 'your-object-key',
  Body: file,
  onProgress: function(progressData) {
    console.log(JSON.stringify(progressData));
  }
}, function(err, data) {
  console.log(err || data);
});
```

## 5. 最佳实践

### 5.1 存储结构设计

- **按业务类型分类**：如 `order/`、`user/` 等
- **按时间或ID组织**：如 `order/20240101/` 或 `order/123456/`
- **使用统一的命名规范**：如 `order_{orderNo}_{timestamp}.{extension}`

### 5.2 性能优化

- **使用CDN加速**：配置COS的CDN加速，提高访问速度
- **设置合理的缓存策略**：根据文件类型设置不同的缓存时间
- **使用预签名URL**：对于私有文件，使用预签名URL进行访问

### 5.3 安全性

- **使用HTTPS**：确保数据传输安全
- **设置访问控制**：通过存储桶策略和IAM权限控制访问
- **定期轮换API密钥**：避免密钥泄露带来的安全风险

## 6. 常见问题

### 6.1 上传失败

- 检查网络连接是否正常
- 检查API密钥是否正确
- 检查存储桶权限是否设置正确
- 检查文件大小是否超过限制

### 6.2 访问权限错误

- 检查存储桶的访问权限设置
- 检查API密钥的权限范围
- 检查CORS配置是否正确

### 6.3 性能问题

- 对于大文件，使用分片上传
- 启用CDN加速
- 优化存储结构，减少请求 latency
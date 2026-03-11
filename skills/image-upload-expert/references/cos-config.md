# 腾讯云COS配置文档

## 配置项

| 配置项 | 说明 | 示例值 |
| --- | --- | --- |
| `cos.secretId` | 腾讯云API密钥SecretId | `YOUR_SECRET_ID` |
| `cos.secretKey` | 腾讯云API密钥SecretKey | `YOUR_SECRET_KEY` |
| `cos.bucketName` | 腾讯云COS存储桶名称 | `jxcwzk-1316797858` |
| `cos.region` | 腾讯云COS区域 | `ap-shanghai` |
| `cos.baseUrl` | 腾讯云COS基础URL | `https://jxcwzk-1316797858.cos.ap-shanghai.myqcloud.com` |

## 获取腾讯云API密钥

1. **登录腾讯云控制台**：访问 https://console.cloud.tencent.com/
2. **进入API密钥管理**：点击左侧导航栏中的 "访问管理" -> "API密钥管理"
3. **创建API密钥**：点击 "新建密钥" 按钮，创建新的API密钥
4. **获取SecretId和SecretKey**：创建成功后，会显示SecretId和SecretKey，复制保存

## 创建COS存储桶

1. **登录腾讯云控制台**：访问 https://console.cloud.tencent.com/
2. **进入COS控制台**：点击左侧导航栏中的 "对象存储" -> "存储桶列表"
3. **创建存储桶**：点击 "创建存储桶" 按钮，填写存储桶信息
   - **存储桶名称**：填写唯一的存储桶名称，如 `jxcwzk-1316797858`
   - **所属地域**：选择存储桶所在的地域，如 "上海"
   - **访问权限**：选择 "公共读私有写" 或 "私有读写"
   - **其他配置**：根据需要配置其他选项
4. **获取存储桶信息**：创建成功后，存储桶列表中会显示新创建的存储桶，记录存储桶名称和地域

## 配置COS基础URL

COS基础URL的格式为：`https://{bucketName}.cos.{region}.myqcloud.com`

例如：
- 存储桶名称：`jxcwzk-1316797858`
- 地域：`ap-shanghai`
- 基础URL：`https://jxcwzk-1316797858.cos.ap-shanghai.myqcloud.com`

## 配置文件示例

在 `application.yml` 文件中添加以下配置：

```yaml
cos:
  secretId: ${COS_SECRET_ID}
  secretKey: ${COS_SECRET_KEY}
  bucketName: jxcwzk-1316797858
  region: ap-shanghai
  baseUrl: https://jxcwzk-1316797858.cos.ap-shanghai.myqcloud.com
```

## 权限配置

1. **存储桶权限**：确保存储桶的访问权限设置正确，允许上传和下载操作
2. **API密钥权限**：确保API密钥具有COS相关的权限，如 `QcloudCOSFullAccess`
3. **跨域设置**：如果前端需要直接访问COS资源，需要配置存储桶的跨域规则

## 测试COS连接

可以使用以下代码测试COS连接：

```java
public void testCosConnection() {
    try {
        boolean bucketExists = cosClient.doesBucketExist(bucketName);
        System.out.println("存储桶 " + bucketName + " 是否存在: " + bucketExists);
    } catch (Exception e) {
        System.out.println("COS连接测试失败: " + e.getMessage());
        e.printStackTrace();
    }
}
```

## 常见问题

1. **SecretId和SecretKey错误**：检查API密钥是否正确，是否有COS相关的权限
2. **存储桶不存在**：检查存储桶名称是否正确，是否已创建
3. **地域错误**：检查region配置是否正确，与存储桶的地域一致
4. **访问权限错误**：检查存储桶的访问权限是否正确，API密钥是否有COS相关的权限
5. **网络连接错误**：检查网络连接是否正常，是否可以访问腾讯云API

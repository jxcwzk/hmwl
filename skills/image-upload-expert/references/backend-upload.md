# 后端上传API实现文档

## API接口

### 1. 本地上传接口

**URL**: `/api/order/image/upload`
**方法**: POST
**参数**:
- `orderId`: 订单ID
- `imageType`: 图片类型
- `file`: 图片文件

**响应**:
```json
{
  "id": 1,
  "orderId": 1,
  "imageUrl": "http://localhost:8081/api/upload/xxx.png",
  "imageType": 1,
  "status": 1,
  "createTime": "2026-03-09T12:00:00",
  "updateTime": "2026-03-09T12:00:00"
}
```

### 2. 腾讯云COS上传接口

**URL**: `/api/order/image/upload/cos`
**方法**: POST
**参数**:
- `orderId`: 订单ID
- `orderNo`: 订单编号
- `imageType`: 图片类型
- `file`: 图片文件

**响应**:
```json
{
  "id": 1,
  "orderId": 1,
  "imageUrl": "https://bucketName.cos.region.myqcloud.com/order/orderNo/order_orderNo_timestamp.png",
  "imageType": 1,
  "status": 1,
  "createTime": "2026-03-09T12:00:00",
  "updateTime": "2026-03-09T12:00:00"
}
```

### 3. 获取图片列表接口

**URL**: `/api/order/image/list`
**方法**: GET
**参数**:
- `orderId`: 订单ID

**响应**:
```json
[
  {
    "id": 1,
    "orderId": 1,
    "imageUrl": "http://localhost:8081/api/upload/xxx.png",
    "imageType": 1,
    "status": 1,
    "createTime": "2026-03-09T12:00:00",
    "updateTime": "2026-03-09T12:00:00"
  }
]
```

## 实现细节

### 1. 本地上传实现

```java
@Override
public OrderImage upload(Long orderId, Integer imageType, MultipartFile file) throws Exception {
    // 1. 生成唯一文件名
    String originalFilename = file.getOriginalFilename();
    String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
    String filename = UUID.randomUUID().toString() + suffix;

    // 2. 创建上传目录
    File uploadDir = new File(uploadPath);
    if (!uploadDir.exists()) {
        uploadDir.mkdirs();
    }

    // 3. 保存文件
    File dest = new File(uploadPath + File.separator + filename);
    file.transferTo(dest);

    // 4. 构建图片URL
    String imageUrl = uploadUrl + "/" + filename;

    // 5. 保存到数据库
    OrderImage orderImage = new OrderImage();
    orderImage.setOrderId(orderId);
    orderImage.setImageType(imageType);
    orderImage.setImageUrl(imageUrl);
    orderImage.setStatus(1);
    orderImage.setCreateTime(new java.util.Date());
    orderImage.setUpdateTime(new java.util.Date());
    save(orderImage);

    return orderImage;
}
```

### 2. 腾讯云COS上传实现

```java
@Override
public OrderImage uploadToCos(Long orderId, String orderNo, Integer imageType, MultipartFile file) throws Exception {
    // 1. 生成文件名
    String originalFilename = file.getOriginalFilename();
    String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
    String timestamp = String.valueOf(System.currentTimeMillis());
    String filename = "order/" + orderNo + "/" + "order_" + orderNo + "_" + timestamp + suffix;

    // 2. 上传到COS
    File tempFile = null;
    try {
        tempFile = File.createTempFile("temp", suffix);
        file.transferTo(tempFile);
        
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, filename, tempFile);
        PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
        
        // 3. 删除临时文件
        tempFile.delete();

        // 4. 构建图片URL
        String imageUrl = baseUrl + "/" + filename;

        // 5. 保存到数据库
        OrderImage orderImage = new OrderImage();
        orderImage.setOrderId(orderId);
        orderImage.setImageType(imageType);
        orderImage.setImageUrl(imageUrl);
        orderImage.setStatus(1);
        orderImage.setCreateTime(new java.util.Date());
        orderImage.setUpdateTime(new java.util.Date());
        save(orderImage);

        return orderImage;
    } catch (Exception e) {
        if (tempFile != null) {
            tempFile.delete();
        }
        // 改为返回本地上传的结果，作为降级方案
        return upload(orderId, imageType, file);
    }
}
```

### 3. 获取图片列表实现

```java
@Override
public List<OrderImage> listByOrderId(Long orderId) {
    return baseMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<OrderImage>()
            .eq(OrderImage::getOrderId, orderId)
            .eq(OrderImage::getStatus, 1));
}
```

## 配置项

| 配置项 | 说明 | 默认值 |
| --- | --- | --- |
| `file.upload.path` | 本地上传目录 | `D:/hmwl-4/upload` |
| `file.upload.url` | 本地上传URL | `http://localhost:8081/api/upload` |
| `cos.secretId` | 腾讯云COS SecretId | - |
| `cos.secretKey` | 腾讯云COS SecretKey | - |
| `cos.bucketName` | 腾讯云COS存储桶名称 | - |
| `cos.region` | 腾讯云COS区域 | `ap-shanghai` |
| `cos.baseUrl` | 腾讯云COS基础URL | `https://bucketName.cos.region.myqcloud.com` |

## 注意事项

1. **文件类型**：只支持jpg和png格式的图片
2. **文件大小**：图片大小不能超过10MB
3. **上传目录**：确保本地上传目录存在，且有写入权限
4. **COS配置**：确保腾讯云COS配置正确，包括SecretId、SecretKey、BucketName和Region
5. **错误处理**：上传失败时会返回本地上传的结果，作为降级方案
6. **数据库**：确保数据库已初始化，包含order_image表

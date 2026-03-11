# Spring Boot集成腾讯云COS

## 1. 环境准备

### 1.1 依赖添加

在`pom.xml`文件中添加腾讯云COS SDK依赖：

```xml
<dependency>
    <groupId>com.qcloud</groupId>
    <artifactId>cos_api</artifactId>
    <version>5.6.88</version>
</dependency>
```

### 1.2 配置文件

在`application.yml`文件中添加COS相关配置：

```yaml
cos:
  secretId: your-secret-id
  secretKey: your-secret-key
  bucketName: your-bucket-name
  region: ap-guangzhou
  baseUrl: https://your-bucket-name.cos.ap-guangzhou.myqcloud.com
```

## 2. COS客户端配置

### 2.1 创建配置类

创建`CosConfig.java`配置类：

```java
package com.hmwl.config;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.region.Region;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CosConfig {
    @Value("${cos.secretId}")
    private String secretId;
    
    @Value("${cos.secretKey}")
    private String secretKey;
    
    @Value("${cos.region}")
    private String region;
    
    @Bean
    public COSClient cosClient() {
        BasicCOSCredentials credentials = new BasicCOSCredentials(secretId, secretKey);
        return new COSClient(credentials, new Region(region));
    }
}
```

## 3. 图片上传服务

### 3.1 创建服务接口

创建`OrderImageService.java`接口：

```java
package com.hmwl.service;

import com.hmwl.entity.OrderImage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface OrderImageService extends IService<OrderImage> {
    /**
     * 上传图片到COS
     */
    OrderImage uploadToCos(Long orderId, String orderNo, Integer imageType, MultipartFile file) throws Exception;

    /**
     * 根据订单ID获取图片列表
     */
    List<OrderImage> listByOrderId(Long orderId);
}
```

### 3.2 实现服务类

创建`OrderImageServiceImpl.java`实现类：

```java
package com.hmwl.service.impl;

import com.hmwl.entity.OrderImage;
import com.hmwl.mapper.OrderImageMapper;
import com.hmwl.service.OrderImageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class OrderImageServiceImpl extends ServiceImpl<OrderImageMapper, OrderImage> implements OrderImageService {

    @Autowired
    private COSClient cosClient;
    
    @Value("${cos.bucketName}")
    private String bucketName;
    
    @Value("${cos.baseUrl}")
    private String baseUrl;

    @Override
    public OrderImage uploadToCos(Long orderId, String orderNo, Integer imageType, MultipartFile file) throws Exception {
        // 1. 生成文件名
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String timestamp = String.valueOf(System.currentTimeMillis());
        String filename = "order/" + orderNo + "/" + "order_" + orderNo + "_" + timestamp + suffix;

        // 2. 上传到COS
        File tempFile = File.createTempFile("temp", suffix);
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
    }

    @Override
    public List<OrderImage> listByOrderId(Long orderId) {
        return lambdaQuery().eq(OrderImage::getOrderId, orderId)
                .eq(OrderImage::getStatus, 1)
                .list();
    }
}
```

## 4. API接口实现

### 4.1 创建控制器

创建`OrderImageController.java`控制器：

```java
package com.hmwl.controller;

import com.hmwl.entity.OrderImage;
import com.hmwl.service.OrderImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/order/image")
public class OrderImageController {

    @Autowired
    private OrderImageService orderImageService;

    /**
     * 上传图片到COS
     */
    @PostMapping(value = "/upload/cos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result uploadToCos(@RequestParam("orderId") Long orderId,
                             @RequestParam("orderNo") String orderNo,
                             @RequestParam("imageType") Integer imageType,
                             @RequestParam("file") MultipartFile file) {
        try {
            OrderImage orderImage = orderImageService.uploadToCos(orderId, orderNo, imageType, file);
            return Result.success(orderImage);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取订单图片列表
     */
    @GetMapping(value = "/list")
    public Result list(@RequestParam("orderId") Long orderId) {
        try {
            List<OrderImage> list = orderImageService.listByOrderId(orderId);
            return Result.success(list);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除图片
     */
    @DeleteMapping(value = "/{id}")
    public Result delete(@PathVariable Long id) {
        try {
            boolean result = orderImageService.removeById(id);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取图片详情
     */
    @GetMapping(value = "/{id}")
    public Result getById(@PathVariable Long id) {
        try {
            OrderImage orderImage = orderImageService.getById(id);
            return Result.success(orderImage);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
```

## 5. 错误处理

### 5.1 全局异常处理

创建`GlobalExceptionHandler.java`全局异常处理类：

```java
package com.hmwl.exception;

import com.hmwl.controller.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        return Result.error(e.getMessage());
    }
}
```

## 6. 测试

### 6.1 测试上传接口

使用Postman或其他API测试工具测试上传接口：

- **请求URL**：`http://localhost:8081/api/order/image/upload/cos`
- **请求方法**：POST
- **请求参数**：
  - orderId：订单ID
  - orderNo：订单编号
  - imageType：图片类型（1-回单，2-发货单，3-其他）
  - file：图片文件

### 6.2 测试获取图片列表接口

- **请求URL**：`http://localhost:8081/api/order/image/list?orderId=1`
- **请求方法**：GET

## 7. 最佳实践

### 7.1 性能优化

1. **使用连接池**：配置COS客户端连接池，提高并发处理能力
2. **异步上传**：对于大文件，使用异步上传方式，避免阻塞主线程
3. **分片上传**：对于大文件，使用分片上传，提高上传速度和可靠性

### 7.2 安全性

1. **密钥管理**：将API密钥存储在安全的地方，避免硬编码
2. **权限控制**：设置合理的COS存储桶权限，确保数据安全
3. **HTTPS**：使用HTTPS协议传输数据，确保数据传输安全

### 7.3 可靠性

1. **错误处理**：实现完善的错误处理机制，确保上传过程的可靠性
2. **重试机制**：对于网络错误，实现自动重试机制
3. **日志记录**：记录上传过程的日志，便于排查问题
# 订单图片API设计

## 1. 后端API接口设计

### 1.1 图片上传接口

**接口路径**：`/order/image/upload`
**请求方式**：`POST`
**请求参数**：
- `orderId`：订单ID（FormData）
- `imageType`：图片类型（1-回单，2-发货单，3-其他）（FormData）
- `file`：图片文件（FormData，multipart/form-data）

**响应格式**：
```json
{
  "code": 200,
  "message": "上传成功",
  "data": {
    "id": 1,
    "orderId": 123,
    "imageUrl": "https://example.com/images/123.jpg",
    "imageType": 1,
    "createTime": "2024-01-01 12:00:00"
  }
}
```

### 1.2 获取订单图片列表接口

**接口路径**：`/order/image/list`
**请求方式**：`GET`
**请求参数**：
- `orderId`：订单ID（Query参数）

**响应格式**：
```json
{
  "code": 200,
  "message": "获取成功",
  "data": [
    {
      "id": 1,
      "orderId": 123,
      "imageUrl": "https://example.com/images/123.jpg",
      "imageType": 1,
      "imageTypeName": "回单",
      "createTime": "2024-01-01 12:00:00"
    },
    {
      "id": 2,
      "orderId": 123,
      "imageUrl": "https://example.com/images/456.jpg",
      "imageType": 2,
      "imageTypeName": "发货单",
      "createTime": "2024-01-01 13:00:00"
    }
  ]
}
```

### 1.3 删除图片接口

**接口路径**：`/order/image/{id}`
**请求方式**：`DELETE`
**请求参数**：
- `id`：图片ID（Path参数）

**响应格式**：
```json
{
  "code": 200,
  "message": "删除成功",
  "data": true
}
```

### 1.4 获取图片详情接口

**接口路径**：`/order/image/{id}`
**请求方式**：`GET`
**请求参数**：
- `id`：图片ID（Path参数）

**响应格式**：
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "id": 1,
    "orderId": 123,
    "imageUrl": "https://example.com/images/123.jpg",
    "imageType": 1,
    "imageTypeName": "回单",
    "createTime": "2024-01-01 12:00:00"
  }
}
```

## 2. 后端实现代码

### 2.1 OrderImageController.java

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
     * 上传图片
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result upload(@RequestParam("orderId") Long orderId,
                         @RequestParam("imageType") Integer imageType,
                         @RequestParam("file") MultipartFile file) {
        try {
            OrderImage orderImage = orderImageService.upload(orderId, imageType, file);
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

### 2.2 OrderImageService.java

```java
package com.hmwl.service;

import com.hmwl.entity.OrderImage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface OrderImageService extends IService<OrderImage> {
    /**
     * 上传图片
     */
    OrderImage upload(Long orderId, Integer imageType, MultipartFile file) throws Exception;

    /**
     * 根据订单ID获取图片列表
     */
    List<OrderImage> listByOrderId(Long orderId);
}
```

### 2.3 OrderImageServiceImpl.java

```java
package com.hmwl.service.impl;

import com.hmwl.entity.OrderImage;
import com.hmwl.mapper.OrderImageMapper;
import com.hmwl.service.OrderImageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class OrderImageServiceImpl extends ServiceImpl<OrderImageMapper, OrderImage> implements OrderImageService {

    @Value("${file.upload.path}")
    private String uploadPath;

    @Value("${file.upload.url}")
    private String uploadUrl;

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

    @Override
    public List<OrderImage> listByOrderId(Long orderId) {
        return lambdaQuery().eq(OrderImage::getOrderId, orderId)
                .eq(OrderImage::getStatus, 1)
                .list();
    }
}
```

### 2.4 OrderImageMapper.java

```java
package com.hmwl.mapper;

import com.hmwl.entity.OrderImage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

public interface OrderImageMapper extends BaseMapper<OrderImage> {
}
```

## 3. 配置文件

### 3.1 application.yml

```yaml
file:
  upload:
    path: D:/hmwl-4/upload  # 本地存储路径
    url: http://localhost:8080/upload  # 访问URL前缀

spring:
  servlet:
    multipart:
      max-file-size: 10MB  # 最大文件大小
      max-request-size: 10MB  # 最大请求大小
```

### 3.2 静态资源配置

在Spring Boot应用中，需要配置静态资源访问，以便能够直接访问上传的图片。

```java
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Value("${file.upload.path}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:" + uploadPath + "/");
    }
}
```
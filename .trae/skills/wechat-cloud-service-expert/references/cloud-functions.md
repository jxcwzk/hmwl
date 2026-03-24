# 云函数开发指南

## 1. 云函数概述

### 1.1 什么是云函数
- **定义**：云函数是运行在云端的代码，无需管理服务器，由微信云开发提供的无服务器计算服务
- **特点**：按需执行、自动扩缩容、免运维
- **适用场景**：数据处理、API调用、定时任务、业务逻辑处理

### 1.2 云函数架构
- **运行环境**：Node.js 10.15.3 及以上版本
- **触发方式**：HTTP 请求、云数据库事件、定时触发器、其他云函数调用
- **执行机制**：冷启动和热启动

## 2. 云函数创建

### 2.1 初始化云函数
1. **打开微信开发者工具**
2. **创建或打开小程序项目**
3. **点击「云开发」按钮**
4. **在云开发控制台中创建云环境**
5. **在项目根目录创建 `cloudfunctions` 文件夹**
6. **在 `project.config.json` 中配置云函数目录**

```json
{
  "cloudfunctionRoot": "cloudfunctions/"
}
```

### 2.2 创建云函数
1. **在 `cloudfunctions` 文件夹上右键**
2. **选择「新建云函数」**
3. **输入云函数名称**
4. **等待云函数初始化完成**

### 2.3 云函数结构
```
cloudfunctions/
└── function-name/
    ├── index.js        # 云函数入口文件
    ├── package.json    # 依赖配置文件
    └── node_modules/   # 依赖包
```

## 3. 云函数开发

### 3.1 入口函数
```javascript
// index.js
exports.main = async (event, context) => {
  // event：包含调用参数和其他信息
  // context：包含运行环境信息
  
  try {
    // 业务逻辑
    return {
      success: true,
      data: '操作成功'
    };
  } catch (error) {
    return {
      success: false,
      error: error.message
    };
  }
};
```

### 3.2 依赖管理
```json
// package.json
{
  "name": "function-name",
  "version": "1.0.0",
  "description": "云函数描述",
  "main": "index.js",
  "dependencies": {
    "wx-server-sdk": "latest"
  }
}
```

### 3.3 常用 API
- **云数据库操作**：`cloud.database()`
- **云存储操作**：`cloud.uploadFile()`, `cloud.downloadFile()`
- **HTTP 请求**：`cloud.request()`
- **获取用户信息**：`context.userInfo`
- **获取环境信息**：`context.environment`

### 3.4 代码规范
- **命名规范**：使用小写字母和下划线
- **代码风格**：遵循 JavaScript 标准风格
- **错误处理**：使用 try-catch 捕获错误
- **日志记录**：使用 `console.log()` 记录日志

## 4. 云函数部署

### 4.1 本地调试
1. **在云函数文件上右键**
2. **选择「本地调试」**
3. **输入测试参数**
4. **查看调试结果**

### 4.2 云端部署
1. **在云函数文件上右键**
2. **选择「上传并部署」**
3. **等待部署完成**
4. **在云开发控制台查看部署状态**

### 4.3 版本管理
- **版本号**：每次部署会生成新的版本号
- **别名管理**：可以为版本设置别名，如 `prod`、`dev`
- **流量分配**：可以为不同版本分配流量

## 5. 云函数优化

### 5.1 冷启动优化
- **减少依赖包**：只安装必要的依赖
- **优化代码结构**：减少代码体积
- **使用预热机制**：定期调用云函数保持热状态

### 5.2 内存配置
- **根据实际需求配置内存**：内存越大，CPU 性能越好
- **监控内存使用**：根据监控结果调整内存配置

### 5.3 超时设置
- **根据业务需求设置超时时间**：默认 3 秒
- **避免长时间运行**：将长时间任务拆分为多个云函数

## 6. 云函数安全

### 6.1 权限配置
- **设置云函数执行权限**：在云开发控制台配置
- **限制访问来源**：验证请求来源
- **使用环境变量**：存储敏感信息

### 6.2 安全编码
- **输入验证**：验证所有输入参数
- **SQL 注入防护**：使用参数化查询
- **XSS 防护**：过滤用户输入
- **错误处理**：不暴露敏感错误信息

## 7. 常用云函数模板

### 7.1 数据处理函数
```javascript
// 处理订单数据
exports.main = async (event, context) => {
  const db = cloud.database();
  const { orderId, status } = event;
  
  try {
    await db.collection('orders').doc(orderId).update({
      data: {
        status: status,
        updatedAt: new Date()
      }
    });
    
    return {
      success: true,
      message: '订单状态更新成功'
    };
  } catch (error) {
    return {
      success: false,
      error: error.message
    };
  }
};
```

### 7.2 HTTP 请求函数
```javascript
// 调用第三方 API
exports.main = async (event, context) => {
  const cloud = require('wx-server-sdk');
  cloud.init();
  
  const { url, method, data } = event;
  
  try {
    const result = await cloud.request({
      url: url,
      method: method || 'GET',
      data: data
    });
    
    return {
      success: true,
      data: result.data
    };
  } catch (error) {
    return {
      success: false,
      error: error.message
    };
  }
};
```

### 7.3 定时任务函数
```javascript
// 定时清理过期数据
exports.main = async (event, context) => {
  const db = cloud.database();
  const now = new Date();
  
  try {
    // 清理 30 天前的日志数据
    const thirtyDaysAgo = new Date(now.getTime() - 30 * 24 * 60 * 60 * 1000);
    
    await db.collection('logs').where({
      createdAt: {
        $lt: thirtyDaysAgo
      }
    }).remove();
    
    return {
      success: true,
      message: '清理过期数据成功'
    };
  } catch (error) {
    return {
      success: false,
      error: error.message
    };
  }
};
```

## 8. 调试和监控

### 8.1 日志管理
- **查看云函数日志**：在云开发控制台查看
- **自定义日志**：使用 `console.log()` 记录关键信息
- **日志级别**：info、warn、error

### 8.2 监控指标
- **调用次数**：云函数被调用的次数
- **执行时间**：云函数的执行时间
- **错误率**：云函数执行失败的比例
- **内存使用**：云函数的内存使用情况

### 8.3 告警配置
- **设置告警规则**：在云开发控制台配置
- **告警通知**：通过微信公众号通知
- **告警级别**：警告、严重

## 9. 最佳实践

### 9.1 云函数设计
- **单一职责**：每个云函数只负责一个功能
- **函数拆分**：将复杂逻辑拆分为多个云函数
- **参数验证**：严格验证输入参数

### 9.2 性能优化
- **缓存策略**：使用 Redis 或内存缓存
- **批量操作**：减少数据库操作次数
- **异步处理**：使用 Promise.all 并行处理

### 9.3 安全最佳实践
- **最小权限原则**：只授予必要的权限
- **数据加密**：对敏感数据进行加密
- **定期审计**：定期检查云函数安全

## 10. 常见问题

### 10.1 冷启动时间长
- **原因**：首次调用或长时间未调用时需要初始化环境
- **解决方案**：减少依赖包、优化代码结构、使用预热机制

### 10.2 内存不足
- **原因**：云函数内存配置不足或处理大量数据
- **解决方案**：增加内存配置、分批处理数据

### 10.3 超时错误
- **原因**：云函数执行时间超过设置的超时时间
- **解决方案**：优化代码、增加超时时间、拆分任务

### 10.4 权限错误
- **原因**：云函数没有足够的权限执行操作
- **解决方案**：在云开发控制台配置正确的权限

### 10.5 依赖安装失败
- **原因**：网络问题或依赖包不兼容
- **解决方案**：检查网络连接、使用兼容的依赖版本
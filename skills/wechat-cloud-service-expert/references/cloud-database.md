# 云数据库开发指南

## 1. 云数据库概述

### 1.1 什么是云数据库
- **定义**：微信云开发提供的 NoSQL 数据库服务，基于 MongoDB 构建
- **特点**：无需管理数据库服务器、自动扩缩容、高可用
- **适用场景**：存储用户数据、业务数据、配置数据等

### 1.2 数据模型
- **集合（Collection）**：类似于关系型数据库的表
- **文档（Document）**：类似于关系型数据库的行
- **字段（Field）**：类似于关系型数据库的列

## 2. 数据库初始化

### 2.1 配置数据库
1. **在微信开发者工具中初始化云开发**
2. **在云开发控制台中创建数据库集合**
3. **设置集合的读写权限**

### 2.2 权限设置
- **仅创建者可读写**：只有创建文档的用户可以读写
- **所有用户可读，仅创建者可写**：所有用户可以读取，只有创建者可以修改
- **所有用户可读，仅管理员可写**：所有用户可以读取，只有管理员可以修改
- **仅管理员可读写**：只有管理员可以读写

### 2.3 索引管理
- **创建索引**：在云开发控制台中为集合创建索引
- **索引类型**：单字段索引、复合索引、地理索引
- **索引优化**：根据查询需求创建合适的索引

## 3. 数据库操作

### 3.1 初始化数据库
```javascript
// 前端初始化
wx.cloud.init({
  env: 'your-env-id'
});

const db = wx.cloud.database();

// 云函数初始化
const cloud = require('wx-server-sdk');
cloud.init({
  env: cloud.DYNAMIC_CURRENT_ENV
});

const db = cloud.database();
```

### 3.2 插入数据
```javascript
// 插入单条数据
const result = await db.collection('users').add({
  data: {
    name: '张三',
    age: 25,
    createdAt: new Date()
  }
});

// 插入多条数据
const result = await db.collection('users').add({
  data: [
    {
      name: '张三',
      age: 25,
      createdAt: new Date()
    },
    {
      name: '李四',
      age: 30,
      createdAt: new Date()
    }
  ]
});
```

### 3.3 查询数据
```javascript
// 查询所有数据
const result = await db.collection('users').get();

// 条件查询
const result = await db.collection('users').where({
  age: {
    $gt: 20
  }
}).get();

// 排序
const result = await db.collection('users').orderBy('age', 'desc').get();

// 分页
const result = await db.collection('users').skip(10).limit(10).get();

// 字段投影
const result = await db.collection('users').field({
  name: true,
  age: true
}).get();
```

### 3.4 更新数据
```javascript
// 更新单条数据
const result = await db.collection('users').doc('document-id').update({
  data: {
    age: 26,
    updatedAt: new Date()
  }
});

// 条件更新
const result = await db.collection('users').where({
  age: {
    $lt: 18
  }
}).update({
  data: {
    status: '未成年'
  }
});

// 原子操作
const result = await db.collection('users').doc('document-id').update({
  data: {
    age: db.command.inc(1), // 自增
    tags: db.command.push('new-tag') // 数组添加元素
  }
});
```

### 3.5 删除数据
```javascript
// 删除单条数据
const result = await db.collection('users').doc('document-id').remove();

// 条件删除
const result = await db.collection('users').where({
  age: {
    $gt: 100
  }
}).remove();
```

## 4. 查询操作符

### 4.1 比较操作符
- `$eq`：等于
- `$neq`：不等于
- `$lt`：小于
- `$lte`：小于等于
- `$gt`：大于
- `$gte`：大于等于
- `$in`：在数组中
- `$nin`：不在数组中

### 4.2 逻辑操作符
- `$and`：与
- `$or`：或
- `$not`：非
- `$nor`：非或

### 4.3 数组操作符
- `$all`：数组包含所有指定元素
- `$elemMatch`：数组中至少有一个元素匹配
- `$size`：数组长度等于指定值

### 4.4 地理位置操作符
- `$geoNear`：按距离排序
- `$geoWithin`：在指定区域内
- `$geoIntersects`：与指定区域相交

## 5. 数据库事务

### 5.1 事务概述
- **定义**：一组原子操作，要么全部成功，要么全部失败
- **适用场景**：转账、订单处理等需要保证数据一致性的场景

### 5.2 事务操作
```javascript
const transaction = await db.startTransaction();

try {
  // 操作1：扣除余额
  await transaction.collection('users').doc('user1').update({
    data: {
      balance: db.command.inc(-100)
    }
  });
  
  // 操作2：增加余额
  await transaction.collection('users').doc('user2').update({
    data: {
      balance: db.command.inc(100)
    }
  });
  
  // 提交事务
  await transaction.commit();
  console.log('事务执行成功');
} catch (error) {
  // 回滚事务
  await transaction.rollback();
  console.error('事务执行失败', error);
}
```

## 6. 数据库优化

### 6.1 查询优化
- **创建索引**：为常用查询字段创建索引
- **优化查询条件**：使用精确查询，避免全表扫描
- **合理使用分页**：限制返回数据量
- **字段投影**：只返回需要的字段

### 6.2 索引优化
- **单字段索引**：适用于单个字段的查询
- **复合索引**：适用于多个字段的查询
- **唯一索引**：确保字段值唯一
- **地理索引**：适用于地理位置查询

### 6.3 数据结构优化
- **合理设计文档结构**：避免嵌套过深
- **使用引用**：对于复杂关系使用引用
- **数据规范化**：避免数据冗余

### 6.4 性能监控
- **查看慢查询**：在云开发控制台查看慢查询日志
- **监控查询性能**：使用 `explain()` 分析查询执行计划
- **优化热点数据**：对热点数据进行特殊处理

## 7. 数据迁移

### 7.1 导入数据
- **CSV 导入**：从 CSV 文件导入数据
- **JSON 导入**：从 JSON 文件导入数据
- **API 导入**：通过 API 导入数据

### 7.2 导出数据
- **CSV 导出**：导出数据为 CSV 文件
- **JSON 导出**：导出数据为 JSON 文件
- **API 导出**：通过 API 导出数据

### 7.3 数据同步
- **定时同步**：定期同步数据
- **实时同步**：使用触发器实时同步数据

## 8. 数据库安全

### 8.1 权限管理
- **设置集合权限**：根据业务需求设置合适的权限
- **使用安全规则**：定义数据访问规则
- **限制数据访问**：只允许授权用户访问数据

### 8.2 数据加密
- **字段加密**：对敏感字段进行加密
- **传输加密**：使用 HTTPS 加密传输
- **存储加密**：对存储的数据进行加密

### 8.3 防止注入攻击
- **使用参数化查询**：避免直接拼接查询条件
- **输入验证**：验证用户输入
- **使用安全的 API**：使用官方提供的安全 API

## 9. 最佳实践

### 9.1 数据模型设计
- **根据查询需求设计索引**：为常用查询字段创建索引
- **合理使用嵌套**：避免过度嵌套
- **使用引用**：对于复杂关系使用引用

### 9.2 查询优化
- **使用索引覆盖查询**：查询只使用索引字段
- **避免全表扫描**：使用索引查询
- **合理使用聚合**：对于复杂查询使用聚合操作

### 9.3 性能优化
- **批量操作**：减少数据库操作次数
- **缓存策略**：对热点数据使用缓存
- **异步处理**：使用异步操作处理大量数据

### 9.4 安全最佳实践
- **最小权限原则**：只授予必要的权限
- **数据验证**：验证所有用户输入
- **定期审计**：定期检查数据库安全

## 10. 常见问题

### 10.1 查询速度慢
- **原因**：缺少索引、查询条件不合理、数据量过大
- **解决方案**：创建合适的索引、优化查询条件、使用分页查询

### 10.2 数据量过大
- **原因**：数据持续增长，没有定期清理
- **解决方案**：数据分片、定期清理过期数据、使用归档

### 10.3 索引失效
- **原因**：查询条件与索引不匹配、使用了不支持索引的操作符
- **解决方案**：优化查询条件、创建合适的索引

### 10.4 权限错误
- **原因**：集合权限设置不当、用户没有足够的权限
- **解决方案**：设置正确的集合权限、确保用户有足够的权限

### 10.5 事务失败
- **原因**：事务操作超时、数据冲突、权限不足
- **解决方案**：优化事务操作、处理数据冲突、确保有足够的权限
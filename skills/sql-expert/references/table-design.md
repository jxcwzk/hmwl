# 表结构设计指南

## 1. 表结构设计原则

### 1.1 数据库设计范式

#### 第一范式 (1NF)
- 每个列都是原子的，不可再分
- 每一行数据都是唯一的

**示例**：
```sql
-- 不符合1NF
CREATE TABLE users (
    id INT,
    name VARCHAR(100),
    contact VARCHAR(200) -- 包含电话和邮箱，可再分
);

-- 符合1NF
CREATE TABLE users (
    id INT,
    name VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(100)
);
```

#### 第二范式 (2NF)
- 满足1NF
- 非主键列完全依赖于主键

**示例**：
```sql
-- 不符合2NF
CREATE TABLE orders (
    order_id INT,
    product_id INT,
    product_name VARCHAR(100), -- 依赖于product_id，而非order_id
    PRIMARY KEY (order_id, product_id)
);

-- 符合2NF
CREATE TABLE orders (
    order_id INT,
    product_id INT,
    PRIMARY KEY (order_id, product_id)
);

CREATE TABLE products (
    product_id INT PRIMARY KEY,
    product_name VARCHAR(100)
);
```

#### 第三范式 (3NF)
- 满足2NF
- 非主键列不依赖于其他非主键列

**示例**：
```sql
-- 不符合3NF
CREATE TABLE orders (
    order_id INT PRIMARY KEY,
    customer_id INT,
    customer_name VARCHAR(100), -- 依赖于customer_id，而非order_id
    total_amount DECIMAL(10,2)
);

-- 符合3NF
CREATE TABLE orders (
    order_id INT PRIMARY KEY,
    customer_id INT,
    total_amount DECIMAL(10,2)
);

CREATE TABLE customers (
    customer_id INT PRIMARY KEY,
    customer_name VARCHAR(100)
);
```

### 1.2 表结构设计最佳实践

- **表名**：使用小写字母，单词之间用下划线分隔，避免使用保留字
- **列名**：使用小写字母，单词之间用下划线分隔，避免使用保留字
- **主键**：每个表都应该有主键，推荐使用自增整数
- **外键**：使用外键保持数据一致性
- **约束**：合理使用约束，如NOT NULL、UNIQUE、DEFAULT等
- **数据类型**：选择合适的数据类型，避免使用过大的数据类型
- **默认值**：为适当的列设置默认值
- **时间戳**：为表添加create_time和update_time字段

## 2. 字段类型选择

### 2.1 数值类型

| 类型 | 大小 | 范围 | 用途 |
|------|------|------|------|
| TINYINT | 1字节 | -128 到 127 | 小整数值 |
| SMALLINT | 2字节 | -32768 到 32767 | 小整数值 |
| INT | 4字节 | -2147483648 到 2147483647 | 整数值 |
| BIGINT | 8字节 | -9223372036854775808 到 9223372036854775807 | 大整数值 |
| DECIMAL | 可变 | 取决于精度 | 精确小数 |
| FLOAT | 4字节 | -3.402823466E+38 到 3.402823466E+38 | 单精度浮点数 |
| DOUBLE | 8字节 | -1.7976931348623157E+308 到 1.7976931348623157E+308 | 双精度浮点数 |

**示例**：
```sql
-- 年龄使用TINYINT
age TINYINT,

-- 订单金额使用DECIMAL
amount DECIMAL(10,2),

-- 用户ID使用INT
user_id INT,

-- 大整数使用BIGINT
big_number BIGINT
```

### 2.2 字符串类型

| 类型 | 大小 | 用途 |
|------|------|------|
| CHAR | 0-255字节 | 固定长度字符串 |
| VARCHAR | 0-65535字节 | 可变长度字符串 |
| TEXT | 0-65535字节 | 长文本 |
| MEDIUMTEXT | 0-16777215字节 | 中等长度文本 |
| LONGTEXT | 0-4294967295字节 | 长文本 |

**示例**：
```sql
-- 固定长度字符串使用CHAR
gender CHAR(1),

-- 可变长度字符串使用VARCHAR
name VARCHAR(50),
email VARCHAR(100),

-- 长文本使用TEXT
description TEXT,

-- 超长文本使用LONGTEXT
content LONGTEXT
```

### 2.3 日期时间类型

| 类型 | 大小 | 范围 | 用途 |
|------|------|------|------|
| DATE | 3字节 | 1000-01-01 到 9999-12-31 | 日期 |
| TIME | 3字节 | -838:59:59 到 838:59:59 | 时间 |
| DATETIME | 8字节 | 1000-01-01 00:00:00 到 9999-12-31 23:59:59 | 日期时间 |
| TIMESTAMP | 4字节 | 1970-01-01 00:00:01 到 2038-01-19 03:14:07 | 时间戳 |
| YEAR | 1字节 | 1901 到 2155 | 年份 |

**示例**：
```sql
-- 出生日期使用DATE
birthdate DATE,

-- 时间点使用DATETIME
create_time DATETIME,
update_time DATETIME,

-- 时间戳使用TIMESTAMP
timestamp TIMESTAMP,

-- 年份使用YEAR
year YEAR
```

### 2.4 其他类型

| 类型 | 大小 | 用途 |
|------|------|------|
| BOOLEAN | 1字节 | 布尔值 |
| ENUM | 1-2字节 | 枚举值 |
| SET | 1-8字节 | 集合值 |
| BLOB | 0-65535字节 | 二进制数据 |
| MEDIUMBLOB | 0-16777215字节 | 中等二进制数据 |
| LONGBLOB | 0-4294967295字节 | 长二进制数据 |

**示例**：
```sql
-- 布尔值使用BOOLEAN
active BOOLEAN,

-- 枚举值使用ENUM
status ENUM('active', 'inactive', 'deleted'),

-- 集合值使用SET
tags SET('tag1', 'tag2', 'tag3'),

-- 二进制数据使用BLOB
image BLOB
```

## 3. 主键和外键设计

### 3.1 主键设计

- **自增主键**：推荐使用自增整数作为主键，性能好，占用空间小
- **复合主键**：当单一列无法唯一标识记录时使用
- **UUID**：当需要分布式系统或避免ID冲突时使用

**示例**：
```sql
-- 自增主键
id INT PRIMARY KEY AUTO_INCREMENT,

-- 复合主键
PRIMARY KEY (order_id, product_id),

-- UUID主键
id CHAR(36) PRIMARY KEY DEFAULT UUID()
```

### 3.2 外键设计

- **外键约束**：使用外键约束保持数据一致性
- **级联操作**：根据业务需求设置级联操作
- **索引**：外键列应该创建索引

**示例**：
```sql
-- 外键约束
customer_id INT,
FOREIGN KEY (customer_id) REFERENCES customers(id),

-- 级联操作
FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE,

-- 外键索引
CREATE INDEX idx_customer_id ON orders(customer_id);
```

## 4. 约束设计

### 4.1 NOT NULL约束
- 确保列不能为空
- 提高数据完整性

**示例**：
```sql
name VARCHAR(50) NOT NULL,
email VARCHAR(100) NOT NULL
```

### 4.2 UNIQUE约束
- 确保列值唯一
- 可以用于多个列的组合

**示例**：
```sql
email VARCHAR(100) UNIQUE,
UNIQUE (name, age)
```

### 4.3 DEFAULT约束
- 为列设置默认值
- 减少应用程序代码

**示例**：
```sql
status ENUM('active', 'inactive') DEFAULT 'active',
create_time DATETIME DEFAULT CURRENT_TIMESTAMP
```

### 4.4 CHECK约束
- 检查列值是否满足条件
- 提高数据完整性

**示例**：
```sql
age INT CHECK (age >= 0),
amount DECIMAL(10,2) CHECK (amount > 0)
```

## 5. 表结构设计示例

### 5.1 用户表

```sql
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20),
    gender ENUM('male', 'female', 'other'),
    age TINYINT,
    status ENUM('active', 'inactive', 'deleted') DEFAULT 'active',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### 5.2 订单表

```sql
CREATE TABLE orders (
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(32) NOT NULL UNIQUE,
    user_id INT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    status ENUM('pending', 'paid', 'shipped', 'delivered', 'cancelled') DEFAULT 'pending',
    payment_method ENUM('credit_card', 'paypal', 'cash'),
    shipping_address TEXT NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

### 5.3 订单明细表

```sql
CREATE TABLE order_items (
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    unit_price DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);
```

### 5.4 产品表

```sql
CREATE TABLE products (
    id INT PRIMARY KEY AUTO_INCREMENT,
    product_name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    category_id INT,
    status ENUM('active', 'inactive') DEFAULT 'active',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id)
);
```

### 5.5 分类表

```sql
CREATE TABLE categories (
    id INT PRIMARY KEY AUTO_INCREMENT,
    category_name VARCHAR(50) NOT NULL,
    parent_id INT,
    status ENUM('active', 'inactive') DEFAULT 'active',
    FOREIGN KEY (parent_id) REFERENCES categories(id)
);
```

## 6. 表结构优化

### 6.1 规范化与反规范化

**规范化**：
- 优点：减少数据冗余，提高数据一致性
- 缺点：查询可能需要多个表连接，性能可能降低

**反规范化**：
- 优点：减少表连接，提高查询性能
- 缺点：增加数据冗余，可能导致数据不一致

**平衡策略**：
- 核心业务表使用规范化设计
- 报表和分析表可以使用反规范化设计
- 适当使用冗余字段提高查询性能

### 6.2 分区表

- **水平分区**：将表数据按行划分到不同的分区
- **垂直分区**：将表数据按列划分到不同的分区

**示例**：
```sql
-- 水平分区
CREATE TABLE orders (
    id INT PRIMARY KEY,
    order_date DATE
)
PARTITION BY RANGE (YEAR(order_date)) (
    PARTITION p2020 VALUES LESS THAN (2021),
    PARTITION p2021 VALUES LESS THAN (2022),
    PARTITION p2022 VALUES LESS THAN (2023)
);
```

### 6.3 临时表和视图

**临时表**：
- 用于存储中间结果
- 会话结束后自动删除

**视图**：
- 简化复杂查询
- 提供数据访问控制

**示例**：
```sql
-- 临时表
CREATE TEMPORARY TABLE temp_users AS SELECT * FROM users WHERE age > 18;

-- 视图
CREATE VIEW active_users AS SELECT * FROM users WHERE status = 'active';
```

## 7. 表结构设计工具

- **MySQL Workbench**：可视化数据库设计工具
- **Navicat**：数据库管理和设计工具
- **ERwin**：专业的数据库设计工具
- **Lucidchart**：在线图表和ER图工具
- **draw.io**：免费的在线图表工具

## 8. 常见表结构问题及解决方案

### 8.1 字段类型过大

**问题**：使用了过大的字段类型，浪费存储空间
**解决方案**：选择合适的字段类型

**示例**：
```sql
-- 优化前
name VARCHAR(255), -- 过大

-- 优化后
name VARCHAR(50), -- 合适的长度
```

### 8.2 缺少索引

**问题**：查询性能差
**解决方案**：为经常查询的列创建索引

**示例**：
```sql
-- 为user_id创建索引
CREATE INDEX idx_user_id ON orders(user_id);
```

### 8.3 表结构设计不合理

**问题**：表结构不符合范式，数据冗余
**解决方案**：重新设计表结构，遵循数据库设计范式

**示例**：
```sql
-- 优化前
CREATE TABLE orders (
    order_id INT PRIMARY KEY,
    customer_id INT,
    customer_name VARCHAR(100), -- 冗余
    product_id INT,
    product_name VARCHAR(100) -- 冗余
);

-- 优化后
CREATE TABLE orders (
    order_id INT PRIMARY KEY,
    customer_id INT,
    product_id INT,
    FOREIGN KEY (customer_id) REFERENCES customers(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);
```

### 8.4 缺少约束

**问题**：数据完整性差
**解决方案**：添加适当的约束

**示例**：
```sql
-- 优化前
CREATE TABLE users (
    id INT,
    name VARCHAR(50),
    email VARCHAR(100)
);

-- 优化后
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE
);
```

## 9. 总结

表结构设计是数据库设计的核心部分，直接影响数据库的性能和可维护性。遵循数据库设计范式，选择合适的字段类型，合理设计主键和外键，添加适当的约束，可以创建出高效、可靠的数据库表结构。在实际应用中，需要根据具体的业务需求和性能要求进行适当的调整和优化，平衡规范化和性能之间的关系。
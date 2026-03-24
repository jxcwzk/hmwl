# SQL语法参考

## 1. SQL基础语法

### 1.1 数据定义语言 (DDL)

#### 1.1.1 创建表

```sql
CREATE TABLE table_name (
    column1 datatype constraint,
    column2 datatype constraint,
    ...
);
```

**示例**：
```sql
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    age INT,
    email VARCHAR(100) UNIQUE NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

#### 1.1.2 修改表

```sql
ALTER TABLE table_name
ADD column_name datatype constraint;

ALTER TABLE table_name
MODIFY column_name datatype constraint;

ALTER TABLE table_name
DROP column_name;
```

**示例**：
```sql
ALTER TABLE users
ADD phone VARCHAR(20);

ALTER TABLE users
MODIFY age INT NOT NULL;

ALTER TABLE users
DROP phone;
```

#### 1.1.3 删除表

```sql
DROP TABLE table_name;
```

**示例**：
```sql
DROP TABLE users;
```

### 1.2 数据操作语言 (DML)

#### 1.2.1 插入数据

```sql
INSERT INTO table_name (column1, column2, ...) VALUES (value1, value2, ...);
```

**示例**：
```sql
INSERT INTO users (name, age, email) VALUES ('John', 25, 'john@example.com');
```

#### 1.2.2 更新数据

```sql
UPDATE table_name SET column1 = value1, column2 = value2, ... WHERE condition;
```

**示例**：
```sql
UPDATE users SET age = 26 WHERE id = 1;
```

#### 1.2.3 删除数据

```sql
DELETE FROM table_name WHERE condition;
```

**示例**：
```sql
DELETE FROM users WHERE id = 1;
```

### 1.3 数据查询语言 (DQL)

#### 1.3.1 基本查询

```sql
SELECT column1, column2, ... FROM table_name WHERE condition;
```

**示例**：
```sql
SELECT id, name, email FROM users WHERE age > 18;
```

#### 1.3.2 排序

```sql
SELECT column1, column2, ... FROM table_name ORDER BY column1 ASC|DESC, column2 ASC|DESC, ...;
```

**示例**：
```sql
SELECT id, name, age FROM users ORDER BY age DESC;
```

#### 1.3.3 分组

```sql
SELECT column1, aggregate_function(column2) FROM table_name GROUP BY column1;
```

**示例**：
```sql
SELECT age, COUNT(*) FROM users GROUP BY age;
```

#### 1.3.4 连接

```sql
SELECT column1, column2, ... FROM table1 JOIN table2 ON table1.column = table2.column;
```

**示例**：
```sql
SELECT users.name, orders.order_no FROM users JOIN orders ON users.id = orders.user_id;
```

### 1.4 数据控制语言 (DCL)

#### 1.4.1 授权

```sql
GRANT privilege ON object TO user;
```

**示例**：
```sql
GRANT SELECT, INSERT ON users TO 'user'@'localhost';
```

#### 1.4.2 撤销授权

```sql
REVOKE privilege ON object FROM user;
```

**示例**：
```sql
REVOKE INSERT ON users FROM 'user'@'localhost';
```

## 2. SQL函数

### 2.1 聚合函数

- **COUNT()**：计算行数
- **SUM()**：计算数值总和
- **AVG()**：计算平均值
- **MAX()**：获取最大值
- **MIN()**：获取最小值

**示例**：
```sql
SELECT COUNT(*) FROM users;
SELECT SUM(age) FROM users;
SELECT AVG(age) FROM users;
SELECT MAX(age) FROM users;
SELECT MIN(age) FROM users;
```

### 2.2 字符串函数

- **CONCAT()**：连接字符串
- **SUBSTRING()**：截取字符串
- **LENGTH()**：获取字符串长度
- **LOWER()**：转换为小写
- **UPPER()**：转换为大写

**示例**：
```sql
SELECT CONCAT(name, ' ', email) FROM users;
SELECT SUBSTRING(name, 1, 3) FROM users;
SELECT LENGTH(name) FROM users;
SELECT LOWER(name) FROM users;
SELECT UPPER(name) FROM users;
```

### 2.3 日期函数

- **NOW()**：获取当前日期时间
- **DATE()**：获取日期部分
- **TIME()**：获取时间部分
- **YEAR()**：获取年份
- **MONTH()**：获取月份
- **DAY()**：获取日期

**示例**：
```sql
SELECT NOW();
SELECT DATE(create_time) FROM users;
SELECT TIME(create_time) FROM users;
SELECT YEAR(create_time) FROM users;
SELECT MONTH(create_time) FROM users;
SELECT DAY(create_time) FROM users;
```

## 3. SQL操作符

### 3.1 比较操作符

- **=**：等于
- **<>**：不等于
- **>**：大于
- **<**：小于
- **>=**：大于等于
- **<=**：小于等于
- **LIKE**：模糊匹配
- **IN**：在集合中
- **BETWEEN**：在范围内

**示例**：
```sql
SELECT * FROM users WHERE age = 25;
SELECT * FROM users WHERE age <> 25;
SELECT * FROM users WHERE age > 25;
SELECT * FROM users WHERE age < 25;
SELECT * FROM users WHERE age >= 25;
SELECT * FROM users WHERE age <= 25;
SELECT * FROM users WHERE name LIKE 'J%';
SELECT * FROM users WHERE age IN (20, 25, 30);
SELECT * FROM users WHERE age BETWEEN 20 AND 30;
```

### 3.2 逻辑操作符

- **AND**：逻辑与
- **OR**：逻辑或
- **NOT**：逻辑非

**示例**：
```sql
SELECT * FROM users WHERE age > 18 AND email LIKE '%@example.com';
SELECT * FROM users WHERE age > 18 OR email LIKE '%@example.com';
SELECT * FROM users WHERE NOT age > 18;
```

### 3.3 其他操作符

- **IS NULL**：为空
- **IS NOT NULL**：不为空
- **EXISTS**：存在
- **NOT EXISTS**：不存在

**示例**：
```sql
SELECT * FROM users WHERE email IS NULL;
SELECT * FROM users WHERE email IS NOT NULL;
SELECT * FROM users WHERE EXISTS (SELECT * FROM orders WHERE orders.user_id = users.id);
SELECT * FROM users WHERE NOT EXISTS (SELECT * FROM orders WHERE orders.user_id = users.id);
```

## 4. SQL子查询

### 4.1 标量子查询

```sql
SELECT column1, (SELECT MAX(column2) FROM table2) FROM table1;
```

**示例**：
```sql
SELECT name, (SELECT MAX(age) FROM users) AS max_age FROM users;
```

### 4.2 行子查询

```sql
SELECT column1, column2 FROM table1 WHERE (column1, column2) IN (SELECT column1, column2 FROM table2);
```

**示例**：
```sql
SELECT name, age FROM users WHERE (name, age) IN (SELECT name, age FROM users_backup);
```

### 4.3 表子查询

```sql
SELECT column1, column2 FROM (SELECT column1, column2 FROM table1) AS subquery;
```

**示例**：
```sql
SELECT name, age FROM (SELECT name, age FROM users WHERE age > 18) AS adult_users;
```

## 5. SQL事务

### 5.1 开始事务

```sql
START TRANSACTION;
```

### 5.2 提交事务

```sql
COMMIT;
```

### 5.3 回滚事务

```sql
ROLLBACK;
```

**示例**：
```sql
START TRANSACTION;
UPDATE users SET age = age + 1 WHERE id = 1;
INSERT INTO orders (user_id, order_no) VALUES (1, 'ORD001');
COMMIT;
```

## 6. SQL视图

### 6.1 创建视图

```sql
CREATE VIEW view_name AS SELECT column1, column2, ... FROM table_name WHERE condition;
```

**示例**：
```sql
CREATE VIEW adult_users AS SELECT id, name, email FROM users WHERE age > 18;
```

### 6.2 查询视图

```sql
SELECT * FROM view_name;
```

**示例**：
```sql
SELECT * FROM adult_users;
```

### 6.3 删除视图

```sql
DROP VIEW view_name;
```

**示例**：
```sql
DROP VIEW adult_users;
```

## 7. SQL存储过程

### 7.1 创建存储过程

```sql
DELIMITER //
CREATE PROCEDURE procedure_name(parameter1 datatype, parameter2 datatype, ...)
BEGIN
    -- SQL statements
END //
DELIMITER ;
```

**示例**：
```sql
DELIMITER //
CREATE PROCEDURE get_user(IN user_id INT)
BEGIN
    SELECT * FROM users WHERE id = user_id;
END //
DELIMITER ;
```

### 7.2 调用存储过程

```sql
CALL procedure_name(parameter1, parameter2, ...);
```

**示例**：
```sql
CALL get_user(1);
```

### 7.3 删除存储过程

```sql
DROP PROCEDURE procedure_name;
```

**示例**：
```sql
DROP PROCEDURE get_user;
```

## 8. SQL触发器

### 8.1 创建触发器

```sql
DELIMITER //
CREATE TRIGGER trigger_name BEFORE|AFTER INSERT|UPDATE|DELETE ON table_name FOR EACH ROW
BEGIN
    -- SQL statements
END //
DELIMITER ;
```

**示例**：
```sql
DELIMITER //
CREATE TRIGGER before_user_insert BEFORE INSERT ON users FOR EACH ROW
BEGIN
    SET NEW.create_time = NOW();
    SET NEW.update_time = NOW();
END //
DELIMITER ;
```

### 8.2 删除触发器

```sql
DROP TRIGGER trigger_name;
```

**示例**：
```sql
DROP TRIGGER before_user_insert;
```

## 9. SQL常见错误

### 9.1 语法错误

- **错误**：缺少分号
  **示例**：`SELECT * FROM users WHERE age > 18`
  **修复**：`SELECT * FROM users WHERE age > 18;`

- **错误**：关键字拼写错误
  **示例**：`SELEC * FROM users`
  **修复**：`SELECT * FROM users;`

- **错误**：表名或列名错误
  **示例**：`SELECT * FROM user WHERE age > 18;`
  **修复**：`SELECT * FROM users WHERE age > 18;`

### 9.2 逻辑错误

- **错误**：条件逻辑错误
  **示例**：`SELECT * FROM users WHERE age > 18 AND age < 10;`
  **修复**：`SELECT * FROM users WHERE age > 10 AND age < 18;`

- **错误**：连接条件错误
  **示例**：`SELECT * FROM users JOIN orders ON users.id = orders.id;`
  **修复**：`SELECT * FROM users JOIN orders ON users.id = orders.user_id;`

### 9.3 执行错误

- **错误**：数据类型不匹配
  **示例**：`INSERT INTO users (age) VALUES ('twenty-five');`
  **修复**：`INSERT INTO users (age) VALUES (25);`

- **错误**：违反约束
  **示例**：`INSERT INTO users (email) VALUES ('john@example.com');`（email已存在）
  **修复**：`INSERT INTO users (email) VALUES ('john1@example.com');`

- **错误**：权限不足
  **示例**：`DROP TABLE users;`（无删除权限）
  **修复**：申请删除权限或使用有权限的用户

## 10. SQL最佳实践

### 10.1 命名规范

- **表名**：使用小写字母，单词之间用下划线分隔（如 `user_info`）
- **列名**：使用小写字母，单词之间用下划线分隔（如 `user_name`）
- **索引名**：使用 `idx_` 前缀，后跟列名（如 `idx_user_id`）
- **视图名**：使用 `v_` 前缀（如 `v_user_info`）
- **存储过程名**：使用 `sp_` 前缀（如 `sp_get_user`）

### 10.2 编写规范

- **关键字**：使用大写
- **缩进**：使用4个空格或1个制表符
- **换行**：每个子句占一行
- **注释**：使用 `--` 或 `/* */` 添加注释

**示例**：
```sql
-- 获取年龄大于18的用户
SELECT
    id,
    name,
    email
FROM
    users
WHERE
    age > 18
ORDER BY
    name ASC;
```

### 10.3 性能优化

- **避免使用 SELECT ***：只选择需要的列
- **使用参数化查询**：防止SQL注入
- **合理使用索引**：为经常查询的列创建索引
- **避免使用子查询**：考虑使用 JOIN
- **使用 LIMIT**：限制返回行数

**示例**：
```sql
-- 优化前
SELECT * FROM users WHERE name = 'John';

-- 优化后
SELECT id, name, email FROM users WHERE name = 'John' LIMIT 10;
```

## 11. 总结

SQL语法是数据库操作的基础，掌握SQL语法对于数据库开发和管理至关重要。本参考文档提供了SQL的基本语法、函数、操作符、子查询、事务、视图、存储过程和触发器的详细说明和示例，帮助开发者正确编写和使用SQL语句。在实际应用中，需要根据具体的数据库系统和业务需求进行适当的调整和优化。
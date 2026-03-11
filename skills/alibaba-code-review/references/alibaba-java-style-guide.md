# 阿里巴巴Java编码规范详细指南

## 1. 命名规范

### 1.1 类名
- 使用大驼峰命名法（UpperCamelCase）
- 类名应体现类的功能和职责
- 示例：`UserService`、`OrderController`、`ProductRepository`

### 1.2 方法名
- 使用小驼峰命名法（lowerCamelCase）
- 方法名应体现方法的功能
- 示例：`getUserById`、`saveOrder`、`deleteProduct`

### 1.3 变量名
- 使用小驼峰命名法（lowerCamelCase）
- 变量名应清晰表达变量的用途
- 避免使用单字母变量名（除了循环变量）
- 示例：`userName`、`orderId`、`productList`

### 1.4 常量名
- 使用全大写字母和下划线（UPPER_CASE_WITH_UNDERSCORES）
- 常量名应清晰表达常量的含义
- 示例：`MAX_COUNT`、`DEFAULT_TIMEOUT`、`ERROR_CODE`

### 1.5 包名
- 使用小写字母
- 包名应体现项目的结构和模块
- 示例：`com.alibaba.service`、`com.alibaba.controller`、`com.alibaba.entity`

### 1.6 接口名
- 使用大驼峰命名法（UpperCamelCase）
- 接口名应体现接口的功能
- 建议使用形容词或动词+名词的形式
- 示例：`UserService`、`OrderRepository`、`ProductValidator`

### 1.7 枚举名
- 使用大驼峰命名法（UpperCamelCase）
- 枚举名应体现枚举的用途
- 枚举常量使用全大写字母和下划线
- 示例：
  ```java
  public enum OrderStatus {
      PENDING, PROCESSING, COMPLETED, CANCELLED
  }
  ```

## 2. 代码风格

### 2.1 缩进
- 使用4个空格进行缩进，不使用制表符
- 保持缩进的一致性

### 2.2 换行
- 每行代码不超过120个字符
- 长行应适当换行，保持代码的可读性
- 换行时应遵循以下原则：
  - 运算符应放在行尾
  - 方法调用的参数过多时应换行
  - 括号应保持对齐

### 2.3 空格
- 操作符前后应添加空格
- 逗号后应添加空格
- 括号内的内容与括号之间应添加空格
- 示例：
  ```java
  int result = a + b;
  List<String> list = Arrays.asList("a", "b", "c");
  if (condition) {
      // 代码
  }
  ```

### 2.4 大括号
- 左大括号不换行，与前面的代码在同一行
- 右大括号单独换行
- 示例：
  ```java
  if (condition) {
      // 代码
  }
  
  public void method() {
      // 代码
  }
  ```

### 2.5 空行
- 不同逻辑块之间应添加空行
- 方法之间应添加空行
- 类的成员变量与方法之间应添加空行

## 3. 注释规范

### 3.1 类注释
- 使用Javadoc注释
- 包含类的功能描述、作者、创建日期等信息
- 示例：
  ```java
  /**
   * 用户服务类，提供用户相关的业务逻辑
   * 
   * @author 作者名
   * @date 2026-03-08
   */
  public class UserService {
      // 代码
  }
  ```

### 3.2 方法注释
- 使用Javadoc注释
- 包含方法的功能、参数、返回值、异常等信息
- 示例：
  ```java
  /**
   * 根据用户ID获取用户信息
   * 
   * @param userId 用户ID
   * @return 用户信息
   * @throws Exception 如果获取失败
   */
  public User getUserById(Long userId) throws Exception {
      // 代码
  }
  ```

### 3.3 代码注释
- 复杂代码应添加注释，说明实现思路
- 注释应简洁明了，避免冗余
- 示例：
  ```java
  // 计算总金额
  double totalAmount = orderItems.stream()
          .mapToDouble(item -> item.getPrice() * item.getQuantity())
          .sum();
  ```

### 3.4 TODO注释
- 使用 `// TODO:` 标记待完成的工作
- 示例：
  ```java
  // TODO: 实现分页功能
  public List<User> getUserList() {
      // 代码
  }
  ```

## 4. 异常处理

### 4.1 异常捕获
- 不要捕获 `Exception`，要捕获具体的异常类型
- 示例：
  ```java
  // 错误
  try {
      // 代码
  } catch (Exception e) {
      // 处理
  }
  
  // 正确
  try {
      // 代码
  } catch (IOException e) {
      // 处理
  } catch (SQLException e) {
      // 处理
  }
  ```

### 4.2 异常处理
- 捕获异常后要进行适当处理，不能直接忽略
- 示例：
  ```java
  try {
      // 代码
  } catch (IOException e) {
      logger.error("IO异常: {}", e.getMessage(), e);
      throw new BusinessException("文件操作失败", e);
  }
  ```

### 4.3 日志记录
- 异常发生时要记录日志，包含足够的上下文信息
- 示例：
  ```java
  try {
      // 代码
  } catch (SQLException e) {
      logger.error("数据库操作失败，SQL: {}", sql, e);
      throw new BusinessException("数据库操作失败", e);
  }
  ```

### 4.4 异常抛出
- 向上层抛出异常时要添加适当的信息
- 示例：
  ```java
  public void deleteUser(Long userId) {
      try {
          userRepository.deleteById(userId);
      } catch (Exception e) {
          throw new BusinessException("删除用户失败，用户ID: " + userId, e);
      }
  }
  ```

## 5. 安全规范

### 5.1 SQL注入
- 使用参数化查询，避免拼接SQL语句
- 示例：
  ```java
  // 错误
  String sql = "SELECT * FROM user WHERE username = '" + username + "'";
  
  // 正确
  String sql = "SELECT * FROM user WHERE username = ?";
  PreparedStatement ps = connection.prepareStatement(sql);
  ps.setString(1, username);
  ```

### 5.2 XSS攻击
- 对用户输入进行过滤和转义
- 示例：
  ```java
  // 使用工具类进行转义
  String safeContent = HtmlUtils.htmlEscape(userInput);
  ```

### 5.3 密码存储
- 使用加盐哈希存储密码，不存储明文密码
- 示例：
  ```java
  // 使用BCrypt进行密码加密
  String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
  ```

### 5.4 权限控制
- 实现严格的权限检查，防止越权访问
- 示例：
  ```java
  @PreAuthorize("hasRole('ADMIN')")
  public void deleteUser(Long userId) {
      // 代码
  }
  ```

## 6. 性能优化

### 6.1 循环优化
- 避免在循环中进行耗时操作
- 示例：
  ```java
  // 错误
  for (int i = 0; i < list.size(); i++) {
      // 代码
  }
  
  // 正确
  int size = list.size();
  for (int i = 0; i < size; i++) {
      // 代码
  }
  ```

### 6.2 内存使用
- 避免创建不必要的对象
- 示例：
  ```java
  // 错误
  String result = "";
  for (String s : list) {
      result += s;
  }
  
  // 正确
  StringBuilder sb = new StringBuilder();
  for (String s : list) {
      sb.append(s);
  }
  String result = sb.toString();
  ```

### 6.3 IO操作
- 使用缓冲流提高IO性能
- 及时关闭资源
- 示例：
  ```java
  try (BufferedReader br = new BufferedReader(new FileReader(file))) {
      String line;
      while ((line = br.readLine()) != null) {
          // 处理
      }
  } catch (IOException e) {
      // 处理
  }
  ```

## 7. 并发编程

### 7.1 线程安全
- 对共享资源进行适当的同步
- 示例：
  ```java
  // 使用synchronized关键字
  public synchronized void increment() {
      count++;
  }
  
  // 使用原子类
  private AtomicInteger count = new AtomicInteger(0);
  public void increment() {
      count.incrementAndGet();
  }
  ```

### 7.2 锁使用
- 尽量使用细粒度锁，减少锁的范围
- 示例：
  ```java
  // 错误
  public synchronized void method() {
      // 不需要同步的代码
      synchronized (this) {
          // 需要同步的代码
      }
      // 不需要同步的代码
  }
  
  // 正确
  public void method() {
      // 不需要同步的代码
      synchronized (this) {
          // 需要同步的代码
      }
      // 不需要同步的代码
  }
  ```

## 8. 代码结构

### 8.1 类的结构
- 成员变量
- 构造方法
- 方法（按功能分组）
- 内部类

### 8.2 方法长度
- 单个方法的长度不应超过100行
- 复杂方法应拆分为多个小方法

### 8.3 代码复杂度
- 避免过多的嵌套层级
- 单个方法的圈复杂度不应超过10

## 9. 最佳实践

### 9.1 代码复用
- 提取重复代码为公共方法
- 使用继承和接口提高代码复用性

### 9.2 可读性
- 编写清晰、易读的代码
- 使用有意义的变量名和方法名
- 添加适当的注释

### 9.3 可维护性
- 代码结构清晰，逻辑分明
- 遵循设计模式，提高代码的可维护性
- 定期重构代码，保持代码质量

### 9.4 测试
- 为代码编写单元测试
- 确保测试覆盖率达到80%以上
- 测试代码应与生产代码保持同步

## 10. 工具推荐

### 10.1 IDE插件
- **Alibaba Java Coding Guidelines**：阿里巴巴Java编码规范插件
- **CheckStyle**：代码风格检查工具
- **FindBugs**：代码缺陷检查工具

### 10.2 构建工具
- **Maven**：使用Maven进行项目管理和构建
- **Gradle**：使用Gradle进行项目管理和构建

### 10.3 代码分析工具
- **SonarQube**：代码质量分析平台
- **PMD**：代码静态分析工具
- **SpotBugs**：代码缺陷检查工具

通过遵循以上规范，可以提高代码的质量、可读性和可维护性，减少bug的产生，提高开发效率。
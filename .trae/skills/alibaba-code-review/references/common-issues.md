# 常见代码问题及解决方案

## 1. 命名规范问题

### 1.1 变量名使用缩写
- **问题**：变量名使用不清晰的缩写，降低代码可读性
- **示例**：
  ```java
  int uId; // 不清晰
  ```
- **解决方案**：使用完整、有意义的变量名
  ```java
  int userId; // 清晰
  ```

### 1.2 方法名不体现功能
- **问题**：方法名不能清晰表达方法的功能
- **示例**：
  ```java
  public void process() { // 不清晰
      // 代码
  }
  ```
- **解决方案**：使用描述性的方法名
  ```java
  public void processOrder() { // 清晰
      // 代码
  }
  ```

### 1.3 常量名使用小写
- **问题**：常量名没有使用全大写字母
- **示例**：
  ```java
  public static final int maxCount = 100; // 错误
  ```
- **解决方案**：使用全大写字母和下划线
  ```java
  public static final int MAX_COUNT = 100; // 正确
  ```

## 2. 代码风格问题

### 2.1 缩进不一致
- **问题**：代码缩进不一致，影响可读性
- **示例**：
  ```java
  if (condition) {
  System.out.println("Hello"); // 缩进错误
      System.out.println("World"); // 缩进错误
  }
  ```
- **解决方案**：使用统一的缩进（4个空格）
  ```java
  if (condition) {
      System.out.println("Hello"); // 正确
      System.out.println("World"); // 正确
  }
  ```

### 2.2 长行代码
- **问题**：一行代码过长，超过120个字符
- **示例**：
  ```java
  public User getUserByIdAndNameAndAge(Long id, String name, int age, String address, String phone) { // 过长
      // 代码
  }
  ```
- **解决方案**：适当换行
  ```java
  public User getUserByIdAndNameAndAge(
          Long id, 
          String name, 
          int age, 
          String address, 
          String phone) { // 正确
      // 代码
  }
  ```

### 2.3 空格使用不当
- **问题**：操作符前后没有空格
- **示例**：
  ```java
  int result=a+b; // 错误
  ```
- **解决方案**：在操作符前后添加空格
  ```java
  int result = a + b; // 正确
  ```

## 3. 异常处理问题

### 3.1 捕获异常后直接忽略
- **问题**：捕获异常后没有任何处理
- **示例**：
  ```java
  try {
      // 代码
  } catch (Exception e) {
      // 没有处理
  }
  ```
- **解决方案**：记录日志或进行适当处理
  ```java
  try {
      // 代码
  } catch (Exception e) {
      logger.error("异常信息: {}", e.getMessage(), e);
      // 其他处理
  }
  ```

### 3.2 捕获所有异常
- **问题**：捕获了所有异常，无法针对性处理
- **示例**：
  ```java
  try {
      // 代码
  } catch (Exception e) {
      // 处理
  }
  ```
- **解决方案**：捕获具体的异常类型
  ```java
  try {
      // 代码
  } catch (IOException e) {
      // 处理IO异常
  } catch (SQLException e) {
      // 处理SQL异常
  }
  ```

### 3.3 没有记录异常信息
- **问题**：异常发生时没有记录日志
- **示例**：
  ```java
  try {
      // 代码
  } catch (Exception e) {
      throw new RuntimeException("操作失败");
  }
  ```
- **解决方案**：记录异常信息
  ```java
  try {
      // 代码
  } catch (Exception e) {
      logger.error("操作失败: {}", e.getMessage(), e);
      throw new RuntimeException("操作失败", e);
  }
  ```

## 4. 注释规范问题

### 4.1 缺少类注释
- **问题**：类缺少Javadoc注释
- **示例**：
  ```java
  public class UserService { // 缺少注释
      // 代码
  }
  ```
- **解决方案**：添加类注释
  ```java
  /**
   * 用户服务类，提供用户相关的业务逻辑
   * 
   * @author 作者名
   * @date 2026-03-08
   */
  public class UserService { // 正确
      // 代码
  }
  ```

### 4.2 缺少方法注释
- **问题**：方法缺少Javadoc注释
- **示例**：
  ```java
  public User getUserById(Long userId) { // 缺少注释
      // 代码
  }
  ```
- **解决方案**：添加方法注释
  ```java
  /**
   * 根据用户ID获取用户信息
   * 
   * @param userId 用户ID
   * @return 用户信息
   */
  public User getUserById(Long userId) { // 正确
      // 代码
  }
  ```

### 4.3 注释与代码不一致
- **问题**：注释与代码逻辑不一致
- **示例**：
  ```java
  // 获取用户列表
  public User getUserById(Long userId) { // 注释与方法功能不一致
      // 代码
  }
  ```
- **解决方案**：确保注释与代码逻辑一致
  ```java
  // 根据用户ID获取用户信息
  public User getUserById(Long userId) { // 正确
      // 代码
  }
  ```

## 5. 安全问题

### 5.1 SQL注入
- **问题**：使用字符串拼接构建SQL语句
- **示例**：
  ```java
  String sql = "SELECT * FROM user WHERE username = '" + username + "'";
  ```
- **解决方案**：使用参数化查询
  ```java
  String sql = "SELECT * FROM user WHERE username = ?";
  PreparedStatement ps = connection.prepareStatement(sql);
  ps.setString(1, username);
  ```

### 5.2 密码明文存储
- **问题**：直接存储明文密码
- **示例**：
  ```java
  user.setPassword(password); // 明文存储
  ```
- **解决方案**：使用加盐哈希存储密码
  ```java
  String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
  user.setPassword(hashedPassword); // 哈希存储
  ```

### 5.3 缺少权限检查
- **问题**：没有进行权限检查，可能导致越权访问
- **示例**：
  ```java
  public void deleteUser(Long userId) { // 缺少权限检查
      // 代码
  }
  ```
- **解决方案**：添加权限检查
  ```java
  @PreAuthorize("hasRole('ADMIN')")
  public void deleteUser(Long userId) { // 正确
      // 代码
  }
  ```

## 6. 性能问题

### 6.1 循环中创建对象
- **问题**：在循环中创建不必要的对象
- **示例**：
  ```java
  for (int i = 0; i < 1000; i++) {
      String s = new String("test"); // 每次循环都创建新对象
      // 代码
  }
  ```
- **解决方案**：在循环外创建对象
  ```java
  String s = "test"; // 只创建一次
  for (int i = 0; i < 1000; i++) {
      // 代码
  }
  ```

### 6.2 频繁的字符串拼接
- **问题**：使用字符串拼接操作符进行频繁的字符串拼接
- **示例**：
  ```java
  String result = "";
  for (String s : list) {
      result += s; // 每次都创建新字符串
  }
  ```
- **解决方案**：使用StringBuilder
  ```java
  StringBuilder sb = new StringBuilder();
  for (String s : list) {
      sb.append(s); // 更高效
  }
  String result = sb.toString();
  ```

### 6.3 数据库连接未关闭
- **问题**：数据库连接使用后未关闭
- **示例**：
  ```java
  Connection conn = DriverManager.getConnection(url, username, password);
  // 使用连接
  // 没有关闭连接
  ```
- **解决方案**：使用try-with-resources自动关闭资源
  ```java
  try (Connection conn = DriverManager.getConnection(url, username, password)) {
      // 使用连接
  } catch (SQLException e) {
      // 处理异常
  }
  ```

## 7. 并发问题

### 7.1 共享变量未同步
- **问题**：多个线程访问共享变量时没有同步
- **示例**：
  ```java
  private int count = 0;
  
  public void increment() {
      count++; // 非原子操作
  }
  ```
- **解决方案**：使用同步或原子类
  ```java
  // 使用synchronized
  private int count = 0;
  
  public synchronized void increment() {
      count++;
  }
  
  // 或使用原子类
  private AtomicInteger count = new AtomicInteger(0);
  
  public void increment() {
      count.incrementAndGet();
  }
  ```

### 7.2 死锁
- **问题**：多个线程互相等待对方释放资源
- **示例**：
  ```java
  // 线程1
  synchronized (lock1) {
      synchronized (lock2) {
          // 代码
      }
  }
  
  // 线程2
  synchronized (lock2) {
      synchronized (lock1) {
          // 代码
      }
  }
  ```
- **解决方案**：按照固定顺序获取锁
  ```java
  // 两个线程都按照相同的顺序获取锁
  synchronized (lock1) {
      synchronized (lock2) {
          // 代码
      }
  }
  ```

## 8. 代码结构问题

### 8.1 方法过长
- **问题**：单个方法过长，超过100行
- **解决方案**：将方法拆分为多个小方法

### 8.2 类职责过多
- **问题**：一个类承担了过多的职责
- **解决方案**：遵循单一职责原则，将不同职责的代码分离到不同的类中

### 8.3 代码重复
- **问题**：存在重复的代码片段
- **解决方案**：提取重复代码为公共方法

## 9. 其他问题

### 9.1 魔法数字
- **问题**：代码中使用了硬编码的魔法数字
- **示例**：
  ```java
  if (status == 1) { // 魔法数字
      // 代码
  }
  ```
- **解决方案**：使用常量
  ```java
  public static final int STATUS_ACTIVE = 1;
  
  if (status == STATUS_ACTIVE) { // 清晰
      // 代码
  }
  ```

### 9.2 未使用的变量
- **问题**：存在未使用的变量
- **解决方案**：删除未使用的变量

### 9.3 空指针异常
- **问题**：没有进行空值检查，可能导致空指针异常
- **示例**：
  ```java
  user.getName(); // 可能为null
  ```
- **解决方案**：添加空值检查
  ```java
  if (user != null) {
      user.getName();
  }
  ```

### 9.4 资源泄露
- **问题**：资源使用后未关闭
- **示例**：
  ```java
  FileInputStream fis = new FileInputStream(file);
  // 使用fis
  // 没有关闭
  ```
- **解决方案**：使用try-with-resources
  ```java
  try (FileInputStream fis = new FileInputStream(file)) {
      // 使用fis
  } catch (IOException e) {
      // 处理异常
  }
  ```

通过识别和解决这些常见问题，可以提高代码的质量、可读性和可维护性，减少bug的产生，提高开发效率。
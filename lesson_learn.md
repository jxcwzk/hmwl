# 前后端开发经验教训总结

## 1. 前后端连接问题

### 问题描述
- 前端无法连接后端API，出现404错误
- 代理配置与后端路径不匹配

### 根本原因
- **代理配置错误**：前端vite.config.js中配置了错误的rewrite规则，将/api前缀移除
- **路径不匹配**：后端配置了context-path: /api，而前端代理将/api前缀移除，导致实际请求路径为http://localhost:8080/business-user/list，而正确路径应该是http://localhost:8080/api/business-user/list

### 解决方案
- **修改前端代理配置**：移除vite.config.js中的rewrite规则，确保前端请求能够正确代理到后端的/api路径
- **保持路径一致性**：确保前端代理配置与后端context-path配置一致

### 预防措施
- **文档化配置**：将代理配置和后端路径配置记录在项目文档中
- **统一配置管理**：使用环境变量或配置文件管理前后端路径配置
- **代码审查**：在代码合并前检查代理配置是否正确
- **自动化测试**：添加API连接测试，确保前后端连接正常

## 2. 字符编码问题

### 问题描述
- 订单详情中的收件人信息显示为乱码（??）
- 只有电话号码显示正常

### 根本原因
- **字符编码不一致**：数据库连接、后端响应和前端接收的编码不一致
- **缺少编码配置**：后端没有正确配置响应编码

### 解决方案
- **数据库连接编码**：在application.yml中配置数据库连接URL，添加useUnicode=true&characterEncoding=utf-8&characterSetResults=utf8mb4
- **Spring编码配置**：在application.yml中配置spring.http.encoding，确保启用UTF-8编码
- **Tomcat编码配置**：在application.yml中配置server.tomcat.uri-encoding: UTF-8
- **控制器编码**：在控制器方法中设置响应编码为UTF-8

### 预防措施
- **统一编码标准**：在项目初始化时就确定使用UTF-8编码
- **配置模板**：创建包含正确编码配置的项目模板
- **编码测试**：添加编码测试用例，确保中文等非ASCII字符显示正常
- **定期检查**：定期检查编码配置是否被意外修改

## 3. 服务启动问题

### 问题描述
- 后端服务启动失败
- 端口被占用
- 命令执行环境问题

### 根本原因
- **端口冲突**：端口8080被其他进程占用
- **依赖问题**：依赖不完整或版本冲突
- **命令执行环境**：PowerShell语法与bash语法差异

### 解决方案
- **端口管理**：使用netstat命令检查端口占用情况，使用taskkill命令终止占用端口的进程
- **依赖管理**：使用mvn dependency:tree检查依赖，确保依赖完整
- **命令执行**：使用PowerShell兼容的命令语法，如使用分号(;)而不是&&连接命令

### 预防措施
- **端口配置**：在配置文件中使用可配置的端口，避免硬编码
- **依赖锁定**：使用dependencyManagement锁定依赖版本
- **环境说明**：在项目文档中说明命令执行环境和语法
- **启动脚本**：创建统一的启动脚本，处理环境差异

## 4. 功能实现问题

### 问题描述
- 业务客户选择功能不工作
- 选择业务用户后没有加载对应的业务客户列表

### 根本原因
- **API实现缺失**：后端缺少根据业务用户ID获取业务客户列表的API
- **前端逻辑不完整**：前端没有实现选择业务用户后调用API加载业务客户列表的逻辑

### 解决方案
- **后端API实现**：添加BusinessCustomerController.listByBusinessUserId接口
- **前端逻辑实现**：在Order.vue中添加getBusinessCustomerList方法，在handleBusinessUserChange中调用该方法
- **数据绑定**：实现业务客户选择后自动填充收件人信息的逻辑

### 预防措施
- **API设计**：在设计阶段就考虑完整的API需求，包括各种查询场景
- **前端逻辑**：确保前端逻辑与后端API对应，处理各种用户交互场景
- **测试用例**：为功能实现添加详细的测试用例
- **代码审查**：确保功能实现的完整性和正确性

## 5. 测试与验证问题

### 问题描述
- 测试流程不规范
- 测试结果记录不完整
- 问题复现困难

### 根本原因
- **缺少测试规范**：没有详细的测试流程和步骤
- **缺少测试文档**：没有记录测试结果和问题
- **环境配置不一致**：测试环境与开发环境配置不同

### 解决方案
- **创建测试规范**：制定详细的测试流程和步骤
- **记录测试结果**：使用标准化的测试结果表格记录测试情况
- **环境一致性**：确保测试环境与开发环境配置一致
- **自动化测试**：添加自动化测试，减少手动测试的误差

### 预防措施
- **测试文档**：创建并维护测试规范文档
- **环境管理**：使用容器或配置管理工具确保环境一致性
- **持续集成**：集成CI/CD流程，自动运行测试
- **测试覆盖**：确保测试覆盖主要功能和场景

## 6. 总结

### 关键教训
1. **配置一致性**：前后端配置必须保持一致，特别是路径、编码等基础配置
2. **代码规范**：遵循统一的代码规范和命名约定
3. **文档化**：将配置、流程和经验教训记录在文档中
4. **测试优先**：在开发过程中持续进行测试，确保功能正常
5. **问题跟踪**：及时记录和解决问题，避免问题积累

### 改进建议
1. **项目初始化**：创建包含正确配置的项目模板
2. **代码审查**：建立代码审查流程，确保代码质量
3. **自动化测试**：增加自动化测试覆盖率
4. **监控告警**：添加系统监控，及时发现问题
5. **知识共享**：定期分享经验教训，避免重复错误

### 后续行动
1. **更新项目文档**：将本总结添加到项目文档中
2. **配置检查**：定期检查项目配置是否正确
3. **测试流程**：按照测试规范执行测试
4. **培训学习**：组织团队学习经验教训
5. **持续改进**：不断优化开发流程和代码质量

---

## 7. 图片上传功能与腾讯云COS配置

### 问题描述
- 前端上传图片功能不工作
- 腾讯云COS上传返回403 Access Denied错误
- 数据库字段长度不足导致上传失败

### 根本原因

#### 7.1 前端上传请求方式问题
- **使用fetch而非axios**：前端使用原生fetch API发送上传请求，导致请求处理不一致
- **Content-Type配置问题**：手动设置Content-Type导致boundary丢失

#### 7.2 数据库字段长度问题
- **image_url字段过短**：数据库order_image表的image_url字段为varchar(255)，无法存储腾讯云COS的完整URL（通常超过255字符）

#### 7.3 腾讯云COS配置问题
- **配置未更新**：application.yml中的COS配置未更新为用户提供的正确配置（旧的bucketName: jxcwzk-1316797858，新的bucketName: jxcwzk-1342353267）
- **密钥权限问题**：旧的密钥没有新存储桶的访问权限

#### 7.4 前端调用接口错误
- **接口路径错误**：前端调用的是本地存储接口 `/api/order/image/upload`，而非COS上传接口 `/api/order/image/upload/cos`
- **缺少参数**：COS上传接口需要orderNo参数，前端未传递

### 解决方案

#### 7.5 修改前端上传逻辑
- 将fetch改为axios发送上传请求
- 移除手动设置的Content-Type，让axios自动处理multipart/form-data
- 修改上传接口为 `/api/order/image/upload/cos`
- 添加orderNo参数

#### 7.6 修改数据库字段
```sql
ALTER TABLE order_image MODIFY COLUMN image_url varchar(1000) DEFAULT NULL;
```

#### 7.7 更新后端配置
更新application.yml中的COS配置为正确值：
```yaml
cos:
  secretId: ${COS_SECRET_ID}
  secretKey: ${COS_SECRET_KEY}
  bucketName: jxcwzk-1342353267
  region: ap-shanghai
  baseUrl: https://jxcwzk-1342353267.cos.ap-shanghai.myqcloud.com
```

#### 7.8 添加CORS配置
创建CorsConfig.java解决跨域问题：
```java
@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*");
        config.setAllowCredentials(true);
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
```

#### 7.9 添加静态资源映射
修改WebMvcConfig.java，添加/api/upload/**映射：
```java
@Override
public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/upload/**")
            .addResourceLocations("file:" + uploadPath + "/");
    registry.addResourceHandler("/api/upload/**")
            .addResourceLocations("file:" + uploadPath + "/");
}
```

### 预防措施
1. **配置管理**：将所有配置项（包括第三方服务）记录在文档中
2. **接口文档**：维护完整的API接口文档，包括参数说明
3. **字段规划**：数据库字段长度要考虑未来扩展，特别是URL等可能很长的字段
4. **测试验证**：使用脚本测试API接口，确保参数正确
5. **配置更新流程**：确保配置文件修改后重新编译打包
6. **前端按钮位置**：注意Vue组件的slot使用，避免将按钮放在错误的插槽中

---

## 8. 订单图片删除功能开发

### 问题描述
- 订单图片删除功能不工作
- 前端删除按钮显示不出来
- 删除图片只删除了数据库记录，没有删除COS上的实际文件

### 根本原因

#### 8.1 后端删除逻辑不完整
- 原删除接口只调用了 `removeById(id)`，只删除了数据库记录
- 没有处理COS云端文件和本地文件的删除，导致存储空间浪费

#### 8.2 前端按钮显示问题
- 使用了 Element Plus 的 `el-image` 组件的 `#footer` slot
- 该 slot 的样式可能与预期不符，导致删除按钮不可见或显示异常

### 解决方案

#### 8.3 后端删除逻辑完善
在 OrderImageService.java 中添加新方法：
```java
boolean deleteImage(Long id) throws Exception;
```

在 OrderImageServiceImpl.java 中实现：
```java
@Override
public boolean deleteImage(Long id) throws Exception {
    OrderImage orderImage = baseMapper.selectById(id);
    if (orderImage == null) {
        throw new Exception("图片不存在");
    }

    String imageUrl = orderImage.getImageUrl();
    if (imageUrl != null && !imageUrl.isEmpty()) {
        if (imageUrl.startsWith("/api/upload/")) {
            // 删除本地文件
            String filename = imageUrl.substring("/api/upload/".length());
            File localFile = new File(uploadPath + File.separator + filename);
            if (localFile.exists()) {
                localFile.delete();
            }
        } else if (imageUrl.startsWith(baseUrl)) {
            // 删除COS文件
            String cosKey = imageUrl.substring(baseUrl.length() + 1);
            cosClient.deleteObject(bucketName, cosKey);
        }
    }

    return removeById(id);
}
```

#### 8.4 前端显示修复
将删除按钮从 `el-image` 的 slot 中移出，改为独立的 div 包裹：
```vue
<div v-for="(image, index) in imageList" :key="image.id" class="image-item-wrapper">
  <el-image :src="image.imageUrl" ...>
  </el-image>
  <div class="image-actions">
    <el-button type="danger" size="small" @click="handleDeleteImage(image.id)">
      删除
    </el-button>
  </div>
</div>
```

添加相应的 CSS 样式：
```css
.image-item-wrapper {
  position: relative;
  width: 120px;
}

.image-actions {
  margin-top: 4px;
  text-align: center;
}
```

### 预防措施
1. **文件清理**：删除数据库记录时，要同时清理关联的物理文件（本地或云存储）
2. **前端组件**：避免将交互元素放在不熟悉的组件 slot 中，使用独立的包裹元素更可靠
3. **测试验证**：删除功能开发完成后要进行实际测试，验证文件是否真正被删除
4. **存储成本**：及时清理无用的云存储文件，避免产生不必要的存储费用

---

## 9. 前端请求拦截器与后端返回格式不一致问题

### 问题描述
- 前端请求显示 "请求失败" 错误
- 浏览器控制台报错：`Uncaught (in promise) Error: 请求失败`
- 使用 Playwright 测试 API 正常返回数据，但前端报错

### 根本原因

#### 9.1 后端返回格式不统一
- **部分 API**：直接返回数据（如分页接口返回 `IPage` 对象）
- **部分 API**：返回 `Result` 包装对象（如 `Result.success(data)`）
- 前端 request.js 拦截器只检查 `res.code !== 200`，导致非 200 响应被误判为失败

#### 9.2 数据解析路径不一致
- 后端返回 `IPage` 时：`res.records`、`res.total`
- 后端返回 `Result` 时：`res.data.records`、`res.data.total`
- 前端代码直接使用 `res.data.records`，导致解析失败

### 解决方案

#### 9.3 修改前端请求拦截器
修改 `request.js`，兼容有 code 字段和无 code 字段的情况：
```javascript
// 修改前
if (res.code !== 200) {
  ElMessage.error(res.message || '请求失败')
  return Promise.reject(new Error(res.message || '请求失败'))
}

// 修改后
if (res.code !== undefined && res.code !== 200) {
  ElMessage.error(res.message || '请求失败')
  return Promise.reject(new Error(res.message || '请求失败'))
}
```

#### 9.4 修改前端数据解析逻辑
兼容两种数据格式：
```javascript
// 修改前
orderList.value = res.data.records
total.value = res.data.total

// 修改后
orderList.value = res.records || res.data?.records || []
total.value = res.total || res.data?.total || 0

// 单个对象同理
businessUserList.value = res.data || res
```

#### 9.5 涉及的组件
- Order.vue
- Driver.vue
- Vehicle.vue
- Route.vue
- Settlement.vue
- NetworkPoint.vue
- User.vue
- BusinessUser.vue
- BusinessCustomer.vue

### 预防措施
1. **统一返回格式**：建议后端所有 API 都使用 `Result` 包装，保持格式统一
2. **API 文档**：维护完整的 API 返回格式文档
3. **前端容错**：前端请求库增加格式兼容性处理
4. **测试验证**：修改前后端接口后要在前端实际测试
5. **错误诊断**：优先使用浏览器控制台和后端日志排查问题

---

## 10. GitHub 机密扫描保护机制与敏感凭证管理

### 问题描述
- GitHub 推送被阻止，显示 "Push cannot contain secrets" 错误
- GitHub 检测到代码中包含腾讯云 COS 的 Secret ID/Secret Key 等敏感凭证
- 机密扫描保护机制触发，禁止将密钥等敏感信息提交到公共仓库

### 根本原因

#### 10.1 敏感凭证硬编码
- **application.yml**：直接写入 COS Secret ID 和 Secret Key
- **CosTest.java**：测试代码中硬编码了凭证
- **文档文件**：cos-config.md、lesson_learn.md 等文档中包含真实凭证示例

#### 10.2 历史提交包含敏感信息
- 即使当前代码已修复，GitHub 仍会扫描历史提交中的敏感凭证
- 需要清理 Git 历史才能彻底解决问题

### 解决方案

#### 10.3 创建本地开发配置文件
创建 `application-dev.yml`，用于本地开发：
```yaml
cos:
  secretId: YOUR_LOCAL_SECRET_ID
  secretKey: YOUR_LOCAL_SECRET_KEY
  bucketName: jxcwzk-1342353267
  region: ap-shanghai
  baseUrl: https://jxcwzk-1342353267.cos.ap-shanghai.myqcloud.com
```

将 `application-dev.yml` 加入 `.gitignore`：
```gitignore
# 本地开发配置（敏感凭证）
application-dev.yml
```

#### 10.4 修改 application.yml 使用环境变量
```yaml
cos:
  secretId: ${COS_SECRET_ID}
  secretKey: ${COS_SECRET_KEY}
  bucketName: jxcwzk-1342353267
  region: ap-shanghai
  baseUrl: https://jxcwzk-1342353267.cos.ap-shanghai.myqcloud.com
```

#### 10.5 修改 CosConfig.java 支持环境变量
```java
@Value("${cos.secretId:${COS_SECRET_ID}}")
private String secretId;

@Value("${cos.secretKey:${COS_SECRET_KEY}}")
private String secretKey;
```

#### 10.6 修改测试代码使用参数传入
```java
String secretId = System.getProperty("cos.secretId", System.getenv("COS_SECRET_ID"));
String secretKey = System.getProperty("cos.secretKey", System.getenv("COS_SECRET_KEY"));

if (secretId == null || secretId.isEmpty()) {
    System.out.println("错误: 请通过 -D 参数或环境变量传入 COS 凭证");
    System.exit(1);
}
```

运行测试：
```bash
java -Dcos.secretId=YOUR_ID -Dcos.secretKey=YOUR_KEY CosTest
```

#### 10.7 清理 Git 历史
使用 `git filter-branch` 或 `git rebase` 清理包含敏感凭证的旧提交：
```bash
# 方法一：使用 filter-branch
git filter-branch --force --tree-filter '...' --tag-name-filter cat -- --all

# 方法二：重新初始化仓库（最彻底）
git update-ref -d HEAD
git reflog expire --expire=now --all
git gc --prune=now --aggressive
```

#### 10.8 强制推送
```bash
git push -u origin main --force
```

### 预防措施
1. **永不硬编码凭证**：敏感信息绝对不能直接写在代码或配置文件中
2. **环境变量管理**：生产环境使用环境变量注入凭证
3. **本地配置文件隔离**：使用 `.gitignore` 排除本地配置文件
4. **文档示例使用占位符**：文档中的配置示例使用占位符，不要使用真实值
5. **敏感凭证轮换**：如果凭证泄露，立即在腾讯云控制台更换新的密钥
6. **GitHub 推送保护**：启用 GitHub 机密扫描，及时发现并修复问题

---

## 11. 微信小程序真机调试网络连接问题

### 问题描述
- 微信小程序在真机（手机）上调试时显示 `ERR_CONNECTION_REFUSED` 错误
- 登录功能失败：`app.js:52 登录失败: {errMsg: "request:fail errcode:-102 cronet_error_code:-102 error_msg:net::ERR_CONNECTION_REFUSED", errno: 600001}`
- 订单提交失败：`send.js:351 提交订单失败`

### 根本原因

#### 11.1 localhost 无法在真机上访问
- 开发者电脑上的 `localhost:8081` 或 `127.0.0.1:8081` 地址只能在本机访问
- 真机（手机）无法访问开发者电脑的 localhost 地址
- 需要使用开发者电脑的局域网 IP 地址

#### 11.2 硬编码的 localhost URL
- `config.js` 中只配置了 `localhost:8081`
- `send.js` 中直接使用了 `require('../../utils/config.js').dev.baseUrl`，导致 localhost 被直接使用

### 解决方案

#### 11.3 获取开发者电脑的局域网 IP
在终端运行以下命令：
- **macOS**: `ifconfig | grep "inet " | grep -v 127.0.0.1`
- **Windows**: `ipconfig`

获取到的 IP 地址示例：`192.168.31.230`

#### 11.4 修改 config.js 添加 IP 地址配置
```javascript
module.exports = {
  dev: {
    baseUrl: 'http://localhost:8081/api',
    // 添加真机可访问的 IP 地址
    ipUrl: 'http://192.168.31.230:8081/api'
  },
  // 添加自动获取正确 URL 的方法
  getApiUrl: function() {
    try {
      const systemInfo = wx.getSystemInfoSync()
      const platform = systemInfo.platform
      // 微信开发者工具使用 localhost，真机使用 IP 地址
      if (platform === 'devtools') {
        return this.dev.baseUrl
      }
      return this.dev.ipUrl || this.dev.baseUrl
    } catch (e) {
      return this.dev.baseUrl
    }
  }
}
```

#### 11.5 修改 api.js 使用平台检测
```javascript
const config = require('./config.js')

const getBaseUrl = () => {
  const systemInfo = wx.getSystemInfoSync()
  const platform = systemInfo.platform
  if (platform === 'devtools') {
    return config.dev.baseUrl
  }
  return config.dev.ipUrl || config.dev.baseUrl
}
```

#### 11.6 修改 send.js 使用统一配置
```javascript
const config = require('../../utils/config.js')

// 修改前（错误）
const BASE_URL = require('../../utils/config.js').dev.baseUrl

// 修改后（正确）
// 直接调用 config.getApiUrl() 获取正确的 URL
url: `${config.getApiUrl()}/order`
```

#### 11.7 确保后端允许跨域
确保后端配置了 CORS，允许来自真机的请求：
```java
@Configuration
public class CorsConfig {
  @Bean
  public CorsFilter corsFilter() {
    CorsConfiguration config = new CorsConfiguration();
    config.addAllowedOriginPattern("*");
    config.setAllowCredentials(true);
    config.addAllowedHeader("*");
    config.addAllowedMethod("*");
    config.setMaxAge(3600L);
    
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return new CorsFilter(source);
  }
}
```

#### 11.8 微信开发者工具开启不校验合法域名
在微信开发者工具中：`详情` -> `本地设置` -> 勾选 `不校验合法域名、web-view（业务域名）、TLS 版本以及 HTTPS 证书`

### 涉及的文件
- `miniprogram/utils/config.js` - 添加 ipUrl 和 getApiUrl() 方法
- `miniprogram/utils/api.js` - 使用平台检测选择正确的 URL
- `miniprogram/pages/send/send.js` - 修改为使用 config.getApiUrl()

### 预防措施
1. **统一配置管理**：所有 API URL 都通过统一的配置模块管理，不要在各个页面直接硬编码
2. **平台检测**：使用 `wx.getSystemInfoSync()` 检测运行环境，自动选择合适的 URL
3. **真机调试准备**：开发初期就配置好 IP 地址，方便真机调试
4. **环境变量**：可以使用环境变量或配置文件区分开发、测试、生产环境
5. **文档记录**：记录开发者电脑的 IP 地址，方便团队成员使用
6. **代码审查**：检查所有网络请求 URL，确保没有遗漏的 localhost 硬编码

---

## 12. 代码安全审查与修复

### 背景
使用 Superpowers 技能框架的 `requesting-code-review` 技能对后端代码进行审查，发现多个安全问题和代码质量问题。

### 发现的问题

#### Critical（必须修复）

| 问题 | 位置 | 描述 |
|------|------|------|
| 密码明文存储 | UserServiceImpl.java:58 | 使用明文比较密码，安全风险极高 |
| JWT 密钥硬编码 | JwtUtil.java:18 | 密钥直接写在代码中，泄露到 Git 仓库 |
| 敏感信息泄露 | UserServiceImpl.java:67-175 | System.out.println 打印 AppID、Secret、openid 等 |
| 参数空指针风险 | OrderController.java 多处 | 8 个方法的 params.get() 未做空值校验 |

#### Important（应该修复）

| 问题 | 位置 | 描述 |
|------|------|------|
| 性能问题 | OrderController.java:79-85 | 内存遍历过滤订单，应使用数据库查询 |
| 统计重复计算 | StatisticsController.java:35-38 | 每次请求重新生成统计 |
| 缺少事务控制 | OrderController.java:456-478 | 批量操作无事务原子性 |
| System.out.println | 多处 | 违反生产环境日志规范 |

### 解决方案

#### 12.1 添加 BCrypt 密码加密依赖

在 `pom.xml` 中添加：
```xml
<dependency>
    <groupId>org.mindrot</groupId>
    <artifactId>jbcrypt</artifactId>
    <version>0.4</version>
</dependency>
```

#### 12.2 修改密码验证逻辑

```java
// 修改前（不安全）
if (user.getPassword() != null && user.getPassword().equals(password)) {
    return user;
}

// 修改后（安全）
if (user.getPassword() != null && BCrypt.checkpw(password, user.getPassword())) {
    return user;
}
```

#### 12.3 修改 JWT 密钥为环境变量

```java
// 修改前（硬编码）
private static final String SECRET_KEY = "hmwl2024hongmeiwuliuverysecrettokenkey123456";

// 修改后（环境变量）
@Value("${jwt.secret:hmwl2024hongmeiwuliuverysecrettokenkey123456}")
private String secretKey;
```

在 `application.yml` 中添加：
```yaml
jwt:
  secret: ${JWT_SECRET_KEY:hmwl2024hongmeiwuliuverysecrettokenkey123456}
```

#### 12.4 替换 System.out.println 为 SLF4J

```java
// 修改前
System.out.println("AppID: " + appid);
System.out.println("Secret: " + (secret != null && !secret.isEmpty() ? "已配置" : "未配置"));
System.out.println("Openid: " + openid);

// 修改后
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

logger.info("========== 微信登录开始 ==========");
logger.info("微信登录成功, openid: {}", openid.substring(0, 8) + "****");
logger.warn("微信登录失败: errcode={}, errmsg={}", response.get("errcode"), response.get("errmsg"));
logger.error("调用微信API异常", e);
```

#### 12.5 添加参数空指针校验

在 OrderController.java 的 8 个方法中添加参数校验：
```java
// 修改前
Long orderId = Long.valueOf(params.get("orderId").toString());

// 修改后
if (params.get("orderId") == null || params.get("driverId") == null) {
    return Result.error("参数错误：缺少orderId或driverId");
}
Long orderId = Long.valueOf(params.get("orderId").toString());
```

涉及的方法：
- assignDriver - 指派司机
- assignNetwork - 指派网点
- updateLogistics - 更新物流
- providePrice - 报价
- updatePrice - 修改价格
- driverAccept - 司机接单
- driverReject - 司机拒单
- driverUpdateStatus - 更新状态

### 涉及的文件

| 文件 | 修改内容 |
|------|---------|
| pom.xml | 添加 jBCrypt 依赖 |
| UserServiceImpl.java | BCrypt 密码验证、SLF4J 日志 |
| JwtUtil.java | 环境变量配置 JWT 密钥 |
| OrderController.java | 8 个方法添加参数校验 |
| application.yml | 添加 JWT 配置项 |

### ⚠️ 重要注意事项

由于密码现在使用 BCrypt 加密，**现有用户的密码将无法登录**！

解决方案：
1. 重置所有用户密码
2. 或在数据库中将现有密码使用 BCrypt 重新哈希：
```java
String hash = BCrypt.hashpw("原始密码", BCrypt.gensalt());
```

### 预防措施

1. **代码审查**：使用 Superpowers 技能框架进行定期代码审查
2. **安全扫描**：使用工具扫描代码中的安全漏洞
3. **密码安全**：所有密码必须使用 BCrypt 或类似算法加密存储
4. **密钥管理**：敏感配置必须使用环境变量，禁止硬编码
5. **日志规范**：生产环境必须使用日志框架，禁止 System.out.println
6. **参数校验**：所有用户输入必须进行空值和类型校验


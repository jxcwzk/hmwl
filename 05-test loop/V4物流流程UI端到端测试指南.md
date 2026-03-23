# V4物流流程UI端到端测试指南

## 测试环境

| 项目 | 地址 |
|------|------|
| 前端 | http://localhost:3000 |
| 后端API | http://localhost:8081/api |

## 测试工具

本项目已配置Puppeteer MCP工具，支持以下UI自动化操作：

### 可用的Puppeteer命令
- `mcp_Puppeteer_puppeteer_navigate` - 导航到URL
- `mcp_Puppeteer_puppeteer_screenshot` - 截图
- `mcp_Puppeteer_puppeteer_click` - 点击元素
- `mcp_Puppeteer_puppeteer_fill` - 填写表单
- `mcp_Puppeteer_puppeteer_select` - 选择下拉选项
- `mcp_Puppeteer_puppeteer_hover` - 悬停
- `mcp_Puppeteer_puppeteer_evaluate` - 执行JavaScript

---

## V4物流流程UI测试步骤

### 第一步：登录系统
```
1. 导航到登录页面
   mcp_Puppeteer_puppeteer_navigate(url="http://localhost:3000/login")

2. 填写用户名
   mcp_Puppeteer_puppeteer_fill(selector="input[type='text']", value="admin1")

3. 填写密码
   mcp_Puppeteer_puppeteer_fill(selector="input[type='password']", value="123456")

4. 点击登录按钮
   mcp_Puppeteer_puppeteer_click(selector="button[type='submit']")

5. 等待页面跳转
   mcp_Puppeteer_puppeteer_evaluate(script="window.location.pathname")
```

### 第二步：客户下单
```
1. 导航到订单管理页面
   mcp_Puppeteer_puppeteer_navigate(url="http://localhost:3000/order")

2. 点击新增订单按钮
   mcp_Puppeteer_puppeteer_click(selector="button:contains('新增订单')")

3. 填写订单信息
   - 发件人信息
   - 收件人信息
   - 货物信息（名称、重量、体积）

4. 提交订单
   mcp_Puppeteer_puppeteer_click(selector="button:contains('提交')")

5. 记录生成的运单号
```

### 第三步：调度派发比价
```
1. 导航到调度管理页面
   mcp_Puppeteer_puppeteer_navigate(url="http://localhost:3000/dispatch")

2. 切换到"派发比价"标签页

3. 选择订单
   mcp_Puppeteer_puppeteer_click(selector="tr:contains('运单号')")

4. 点击"选择网点派发"按钮

5. 选择多个网点（复选框）
   mcp_Puppeteer_puppeteer_click(selector="input[type='checkbox']")

6. 点击"确认派发"按钮
```

### 第四步：网点响应报价
```
1. 以网点账号登录（或通过API模拟）

2. 导航到网点报价页面

3. 查看待报价订单

4. 填写报价信息
   - 底价
   - 客户报价
   - 运输天数

5. 提交报价
```

### 第五步：调度选择报价
```
1. 导航回调度管理页面
   mcp_Puppeteer_puppeteer_navigate(url="http://localhost:3000/dispatch")

2. 切换到"待确认报价"标签页

3. 查看各网点报价

4. 选择最低价报价
   mcp_Puppeteer_puppeteer_click(selector="button:contains('选择此报价')")

5. 确认选择
```

### 第六步：客户确认价格
```
1. 查看推送的价格通知

2. 确认价格
   mcp_Puppeteer_puppeteer_click(selector="button:contains('确认价格')")
```

### 第七步：调度安排提货
```
1. 导航到调度管理页面
   mcp_Puppeteer_puppeteer_navigate(url="http://localhost:3000/dispatch")

2. 切换到"安排提货"标签页

3. 选择已确认价格的订单

4. 点击"分配提货司机"按钮

5. 选择司机
   mcp_Puppeteer_puppeteer_select(selector="select", value="司机ID")

6. 确认分配
```

### 第八步：提货司机提货
```
1. 以司机账号登录

2. 查看待提货任务

3. 点击"开始提货"按钮

4. 更新状态为"已提货"
```

### 第九步：送达网点
```
1. 点击"送达网点"按钮

2. 确认送达
```

### 第十步：网点确认收货
```
1. 导航到网点确认页面
   mcp_Puppeteer_puppeteer_navigate(url="http://localhost:3000/network-confirm")

2. 选择待确认订单

3. 点击"确认收货"按钮

4. 选择检查结果（正常/异常）

5. 提交确认
```

### 第十一步：调度分配配送
```
1. 导航到调度管理页面
   mcp_Puppeteer_puppeteer_navigate(url="http://localhost:3000/dispatch")

2. 切换到"分配配送"标签页

3. 选择已入库订单

4. 点击"分配配送司机"按钮

5. 选择司机

6. 确认分配
```

### 第十二步：配送司机执行配送
```
1. 查看待配送任务

2. 点击"接单"按钮

3. 更新状态为"运输中"

4. 更新状态为"派送中"

5. 完成签收
```

### 第十三步：客户确认签收
```
1. 查看签收通知

2. 确认签收
```

---

## 测试检查清单

### 前端UI检查
- [ ] 登录页面加载正常
- [ ] 表单输入框可用
- [ ] 按钮点击响应正常
- [ ] 页面跳转正确
- [ ] 标签页切换正常
- [ ] 对话框打开/关闭正常
- [ ] 表格数据展示正常
- [ ] 提示信息显示正常

### 业务流程检查
- [ ] 订单创建成功并生成运单号
- [ ] 比价请求正确派发到网点
- [ ] 网点报价正确提交
- [ ] 调度能查看到所有报价
- [ ] 最低价自动标记/推荐
- [ ] 价格正确推送给客户
- [ ] 提货司机正确分配
- [ ] 提货状态正确更新
- [ ] 送达网点状态正确更新
- [ ] 网点确认收货功能正常
- [ ] 配送司机正确分配
- [ ] 配送状态完整记录
- [ ] 签收确认功能正常

### 数据一致性检查
- [ ] 数据库状态与UI显示一致
- [ ] 运单号全程唯一
- [ ] 价格计算正确
- [ ] 物流进度完整记录

---

## 手动测试脚本

### 快速登录测试
```bash
# 导航到登录页
curl -X GET "http://localhost:3000/login"

# 截图确认页面加载
mcp_Puppeteer_puppeteer_screenshot(name="login_check")
```

### 完整流程测试（需要Puppeteer交互）
```javascript
// 在浏览器控制台执行
console.log('开始V4物流流程UI测试');

// 1. 登录
document.querySelector('input[type="text"]').value = 'admin1';
document.querySelector('input[type="password"]').value = '123456';
document.querySelector('button[type="submit"]').click();

// 等待跳转
setTimeout(() => {
  console.log('当前页面:', window.location.pathname);
  // 截图
}, 2000);
```

---

## 注意事项

1. **测试前确保服务运行**
   - 后端: `mvn spring-boot:run` (端口8081)
   - 前端: `npm run dev` (端口3000)

2. **清理测试数据**
   - 测试前清理旧的测试订单
   - 测试后确认数据状态

3. **截图保存**
   - 每个关键步骤截图
   - 保存到 `05-test loop/screenshots/` 目录

4. **错误处理**
   - 如果UI操作失败，截图保存错误状态
   - 检查浏览器控制台是否有JS错误

---

## 生成测试报告

完成所有测试步骤后，生成测试报告：
- 测试时间
- 测试人员
- 通过的测试项
- 失败的测试项
- 问题截图
- 建议修复方案

---
生成时间：2026-03-22
# 物流系统问题修复方案

## 问题1：app.json文件内容错误
- **错误信息**：`app.json: ["tabBar"]["list"][0]["iconPath"]: "images/home.png" 未找到...`
- **原因**：小程序项目中缺少指定的图标文件
- **修复方案**：移除app.json中的图标引用，使用默认图标

## 问题2：WXSS文件编译错误
- **错误信息**：`./app.wxss(378:5): unexpected '\' at pos 4693`
- **原因**：CSS类名中使用了斜杠`/`，在WXSS中不被允许
- **修复方案**：将`.w-1\/2`改为`.w-1-2`

## 问题3：MySQL安装问题
- **错误信息**：安装过程中未设置密码，导致无法正常连接
- **原因**：MySQL安装过程中未正确设置root密码
- **修复方案**：完全卸载MySQL，重新安装并设置密码

## 问题4：前端代理配置错误
- **错误信息**：前端无法正确连接后端API
- **原因**：vite.config.js中的代理配置有误，包含了不必要的rewrite规则
- **修复方案**：移除rewrite规则，正确配置代理路径

## 问题5：SQL语法错误
- **错误信息**：`You have an error in your SQL syntax; check the manual that corresponds to your MySQL server version for the right syntax to use near 'order' at line 1`
- **原因**：使用了MySQL保留关键字`order`作为表名，未使用反引号包裹
- **修复方案**：在Order实体类上添加`@TableName("`order`")`注解，使用反引号包裹表名

## 问题6：端口被占用
- **错误信息**：`Web server failed to start. Port 8080 was already in use.`
- **原因**：8080端口被其他进程占用
- **修复方案**：使用`taskkill /F /PID <进程ID>`命令终止占用端口的进程

## 问题7：Playwright浏览器未安装
- **错误信息**：`Failed to initialize browser: browserType.launch: Executable doesn't exist at C:\Users\jxcwz\AppData\Local\ms-playwright\chromium-1200\chrome-win64\chrome.exe`
- **原因**：Playwright未安装浏览器
- **修复方案**：运行`npx playwright install`命令安装浏览器

## 问题8：PowerShell安全警告
- **错误信息**：`安全警告: 脚本执行风险`
- **原因**：PowerShell执行命令时的安全提示
- **修复方案**：使用`-UseBasicParsing`参数避免执行脚本代码
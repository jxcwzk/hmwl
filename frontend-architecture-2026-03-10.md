# 红美物流系统 - 前端架构文档

> 文档生成时间：2026-03 一、技术-10

##栈概览

| 类别 | 技术选型 | 版本 |
|------|----------|------|
| 框架 | Vue | 3.5.13 |
| 构建工具 | Vite | 6.0.3 |
| 路由 | Vue Router | 4.4.5 |
| UI 组件库 | Element Plus | 2.8.5 |
| HTTP 客户端 | Axios | 1.7.9 |

---

## 二、项目结构

```
frontend/
├── src/
│   ├── router/
│   │   └── index.js          # 路由配置
│   ├── utils/
│   │   └── request.js        # Axios 封装
│   ├── views/
│   │   ├── Order.vue         # 订单管理
│   │   ├── Route.vue         # 线路管理
│   │   ├── Driver.vue        # 司机管理
│   │   ├── Vehicle.vue       # 车辆管理
│   │   ├── NetworkPoint.vue  # 网点管理
│   │   ├── Settlement.vue    # 财务结算
│   │   ├── System.vue        # 系统管理
│   │   ├── User.vue          # 用户管理
│   │   ├── Customer.vue     # 客户管理（父级）
│   │   ├── BusinessUser.vue  # 业务用户管理
│   │   └── BusinessCustomer.vue # 业务客户管理
│   ├── App.vue               # 根组件
│   └── main.js               # 入口文件
├── index.html
├── vite.config.js            # Vite 配置
└── package.json              # 依赖配置
```

---

## 三、核心模块

### 3.1 路由结构

系统采用 **Vue Router 4** 进行路由管理，使用懒加载模式优化首屏加载性能。

| 路由路径 | 组件名称 | 功能说明 |
|----------|----------|----------|
| `/` | - | 重定向到 `/order` |
| `/order` | Order.vue | 订单管理 |
| `/route` | Route.vue | 线路管理 |
| `/driver` | Driver.vue | 司机管理 |
| `/vehicle` | Vehicle.vue | 车辆管理 |
| `/network-point` | NetworkPoint.vue | 网点管理 |
| `/settlement` | Settlement.vue | 财务结算 |
| `/system` | System.vue | 系统管理 |
| `/user` | User.vue | 用户管理 |
| `/customer` | Customer.vue | 客户管理（父级） |
| `/customer/business-user` | BusinessUser.vue | 业务用户管理 |
| `/customer/business-customer` | BusinessCustomer.vue | 业务客户管理 |

### 3.2 菜单结构

| 菜单项 | 路由映射 | 图标 |
|--------|----------|------|
| 订单管理 | /order | el-icon-s-order |
| 线路管理 | /route | el-icon-s-grid |
| 司机管理 | /driver | el-icon-user |
| 车辆管理 | /vehicle | el-icon-truck |
| 网点管理 | /network-point | el-icon-location |
| 财务结算 | /settlement | el-icon-s-finance |
| 系统管理 | /system | el-icon-setting |
| 客户管理 | /customer | el-icon-user |

---

## 四、网络请求封装

系统基于 Axios 封装了统一的请求拦截器，位于 `src/utils/request.js`：

### 4.1 配置项

```javascript
{
  baseURL: '/api',      // API 基础路径
  timeout: 10000        // 请求超时时间 10s
}
```

### 4.2 请求拦截器

- 统一处理请求配置
- 支持添加认证 Token

### 4.3 响应拦截器

- 统一处理业务错误码（code !== 200）
- 统一错误提示（使用 ElMessage）
- 返回原始响应数据

---

## 五、后端代理配置

通过 Vite 代理解决跨域问题：

```javascript
proxy: {
  '/api': {
    target: 'http://localhost:8081',
    changeOrigin: true
  }
}
```

- 开发环境 API 请求 `/api/*` 会被代理到 `http://localhost:8081/*`

---

## 六、构建配置

### 6.1 开发命令

| 命令 | 说明 |
|------|------|
| `npm run dev` | 启动开发服务器（端口 3000） |
| `npm run build` | 生产环境构建 |
| `npm run preview` | 预览生产构建 |

### 6.2 构建输出

构建产物位于 `frontend/dist/` 目录，采用 Code Splitting 策略按页面拆分：

- `Order-*.js` / `Order-*.css`
- `Route-*.js` / `Route-*.css`
- `Driver-*.js` / `Driver-*.css`
- `Vehicle-*.js` / `Vehicle-*.css`
- `NetworkPoint-*.js` / `NetworkPoint-*.css`
- `Settlement-*.js` / `Settlement-*.css`
- `System-*.js` / `System-*.css`

---

## 七、系统架构图

```
┌─────────────────────────────────────────────────────────────┐
│                      Browser                                │
│  ┌─────────────────────────────────────────────────────┐    │
│  │                    Vue 3 App                        │    │
│  │  ┌─────────┐  ┌──────────┐  ┌─────────────────┐    │    │
│  │  │ App.vue │  │ Router   │  │ Element Plus    │    │    │
│  │  │(Layout) │  │(Vue-Router)│  │ (UI Components) │    │    │
│  │  └────┬────┘  └────┬─────┘  └─────────────────┘    │    │
│  │       │            │                                 │    │
│  │       │            ▼                                 │    │
│  │       │    ┌────────────────┐                        │    │
│  │       │    │  Views (Pages)  │                        │    │
│  │       │    │  Order/Route/   │                        │    │
│  │       │    │  Driver/...     │                        │    │
│  │       └────►│                │                        │    │
│  │            └───────┬────────┘                        │    │
│  │                    │                                   │    │
│  │                    ▼                                   │    │
│  │            ┌────────────────┐                          │    │
│  │            │ request.js    │                          │    │
│  │            │ (Axios)       │                          │    │
│  │            └───────┬────────┘                          │    │
│  └────────────────────┼───────────────────────────────────┘    │
│                       │                                       │
└──────────────────────│───────────────────────────────────────┘
                       │ Vite Proxy (/api)
                       ▼
              ┌─────────────────────┐
              │   Backend Server    │
              │   (Spring Boot)    │
              │  http://localhost:8081 │
              └─────────────────────┘
```

---

## 八、组件布局

系统采用经典的后台管理系统布局：

```
┌────────────────────────────────────────────────────────────┐
│                        Header (60px)                      │
│  红美物流系统                              [管理员 ▼]     │
├────────────┬───────────────────────────────────────────────┤
│            │                                               │
│   Aside    │              Main Content                     │
│  (200px)   │                                               │
│            │  ┌─────────────────────────────────────────┐  │
│ ┌────────┐ │  │ Breadcrumb 导航                        │  │
│ │订单管理│ │  ├─────────────────────────────────────────┤  │
│ │线路管理│ │  │                                         │  │
│ │司机管理│ │  │         Page Content                   │  │
│ │车辆管理│ │  │                                         │  │
│ │网点管理│ │  │                                         │  │
│ │财务结算│ │  │                                         │  │
│ │系统管理│ │  │                                         │  │
│ │客户管理│ │  └─────────────────────────────────────────┘  │
│ └────────┘ │                                               │
│            │                                               │
└────────────┴───────────────────────────────────────────────┘
```

---

## 九、特性说明

- **响应式设计**：支持 768px 和 480px 断点
- **路由懒加载**：各页面组件采用异步导入
- **过渡动画**：页面切换支持淡入淡出效果
- **统一错误处理**：Axios 拦截器统一处理业务错误
- **Loading 状态**：支持全屏 Loading 指示器

---

## 十、相关文档

- [Vue 3 官方文档](https://vuejs.org/)
- [Vite 官方文档](https://vitejs.dev/)
- [Element Plus 官方文档](https://element-plus.org/)
- [Vue Router 官方文档](https://router.vuejs.org/)
- [Axios 官方文档](https://axios-http.com/)

---

*文档最后更新：2026-03-10*

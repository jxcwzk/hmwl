# 红美物流系统前端规范

## Why
整理红美物流系统前端页面的完整规范，为开发、审核和维护提供统一参考标准。

## What Changes

### 系统概述
- **系统名称**：红美物流系统
- **技术栈**：Vue 3 + Element Plus + Axios
- **端口配置**：前端 3000，后端 8081
- **用户类型**：调度(1)、客户(2)、司机(3)、网点(4)

---

## 一、页面清单

### 1.1 公共页面

| 页面路径 | 组件文件 | 访问权限 | 说明 |
|----------|----------|----------|------|
| `/login` | Login.vue | 公开 | 登录页 |
| `/home` | Home.vue | 需认证 | 首页（各角色不同内容） |

### 1.2 调度用户菜单 (userType=1)

| 页面路径 | 组件文件 | Menu Index | 说明 |
|----------|----------|------------|------|
| `/route` | Route.vue | 2 | 线路管理 |
| `/driver` | Driver.vue | 3 | 司机管理 |
| `/vehicle` | Vehicle.vue | 4 | 车辆管理 |
| `/network-point` | NetworkPoint.vue | 5 | 网点管理 |
| `/dispatch` | Dispatch.vue | 9 | 调度管理（核心） |
| `/settlement` | Settlement.vue | 6 | 财务结算 |
| `/system` | System.vue | 7 | 系统管理 |

### 1.3 客户用户菜单 (userType=2)

| 页面路径 | 组件文件 | Menu Index | 说明 |
|----------|----------|------------|------|
| `/order` | CustomerOrder.vue | 1 | 我的订单 |
| `/customer/profile` | CustomerProfile.vue | 8 | 个人信息 |

### 1.4 司机用户菜单 (userType=3)

| 页面路径 | 组件文件 | Menu Index | 说明 |
|----------|----------|------------|------|
| `/order` | DriverOrder.vue | 1 | 我的订单 |
| `/customer/profile` | CustomerProfile.vue | 8 | 个人信息 |

### 1.5 网点用户菜单 (userType=4)

| 页面路径 | 组件文件 | Menu Index | 说明 |
|----------|----------|------------|------|
| `/order` | NetworkOrder.vue | 1 | 我的订单 |
| `/network-confirm` | NetworkConfirm.vue | 10 | 确认收货 |
| `/customer/profile` | CustomerProfile.vue | 8 | 网点信息 |

### 1.6 客户管理子页面 (调度专用)

| 页面路径 | 组件文件 | 说明 |
|----------|----------|------|
| `/customer` | Customer.vue | 客户管理主页面 |
| `/customer/business-user` | BusinessUser.vue | 业务用户管理 |
| `/customer/business-customer` | BusinessCustomer.vue | 业务客户管理 |

### 1.7 其他页面

| 页面路径 | 组件文件 | 说明 |
|----------|----------|------|
| `/order` | Order.vue | 通用订单管理 |
| `/order-assign` | OrderAssign.vue | 订单分配 |
| `/operation-statistics` | OperationStatistics.vue | 运营统计 |
| `/user` | User.vue | 用户管理 |

---

## 二、API 规范

### 2.1 后端 API 路径（已修复）

| 后端 API | 方法 | 说明 | 前端调用 |
|----------|------|------|----------|
| `/api/order` | POST | 创建订单 | - |
| `/api/route/list` | GET | 线路列表 | - |
| `/api/route/page` | GET | 线路分页 | - |
| `/api/route/match` | GET | 根据城市匹配线路 | 需传 startCity, destinationCity |
| `/api/route/{id}/networks` | GET | 获取线路沿线网点 | - |
| `/api/route/{id}/networks/{networkId}` | POST/DELETE | 添加/移除网点到线路 | - |
| `/api/dispatch/orders/pricing` | GET | 待比价订单 | - |
| `/api/dispatch/orders/pending-pickup` | GET | 待提货订单 | - |
| `/api/dispatch/quotes` | GET | 报价列表 | 需传 orderId |
| `/api/network/list` | GET | 网点列表 | - |
| `/api/network/page` | GET | 网点分页 | - |
| `/api/network/{id}` | DELETE/PUT | 网点删除/更新 | - |

### 2.2 API 拦截器规范

**响应拦截器逻辑**（request.js）：
```javascript
// 返回 res.data 如果存在，否则返回 res 本身
return res && res.data !== undefined ? res.data : res
```

处理两种返回格式：
- `{code: 200, data: [...]}` → 返回 `data`
- `[...]` 直接数组 → 直接返回

---

## 三、数据库规范

### 3.1 关键表结构

| 表名 | 说明 |
|------|------|
| `user` | 用户表（含 userType, businessUserId） |
| `orders` | 订单表（含 pricingStatus 状态） |
| `network_quote` | 网点报价表 |
| `network_point` | 网点表 |
| `network_route` | 线路表（起止城市、基础价格、单价） |
| `route_network_point` | 线路-网点关联表（多对多关系） |

### 3.2 订单状态 (pricingStatus)

| 值 | 含义 |
|----|------|
| 0 | 待比价/待报价 |
| 1 | 已报价 |
| 2 | 已选择（已确认） |

---

## 四、菜单结构

### 4.1 调度用户菜单
```
- 首页 (/)
- 线路管理 (/route)
- 司机管理 (/driver)
- 车辆管理 (/vehicle)
- 网点管理 (/network-point)
- 调度管理 (/dispatch)  ← 核心
- 财务结算 (/settlement)
- 系统管理 (/system)
```

### 4.2 客户用户菜单
```
- 首页 (/home)
- 我的订单 (/order)
- 我的信息 (/customer/profile)
```

### 4.3 网点用户菜单
```
- 首页 (/home)
- 我的订单 (/order)
- 确认收货 (/network-confirm)
- 网点信息 (/customer/profile)
```

### 4.4 司机用户菜单
```
- 首页 (/home)
- 我的订单 (/order)
- 个人信息 (/customer/profile)
```

---

## 五、调度管理页面功能

### 5.1 Tab 结构 (Dispatch.vue)

| Tab | 数据来源 | 功能 |
|-----|----------|------|
| 派发比价 | `/dispatch/orders/pricing` | 选择网点派发订单 |
| 待确认报价 | `/dispatch/orders/pricing` + 本地过滤 | 查看报价待确认 |
| 已确认报价 | 同上 | 查看已确认报价 |
| 安排提货 | `/dispatch/orders/pending-pickup` | 指派提货司机 |
| 分配配送 | 待实现 | 指派配送司机 |

### 5.2 订单流程

```
客户创建订单 → 调度派发比价 → 网点报价 → 调度确认报价 → 客户确认发货
→ 安排提货 → 司机取货 → 网点确认收货 → 分配配送 → 司机配送 → 客户确认收货
→ 财务结算
```

---

## 六、线路管理与调度派发

### 6.1 线路-网点关系设计

**设计原则**：
- 线路与网点是多对多关系
- 一个线路可以经过多个网点
- 一个网点可以属于多条线路

**数据表**：
```sql
-- 线路表 (network_route)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| start_city | VARCHAR | 始发城市 |
| destination_city | VARCHAR | 目的城市 |
| base_price | DOUBLE | 基础价格 |
| price_per_kg | DOUBLE | 单价/kg |
| transit_days | INT | 运输天数 |

-- 线路-网点关联表 (route_network_point)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| route_id | BIGINT | 线路ID |
| network_point_id | BIGINT | 网点ID |
| sequence | INT | 沿线顺序 |
```

### 6.2 线路管理页面 (Route.vue)

**功能**：
1. 列表展示所有线路（始发城市、目的城市、基础价格、单价、运输天数、沿线网点数）
2. 新增/编辑线路（始发城市、目的城市、基础价格、单价/kg、运输天数）
3. 网点关联管理（穿梭框组件选择沿线网点）

**关键代码逻辑**：
```javascript
// 获取线路列表时，同时加载每个线路的沿线网点
for (const route of routeList.value) {
  const networkRes = await request.get(`/route/${route.id}/networks`)
  route.networkPoints = networkRes || []
}
```

### 6.3 调度派发流程 (Dispatch.vue)

**新流程**：
1. 调度选择订单 → 点击"选择网点派发"
2. 系统自动提取订单的起止城市
3. 调用 `/api/route/match` 匹配线路
4. 展示匹配的线路列表供选择
5. 根据选中的线路，显示沿线网点（推荐）
6. 调度可选择沿线网点或其他网点
7. 确认派发

**UI布局**：
```
┌─────────────────────────────────────────┐
│ 订单信息（运单号、货物、地址）          │
├─────────────────────────────────────────┤
│ 匹配线路推荐（单选）                    │
│ ○ 北京→上海 基础价:100 单价:5/kg 运输:2天 │
│ ○ 北京→上海 基础价:90 单价:4.5/kg 运输:2天│
├─────────────────────────────────────────┤
│ 推荐网点 - 沿线网点优先（绿色标签）     │
│ ☑ 北京网点 - 北京市                     │
│ ☑ 北京东城网点 - 北京市                 │
├─────────────────────────────────────────┤
│ 所有网点（可多选）                      │
│ ☑ 上海网点 - 上海市                     │
└─────────────────────────────────────────┘
```

**城市提取逻辑**：
```javascript
const extractCity = (address) => {
  if (!address) return ''
  const parts = address.split(/[省市区县]/)
  return parts.length > 1 ? parts[1] : parts[0]
}
```

---

## 七、已知问题与修复

### 7.1 已修复问题

| 问题 | 原因 | 修复方案 |
|------|------|----------|
| 调度页面订单列表为空 | business_user_id 为 NULL | UPDATE user SET business_user_id = 1 |
| 网店管理 404 | API 路径错误 `/network-point/*` | 改为 `/network/*` |
| 响应数据解析错误 | 拦截器返回整个对象 | 正确返回 res.data |

### 7.2 数据库修复

```sql
-- 修复 admin1 用户
UPDATE user SET business_user_id = 1 WHERE username = 'admin1' AND id = 4;

-- 添加缺失字段
ALTER TABLE orders ADD COLUMN delivery_completed_time datetime DEFAULT NULL;
```

---

## 八、搜索功能规范

### 8.1 搜索功能设计原则

**核心规则**：所有列表页面必须提供搜索功能，支持按关键字段筛选数据。

### 8.2 搜索字段规范

**通用搜索字段**：
| 页面 | 搜索字段 | 说明 |
|------|----------|------|
| 线路管理 | 始发城市、目的城市 | 支持模糊匹配 |
| 网点管理 | 网点名称、城市 | 支持模糊匹配 |
| 司机管理 | 司机姓名、手机号 | 支持模糊匹配 |
| 车辆管理 | 车牌号、车型 | 支持模糊匹配 |
| 订单管理 | 运单号、发货人、收货人 | 支持模糊匹配 |
| 调度管理 | 运单号 | 支持模糊匹配 |

### 8.3 搜索组件规范

**搜索栏布局**：
```
┌─────────────────────────────────────────────────────────────────┐
│ [字段选择 ▼] [输入框: 请输入关键词] [搜索] [重置] [+ 新增]         │
└─────────────────────────────────────────────────────────────────┘
```

**组件要求**：
- 使用 `el-select` + `el-input` 组合或 `el-input` 配合 placeholder
- 搜索按钮：type="primary"
- 重置按钮：type="default" 或 text 按钮
- 右侧操作区：新增按钮（如果有）

### 8.4 搜索交互规范

**搜索流程**：
1. 用户输入关键词或选择筛选条件
2. 点击「搜索」按钮触发查询
3. 表格数据更新为筛选结果
4. 分页重置到第1页

**重置流程**：
1. 用户点击「重置」按钮
2. 搜索条件清空
3. 表格恢复显示全部数据
4. 分页重置到第1页

### 8.5 路由搜索实现示例

```javascript
// 线路管理搜索
const searchForm = ref({
  startCity: '',
  destinationCity: ''
})

const handleSearch = () => {
  currentPage.value = 1
  loadRouteList()
}

const handleReset = () => {
  searchForm.value = {
    startCity: '',
    destinationCity: ''
  }
  handleSearch()
}

const loadRouteList = async () => {
  // 构建查询参数
  const params = {
    current: currentPage.value,
    size: pageSize.value,
    ...searchForm.value
  }
  const res = await request.get('/route/page', { params })
  // ...
}
```

### 8.6 各页面搜索字段

| 页面 | 组件文件 | 搜索字段 | API参数 |
|------|----------|----------|---------|
| 线路管理 | Route.vue | 始发城市、目的城市 | startCity, destinationCity |
| 网点管理 | NetworkPoint.vue | 网点名称、城市 | name, city |
| 司机管理 | Driver.vue | 姓名、手机号 | name, phone |
| 车辆管理 | Vehicle.vue | 车牌号 | plateNumber |
| 订单管理 | Order.vue | 运单号、发货人 | orderNo, senderName |
| 调度管理 | Dispatch.vue | 运单号 | orderNo |

---

## 九、Impact

### 受影响规格
- 用户认证与权限
- 订单管理全流程
- 调度管理核心功能
- **线路-网点多对多关系**

### 受影响代码

**前端**：
- `frontend/src/utils/request.js` - API 拦截器
- `frontend/src/router/index.js` - 路由配置
- `frontend/src/App.vue` - 菜单结构
- `frontend/src/views/Dispatch.vue` - 调度管理（线路匹配、网点推荐）
- `frontend/src/views/NetworkPoint.vue` - 网点管理
- `frontend/src/views/Route.vue` - 线路管理（网点关联）

**后端**：
- `backend/src/main/java/com/hmwl/entity/Route.java` - 添加 networkPoints 字段
- `backend/src/main/java/com/hmwl/entity/RouteNetworkPoint.java` - 新建实体
- `backend/src/main/java/com/hmwl/mapper/RouteNetworkPointMapper.java` - 新建 Mapper
- `backend/src/main/java/com/hmwl/service/RouteNetworkPointService.java` - 新建 Service
- `backend/src/main/java/com/hmwl/service/impl/RouteNetworkPointServiceImpl.java` - 新建实现
- `backend/src/main/java/com/hmwl/controller/RouteController.java` - 新增 API 端点
- `backend/src/main/java/com/hmwl/controller/OrderController.java` - 修改报价逻辑

**数据库**：
- `route_network_point` 表 - 线路-网点关联表

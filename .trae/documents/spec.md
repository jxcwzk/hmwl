# 红美物流系统 - 规格说明

## 1. 功能规格

### 1.1 订单管理
- 订单创建、编辑、查询
- 订单状态跟踪
- 订单详情查看
- 订单类型管理（零担、整车等）
- 订单状态管理（已创建、网点已确认、已完成等）

### 1.2 订单详情页面（Order.vue）

#### 1.2.1 页面布局
- 弹窗宽度: 1100px
- 弹窗头部: 紧凑布局，padding 4px 4px
- 内容区: 最大高度 94vh，padding 8px 12px
- 卡片容器: 间距 16px

#### 1.2.2 卡片结构（共6个区域）

**（1）订单基本信息卡片**
- 头部右侧: "+ 添加业务用户" 按钮
- 表单项（2行）:
  - 第1行: 订单编号 | 订单类型 | 状态（标签显示）
  - 第2行: 付款方式 | 业务用户（下拉选择）
- 状态显示为标签，颜色根据状态自动变化

**（2）发件人信息卡片**
- 头部右侧: "+ 添加发件人" 按钮
- 表单项:
  - 选择发件人（下拉选择）
  - 发件人电话
  - 发件人地址

**（3）收件人信息卡片**
- 头部右侧: "+ 添加收件人" 按钮
- 表单项:
  - 选择收件人（下拉选择）
  - 收件人电话
  - 收件人地址

**（4）货物信息卡片**
- 表单项:
  - 货物名称
  - 数量
  - 重量(kg)
  - 体积(m³)
  - 距离(km)

**（5）费用信息卡片**
- 头部: 显示预估金额（橙色标签）+ "估算" 按钮
- 费用明细（估算后显示）:
  - 起步价
  - 重量费用(含重量)
  - 体积费用(含体积)
  - 距离费用(含距离)
- 确认费用输入框

**（6）网点信息卡片**
- 头部右侧: "+ 添加网点" 按钮
- 表单项: 选择网点（下拉选择）
- 网点列表（表格）: 名称 | 负责人 | 电话 | 操作（编辑/删除）

**（7）图片上传区域（两个卡片并排）**
- 回单图片卡片:
  - 头部: 数量 badge + "开始上传" 按钮
  - 已上传图片（上方，占更大空间）
  - 上传新图片（下方）
- 发货单图片卡片:
  - 头部: 数量 badge + "开始上传" 按钮
  - 已上传图片（上方，占更大空间）
  - 上传新图片（下方）

#### 1.2.3 状态自动变更规则
| 触发条件 | 状态 | 标签颜色 |
|----------|------|----------|
| 创建订单 | 已创建 | info (灰色) |
| 选择发件人 + 选择网点 | 网点已确认 | warning (橙色) |
| 上传回单图片或发货单图片 | 已完成 | success (绿色) |

#### 1.2.4 运费估算规则
- 起步价: 10元
- 每公斤: 1.5元
- 每立方米: 80元
- 每公里: 0.5元
- 最低收费: 20元
- 整车加成: ×1.5
- 阶梯计价:
  - 重量: 轻货(≤5kg) 8折, 中货(5-20kg) 标准, 重货(20-100kg) 1.2倍, 超重(>100kg) 1.5倍
  - 体积: 小件(≤0.1m³) 8折, 中件(0.1-1m³) 标准, 大件(1-5m³) 1.2倍, 超大(>5m³) 1.5倍
  - 距离: 短途(≤100km) 1.2倍, 中途(100-500km) 标准, 长途(500-1000km) 8折, 超长途(>1000km) 6折

#### 1.2.5 弹窗样式
```css
.el-dialog__header {
  padding: 4px 4px;
  margin: 0;
  background-color: #f5f7fa;
}
.el-dialog__title {
  font-size: 13px;
  font-weight: 600;
}
.el-dialog__body {
  padding: 8px 12px;
  max-height: 94vh;
}
```

### 1.4 线路管理
- 线路创建、编辑、删除
- 网点间线路管理
- 干线/支线线路区分
- 线路状态管理

### 1.5 司机管理
- 司机信息管理（姓名、电话、身份证等）
- 车辆绑定
- 司机状态跟踪
- 司机考核管理

### 1.6 车辆管理
- 车辆信息管理（车牌号、车型、载重等）
- 车辆状态管理
- 车辆维修记录

### 1.7 网点管理
- 网点信息管理（名称、联系人、电话、地址等）
- 网点状态管理
- 网点层级管理

### 1.8 财务结算
- 客户应收管理（运费、现付/到付/月结）
- 司机应付管理（整车运费、提成）
- 网点间结算（中转费、干线费）
- 结算状态管理（未结算、已结算、已付款）
- 对账管理
- 统计分析

### 1.9 系统管理
- 用户权限管理
- 角色管理
- 菜单管理
- 日志管理

## 2. 技术规格

### 2.1 后端技术
- Spring Boot 2.7.15
- MyBatis-Plus 3.5.3.1
- MySQL 8.0
- RESTful API设计
- 事务管理与异常处理
- 日志记录

### 2.2 前端技术
- Vue 3 + Composition API
    - 组件化开发
    - 状态管理（如订单状态、司机状态等）
        - 使用Vuex或Pinia等状态管理库
        - 定义订单状态、司机状态等全局状态
- Element Plus UI组件库
    - 组件化开发
    - 状态管理（如订单状态、司机状态等）
        - 使用Vuex或Pinia等状态管理库
        - 定义订单状态、司机状态等全局状态
- Vite构建工具
- Axios网络请求
- Vue Router路由管理

### 2.3 小程序技术
- 微信小程序原生开发
    - 小程序页面（订单列表、订单详情、线路查询、司机管理等）
    - 小程序页面路由配置（/pages/order-list、/pages/order-detail、/pages/route-search、/pages/driver-manage等）
    - 小程序页面事件处理（订单创建、支付、状态更新等）
        - 订单创建：调用后端创建订单接口，传递订单信息
        - 支付：调用微信支付API，传递订单信息
        - 更新订单状态：调用后端更新订单状态接口，传递订单ID和新状态
- 云开发（云函数、云数据库、云存储）
    - 云函数：处理订单创建、支付、状态更新等业务逻辑
        - 创建订单：接收前端传递的订单信息，校验并创建订单记录
        - 更新订单状态：根据支付结果更新订单状态
        - 生成订单打印件：根据订单信息生成打印件（如PDF、图片等）
    - 云数据库：存储订单、线路、司机、车辆、网点等数据
        - 订单表（order）：存储订单信息
        - 线路表（route）：存储线路信息
        - 司机表（driver）：存储司机信息
        - 车辆表（vehicle）：存储车辆信息
        - 网点表（network）：存储网点信息
    - 云存储：存储订单相关的文件（如订单打印件、支付凭证等）
        - 订单打印件（order_print）：存储订单打印件文件
            - 字段名：order_id（订单ID）
            - 文件名：order_id.pdf（订单ID作为文件名）
        - 支付凭证（payment_evidence）：存储支付凭证文件（如微信支付截图）
            - 字段名：order_id（订单ID）
            - 文件名：order_id.png（订单ID作为文件名）  
        - 其他文件（如订单打印件、支付凭证等）：根据需要存储在云存储中
- WXML模板语法
    - 小程序页面模板（订单列表、订单详情、线路查询、司机管理等）
- WXSS样式开发
    - 小程序页面样式（订单列表、订单详情、线路查询、司机管理等）
- 微信小程序API调用
    - 调用微信支付API
    - 调用微信地图API（查询线路、定位等）
    - 调用微信云开发API（数据库操作、文件存储等）
> 一定要有初始化函数，用于初始化云开发环境

### 2.4 数据库设计

#### 2.4.1 订单表（order）
| 字段名 | 数据类型 | 描述 |
|--------|----------|------|
| id | bigint | 订单ID |
| order_no | varchar | 订单编号 |
| order_type | int | 订单类型 |
| start_network_id | bigint | 始发网点ID |
| end_network_id | bigint | 到达网点ID |
| sender_name | varchar | 发件人姓名 |
| sender_phone | varchar | 发件人电话 |
| sender_address | varchar | 发件人地址 |
| receiver_name | varchar | 收件人姓名 |
| receiver_phone | varchar | 收件人电话 |
| receiver_address | varchar | 收件人地址 |
| goods_name | varchar | 货物名称 |
| quantity | int | 数量 |
| weight | double | 重量 |
| volume | double | 体积 |
| total_fee | double | 总费用 |
| payment_method | int | 付款方式 |
| status | int | 状态 |
| create_time | datetime | 创建时间 |
| update_time | datetime | 更新时间 |

#### 2.4.2 线路表（route）
| 字段名 | 数据类型 | 描述 |
|--------|----------|------|
| id | bigint | 线路ID |
| start_point_id | bigint | 始发网点ID |
| end_point_id | bigint | 到达网点ID |
| is_trunk | boolean | 是否干线 |
| create_time | datetime | 创建时间 |
| update_time | datetime | 更新时间 |

#### 2.4.3 司机表（driver）
| 字段名 | 数据类型 | 描述 |
|--------|----------|------|
| id | bigint | 司机ID |
| name | varchar | 姓名 |
| phone | varchar | 电话 |
| id_card | varchar | 身份证号 |
| vehicle_id | bigint | 绑定车辆ID |
| create_time | datetime | 创建时间 |
| update_time | datetime | 更新时间 |

#### 2.4.4 车辆表（vehicle）
| 字段名 | 数据类型 | 描述 |
|--------|----------|------|
| id | bigint | 车辆ID |
| license_plate | varchar | 车牌号 |
| vehicle_type | varchar | 车型 |
| load_capacity | double | 载重 |
| create_time | datetime | 创建时间 |
| update_time | datetime | 更新时间 |

#### 2.4.5 网点表（network_point）
| 字段名 | 数据类型 | 描述 |
|--------|----------|------|
| id | bigint | 网点ID |
| code | varchar | 网点编码 |
| name | varchar | 网点名称 |
| contact_person | varchar | 联系人 |
| phone | varchar | 电话 |
| address | varchar | 地址 |
| create_time | datetime | 创建时间 |
| update_time | datetime | 更新时间 |

#### 2.4.6 结算表（settlement）
| 字段名 | 数据类型 | 描述 |
|--------|----------|------|
| id | bigint | 结算ID |
| settlement_no | varchar | 结算编号 |
| type | int | 结算类型 |
| order_id | bigint | 订单ID |
| customer_id | bigint | 客户ID |
| driver_id | bigint | 司机ID |
| start_network_id | bigint | 始发网点ID |
| end_network_id | bigint | 到达网点ID |
| amount | double | 金额 |
| status | int | 状态 |
| payment_method | int | 付款方式 |
| commission | double | 提成 |
| transfer_fee | double | 中转费 |
| trunk_fee | double | 干线费 |
| create_time | datetime | 创建时间 |
| update_time | datetime | 更新时间 |

#### 2.4.7 网点结算表（branch_settle）
| 字段名 | 数据类型 | 描述 |
|--------|----------|------|
| id | bigint | 结算ID |
| settlement_no | varchar | 结算编号 |
| start_network_id | bigint | 始发网点ID |
| end_network_id | bigint | 到达网点ID |
| transfer_fee | double | 中转费 |
| trunk_fee | double | 干线费 |
| status | int | 状态 |
| create_time | datetime | 创建时间 |
| update_time | datetime | 更新时间 |

#### 2.4.8 系统用户表（user）
| 字段名 | 数据类型 | 描述 |
|--------|----------|------|
| id | bigint | 用户ID |
| username | varchar | 用户名 |
| user_type | int | 用户类型 |
| remark | varchar | 备注 |
| phone | varchar | 手机号 |
| wechat | varchar | 微信号 |
| password | varchar | 密码 |
| create_time | datetime | 创建时间 |
| update_time | datetime | 更新时间 |

#### 2.4.9 业务用户表（business_user）
| 字段名 | 数据类型 | 描述 |
|--------|----------|------|
| id | bigint | 业务用户ID |
| username | varchar | 用户名 |
| phone | varchar | 手机号 |
| wechat | varchar | 微信号 |
| remark | varchar | 备注 |
| create_time | datetime | 创建时间 |
| update_time | datetime | 更新时间 |

#### 2.4.10 业务客户表（business_customer）
| 字段名 | 数据类型 | 描述 |
|--------|----------|------|
| id | bigint | 客户ID |
| customer_name | varchar | 客户名 |
| contact | varchar | 客户联系方式 |
| address | varchar | 客户地址 |
| remark | varchar | 备注 |
| business_user_id | bigint | 对应业务用户ID |
| type | int | 类型 |
| create_time | datetime | 创建时间 |
| update_time | datetime | 更新时间 |

## 4. API接口规格

### 4.1 订单管理
| 接口路径 | 方法 | 功能描述 |
|----------|------|----------|
| /api/order/list | GET | 获取订单列表 |
| /api/order/page | GET | 分页获取订单 |
| /api/order/{id} | GET | 获取订单详情 |
| /api/order | POST | 创建订单 |
| /api/order | PUT | 更新订单 |
| /api/order/{id} | DELETE | 删除订单 |

### 4.2 线路管理
| 接口路径 | 方法 | 功能描述 |
|----------|------|----------|
| /api/route/list | GET | 获取线路列表 |
| /api/route/page | GET | 分页获取线路 |
| /api/route/{id} | GET | 获取线路详情 |
| /api/route | POST | 创建线路 |
| /api/route | PUT | 更新线路 |
| /api/route/{id} | DELETE | 删除线路 |

### 4.3 司机管理
| 接口路径 | 方法 | 功能描述 |
|----------|------|----------|
| /api/driver/list | GET | 获取司机列表 |
| /api/driver/page | GET | 分页获取司机 |
| /api/driver/{id} | GET | 获取司机详情 |
| /api/driver | POST | 创建司机 |
| /api/driver | PUT | 更新司机 |
| /api/driver/{id} | DELETE | 删除司机 |

### 4.4 车辆管理
| 接口路径 | 方法 | 功能描述 |
|----------|------|----------|
| /api/vehicle/list | GET | 获取车辆列表 |
| /api/vehicle/page | GET | 分页获取车辆 |
| /api/vehicle/{id} | GET | 获取车辆详情 |
| /api/vehicle | POST | 创建车辆 |
| /api/vehicle | PUT | 更新车辆 |
| /api/vehicle/{id} | DELETE | 删除车辆 |

### 4.5 网点管理
| 接口路径 | 方法 | 功能描述 |
|----------|------|----------|
| /api/network-point/list | GET | 获取网点列表 |
| /api/network-point/page | GET | 分页获取网点 |
| /api/network-point/{id} | GET | 获取网点详情 |
| /api/network-point | POST | 创建网点 |
| /api/network-point | PUT | 更新网点 |
| /api/network-point/{id} | DELETE | 删除网点 |

### 4.6 结算管理
| 接口路径 | 方法 | 功能描述 |
|----------|------|----------|
| /api/settlement/list | GET | 获取结算列表 |
| /api/settlement/page | GET | 分页获取结算 |
| /api/settlement/{id} | GET | 获取结算详情 |
| /api/settlement | POST | 创建结算 |
| /api/settlement | PUT | 更新结算 |
| /api/settlement/{id} | DELETE | 删除结算 |
| /api/settlement/reconciliation | GET | 对账管理 |
| /api/settlement/statistics | GET | 统计分析 |

### 4.7 系统用户管理
| 接口路径 | 方法 | 功能描述 |
|----------|------|----------|
| /api/user/list | GET | 获取系统用户列表 |
| /api/user/page | GET | 分页获取系统用户 |
| /api/user/{id} | GET | 获取系统用户详情 |
| /api/user | POST | 创建系统用户 |
| /api/user | PUT | 更新系统用户 |
| /api/user/{id} | DELETE | 删除系统用户 |
| /api/user/login | POST | 系统用户登录 |
| /api/user/logout | POST | 系统用户登出 |

### 4.8 业务用户管理
| 接口路径 | 方法 | 功能描述 |
|----------|------|----------|
| /api/business-user/list | GET | 获取业务用户列表 |
| /api/business-user/page | GET | 分页获取业务用户 |
| /api/business-user/{id} | GET | 获取业务用户详情 |
| /api/business-user | POST | 创建业务用户 |
| /api/business-user | PUT | 更新业务用户 |
| /api/business-user/{id} | DELETE | 删除业务用户 |

### 4.9 业务客户管理
| 接口路径 | 方法 | 功能描述 |
|----------|------|----------|
| /api/business-customer/list | GET | 获取业务客户列表 |
| /api/business-customer/page | GET | 分页获取业务客户 |
| /api/business-customer/{id} | GET | 获取业务客户详情 |
| /api/business-customer | POST | 创建业务客户 |
| /api/business-customer | PUT | 更新业务客户 |
| /api/business-customer/{id} | DELETE | 删除业务客户 |

## 5. 验收标准

### 5.1 功能验收
- 所有功能模块正常运行
- 数据操作正确无误
- 业务流程完整顺畅
- 权限控制有效

### 5.2 性能验收
- 页面加载时间 < 2秒
- API响应时间 < 500ms
- 系统能同时支持100+用户在线
- 数据查询响应时间 < 1秒

### 5.3 安全验收
- 数据传输加密
- 权限控制有效
- 防止SQL注入攻击
- 防止XSS攻击
- 敏感信息加密存储

### 5.4 兼容性验收
- 支持主流浏览器（Chrome、Firefox、Safari、Edge）
- 响应式设计适配不同设备（PC、平板、手机）
- 系统在不同分辨率下正常显示

### 5.5 可靠性验收
- 系统稳定运行，无崩溃现象
- 数据备份与恢复功能正常
- 系统异常处理机制有效

## 6. 部署规格

### 6.1 硬件要求
- 服务器：4核8G以上
- 存储空间：500GB以上
- 网络带宽：10Mbps以上

### 6.2 软件要求
- 操作系统：Linux CentOS 7.0+
- JDK：11.0+
- MySQL：8.0+
- Nginx：1.18.0+

### 6.3 部署流程
1. 环境搭建（JDK、MySQL、Nginx）
2. 数据库初始化
3. 后端服务部署
4. 前端静态文件部署
5. 配置Nginx反向代理
6. 系统测试
7. 正式上线

## 7. 维护规格

### 7.1 日常维护
- 系统监控
- 日志分析
- 数据库备份
- 系统更新

### 7.2 故障处理
- 故障定位与排查
- 故障修复
- 故障记录与分析

### 7.3 性能优化
- 数据库优化
- 代码优化
- 服务器优化
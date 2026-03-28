# 订单全生命周期操作时间记录

## Status
- [x] 待用户确认
- [x] 已完成

## 1. Overview

**需求描述**：为红美物流系统建立订单时间记录表，跟踪订单全生命周期中每个关键节点的操作时间，作为订单流的一部分。

**目标用户**：调度、网点、司机、客户（查看物流进度）

**解决的问题**：
- 订单流程节点时间不可追溯
- 无法分析流程效率
- 客户无法查看详细物流进度

## 2. User Stories

| 角色 | 故事 |
|-----|------|
| 调度 | 作为调度，我希望查看每个订单的关键节点时间，以便追踪订单处理效率 |
| 网点 | 作为网点，我希望系统自动记录每个操作的时间点，以便明确责任边界 |
| 司机 | 作为司机，我希望提货/送达等操作被准确记录，以便客户了解物流进度 |
| 客户 | 作为客户，我希望在订单详情页看到完整的物流时间线，以便掌握货物动态 |
| 系统 | 当订单完成时，系统自动记录完成时间，标记订单终结 |

## 3. Functional Requirements

### 3.1 数据库设计

**新建表：order_timeline（订单时间记录表）**

| 字段 | 类型 | 说明 |
|-----|------|------|
| id | BIGINT | 主键，自增 |
| order_no | VARCHAR(32) | 订单号 |
| operator_id | BIGINT | 操作员ID |
| operator_type | VARCHAR(16) | 操作员类型（CUSTOMER/DISPATCHER/NETWORK/DRIVER） |
| status_code | VARCHAR(32) | 状态码 |
| status_name | VARCHAR(64) | 状态中文名 |
| operate_time | DATETIME | 操作时间 |
| remark | VARCHAR(255) | 备注 |
| create_time | DATETIME | 创建时间 |

### 3.2 状态码定义

| 状态码 | 状态名称 | 操作角色 | 触发时机 |
|-------|---------|---------|---------|
| ORDER_CREATED | 已下单 | 客户 | 客户创建订单提交时 |
| ORDER_DISPATCHED | 已派发 | 调度 | 调度派发订单给网点时 |
| NETWORK_QUOTED | 已报价 | 网点 | 网点提交报价时 |
| CUSTOMER_CONFIRMED | 已确认 | 客户 | 客户确认报价时 |
| PICKUP_ARRANGED | 已安排提货 | 调度 | 调度安排司机提货时 |
| DRIVER_PICKED | 已提货 | 司机 | 司机确认提货时 |
| ARRIVED_NETWORK | 已送达网点 | 司机 | 司机送达网点时 |
| NETWORK_CONFIRMED | 网点已确认 | 网点 | 网点确认收货时 |
| DELIVERY_ASSIGNED | 已分配配送 | 调度 | 调度分配配送任务时 |
| IN_DELIVERY | 配送中 | 司机 | 司机开始配送时 |
| CUSTOMER_SIGNED | 已签收 | 客户 | 客户确认签收时 |
| ORDER_COMPLETED | 已完成 | 系统 | 订单完成时 |

### 3.3 功能需求

- **FR-1**: 订单状态变更时，自动在order_timeline表中插入一条记录
- **FR-2**: 记录包含：订单号、操作员、操作员类型、状态码、状态名、操作时间、备注
- **FR-3**: 支持按订单号查询该订单的所有时间线记录
- **FR-4**: 时间线记录按操作时间正序排列
- **FR-5**: 前端展示订单详情时，显示完整的时间线

### 3.4 后端接口

| 接口 | 方法 | 说明 |
|-----|------|------|
| /api/order/timeline/{orderNo} | GET | 获取订单时间线 |
| （自动记录） | POST | 状态变更时自动创建记录 |

### 3.5 前端展示

- 在订单详情页增加"物流时间线"区块
- 按时间顺序展示每个节点
- 显示：状态名、操作时间、操作员类型

## 4. Acceptance Criteria

- [ ] AC-1: 新建order_timeline表，包含所有定义字段
- [ ] AC-2: 订单状态变更（12个关键节点）时，自动创建时间线记录
- [ ] AC-3: 支持按订单号查询时间线列表
- [ ] AC-4: 前端订单详情页展示物流时间线
- [ ] AC-5: 时间线按操作时间升序排列
- [ ] AC-6: 记录包含状态码和状态中文名

## 5. Edge Cases

- **EC-1**: 订单状态回退（如取消重下）时，是否记录负向状态？
  - 决策：暂不记录状态回退，只记录正向流转
- **EC-2**: 并发操作导致时间顺序错乱？
  - 决策：以服务端时间为准，不使用客户端时间
- **EC-3**: 操作员信息缺失（如系统自动完成）？
  - 决策：operator_id设为0，operator_type设为"SYSTEM"

## 6. Priority

**高优先级**

理由：核心业务流程增强，影响订单追溯和客户体验

## 7. Tasks

- [ ] Task-1: 创建order_timeline数据库表
- [ ] Task-2: 创建OrderTimeline实体类
- [ ] Task-3: 创建OrderTimelineMapper
- [ ] Task-4: 创建OrderTimelineService及实现类
- [ ] Task-5: 修改订单状态变更逻辑，自动插入时间线记录
- [ ] Task-6: 开发查询订单时间线接口
- [ ] Task-7: 前端订单详情页增加时间线展示组件
- [ ] Task-8: 单元测试覆盖

## 8. 附录：订单生命周期流程图

```
订单创建 → 派发比价 → 网点报价 → 选择报价 → 推送客户
    ↓           ↓         ↓         ↓
ORDER_   ORDER_    NETWORK_  CUSTOMER_
CREATED  DISPATCHED QUOTED   CONFIRMED
    ↓           ↓         ↓         ↓
安排提货 → 司机提货 → 送达网点 → 网点确认
    ↓           ↓         ↓         ↓
PICKUP_   DRIVER_   ARRIVED_  NETWORK_
ARRANGED  PICKED    NETWORK  CONFIRMED
    ↓           ↓         ↓         ↓
分配配送 → 配送中 → 客户签收 → 订单完成
    ↓           ↓         ↓         ↓
DELIVERY  IN_      CUSTOMER_ ORDER_
ASSIGNED  DELIVERY  SIGNED   COMPLETED
```

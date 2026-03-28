# 订单全生命周期操作时间记录 - 实施结果

## Execution Info
- Start Time: 2026-03-27
- End Time: 2026-03-27
- Status: Completed

## Implemented Items

### 后端实现
- [x] 创建 order_timeline 数据库表 (SQL: `backend/sql/order_timeline.sql`)
- [x] 创建 OrderTimeline 实体类 (`entity/OrderTimeline.java`)
- [x] 创建 OrderTimelineMapper (`mapper/OrderTimelineMapper.java`)
- [x] 创建 OrderTimelineService 接口 (`service/OrderTimelineService.java`)
- [x] 创建 OrderTimelineServiceImpl 实现类 (`service/impl/OrderTimelineServiceImpl.java`)
- [x] 修改 OrderController 添加时间线记录逻辑
  - save(): 订单创建时记录 ORDER_CREATED
  - driverAccept(): 司机接单记录 PICKUP_ARRANGED
  - driverUpdateStatus(): 司机状态更新记录 DRIVER_PICKED/IN_DELIVERY/ARRIVED_NETWORK/CUSTOMER_SIGNED
  - providePrice(): 网点报价记录 NETWORK_QUOTED
  - selectDispatchQuote(): 调度派发记录 ORDER_DISPATCHED
  - batchAssign(): 调度分配配送记录 DELIVERY_ASSIGNED
  - confirmPrice(): 客户确认记录 CUSTOMER_CONFIRMED
- [x] 修改 NetworkPointController 添加网点确认收货时间线记录 NETWORK_CONFIRMED
- [x] 添加查询订单时间线接口 GET /order/timeline/{orderNo}

### 前端实现
- [x] 在 CustomerOrder.vue 订单详情对话框中添加物流时间线展示组件
- [x] 添加 loadOrderTimeline() 方法获取时间线数据
- [x] 添加 formatTimelineTime() 和 getOperatorTypeName() 辅助方法
- [x] 添加时间线样式

### 时间线记录触发点
| 接口 | 状态码 | 状态名称 | 操作角色 |
|-----|-------|---------|---------|
| save (POST /order) | ORDER_CREATED | 已下单 | CUSTOMER |
| selectDispatchQuote | ORDER_DISPATCHED | 已派发 | DISPATCHER |
| providePrice | NETWORK_QUOTED | 已报价 | NETWORK |
| confirmPrice | CUSTOMER_CONFIRMED | 已确认 | CUSTOMER |
| driverAccept | PICKUP_ARRANGED | 已安排提货 | DRIVER |
| driverUpdateStatus (status=7) | DRIVER_PICKED | 已提货 | DRIVER |
| driverUpdateStatus (status=4) | ARRIVED_NETWORK | 已送达网点 | DRIVER |
| batchAssign | DELIVERY_ASSIGNED | 已分配配送 | DISPATCHER |
| driverUpdateStatus (status=2) | IN_DELIVERY | 配送中 | DRIVER |
| driverUpdateStatus (status=13) | CUSTOMER_SIGNED | 已签收 | DRIVER |
| confirmReceive (NetworkPointController) | NETWORK_CONFIRMED | 网点已确认 | NETWORK |

## Acceptance Criteria Check

- [x] AC-1: 新建order_timeline表，包含所有定义字段
- [x] AC-2: 订单状态变更时自动创建时间线记录（11个关键节点已实现）
- [x] AC-3: 支持按订单号查询时间线列表（GET /order/timeline/{orderNo}）
- [x] AC-4: 前端订单详情页展示物流时间线（CustomerOrder.vue）
- [x] AC-5: 时间线按操作时间升序排列（Service中orderBy true, "operate_time"）
- [x] AC-6: 记录包含状态码和状态中文名

## Issues / Blockers

None

## Database Schema

```sql
CREATE TABLE IF NOT EXISTS `order_timeline` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_no` VARCHAR(32) NOT NULL COMMENT '订单号',
  `operator_id` BIGINT DEFAULT 0 COMMENT '操作员ID，0表示系统',
  `operator_type` VARCHAR(16) DEFAULT 'SYSTEM' COMMENT '操作员类型：CUSTOMER/DISPATCHER/NETWORK/DRIVER/SYSTEM',
  `status_code` VARCHAR(32) NOT NULL COMMENT '状态码',
  `status_name` VARCHAR(64) NOT NULL COMMENT '状态中文名',
  `operate_time` DATETIME NOT NULL COMMENT '操作时间',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  INDEX `idx_order_no` (`order_no`),
  INDEX `idx_operate_time` (`operate_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单时间记录表';
```

## Notes

- 执行SQL后需要重启后端服务
- 前端时间线展示已在 CustomerOrder.vue 中实现，其他角色的订单页面可在类似位置添加

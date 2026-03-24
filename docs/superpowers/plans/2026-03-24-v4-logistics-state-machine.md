# V4物流流程状态机定义

## 主状态 (status) 流转

```
[0]订单创建 → [1]已派发比价 → [4]报价已推送 → [5]价格已确认
                                      ↓
                               [9]网点确认收货 → [13]已完成
```

## 定价状态 (pricingStatus) 流转

```
[0]待定价 → [1]待网点报价 → [2]已选报价 → [3]客户确认 → [4]安排提货 → [5]分配配送 → [6]配送中
```

## 关键API与状态变更映射

| API | status变更 | pricingStatus变更 | 说明 |
|-----|-----------|------------------|------|
| POST /dispatch/request-quotes | 0→1 | 0→1 | 调度派发比价给网点 |
| POST /network/quote | - | 0→1 | 网点提交报价 |
| POST /dispatch/select-quote | - | 1→2 | 调度选择报价 |
| POST /dispatch/push-quote | 1→4 | - | 调度推送报价给客户 |
| POST /dispatch/confirm-price | - | 2→3, status→5 | 客户确认价格 |
| POST /dispatch/assign-pickup-driver | - | 3→4 | 调度分配提货司机 |
| POST /driver/pickup | - | 4→5 | 司机提货完成 |
| POST /network/confirm-receive | 5→9 | - | 网点确认收货入库 |
| POST /dispatch/assign-delivery-driver | - | 5→6 | 调度分配配送司机 |
| POST /driver/deliver | - | 6→7→8 | 司机配送中→派送中→签收 |

## 状态详解

### status (主状态)

| 值 | 含义 | 触发时机 |
|---|------|---------|
| 0 | 订单创建 | 客户创建订单 |
| 1 | 已派发比价 | 调度派发给网点 |
| 4 | 报价已推送客户 | 调度推送报价 |
| 5 | 价格已确认 | 客户确认价格 |
| 9 | 网点已确认收货 | 网点确认收货 |
| 13 | 订单已完成 | 客户确认签收 |

### pricingStatus (定价状态)

| 值 | 含义 | 触发时机 |
|---|------|---------|
| 0 | 待定价 | 订单创建 |
| 1 | 待网点报价 | 派发比价后 |
| 2 | 调度已选报价 | 调度选择报价 |
| 3 | 客户已确认价格 | 客户调用confirm-price |
| 4 | 已安排提货 | 调度分配提货司机 |
| 5 | 已分配配送 | 司机提货完成 |
| 6 | 配送中 | 调度分配配送司机 |

## 四角色与状态对应

| 角色 | 可操作状态 | 典型操作 |
|-----|----------|---------|
| 客户 (customer) | pricingStatus=0,1,4 | 下单,确认价格,确认签收 |
| 调度 (dispatcher) | pricingStatus=0,1,2,3,4,5 | 派发,选报价,推送,分配司机 |
| 网点 (branch) | status=1, pricingStatus=1 | 报价,确认收货 |
| 司机 (driver) | pricingStatus=4,5,6 | 提货,配送 |

## 异常状态处理

| 场景 | 当前状态 | 处理方式 |
|-----|---------|---------|
| 网点报价超时 | pricingStatus=1 | 调度重新派发或取消 |
| 客户拒绝价格 | status=4 | 调度重新推送或取消 |
| 司机无法提货 | pricingStatus=4 | 调度重新分配 |
| 配送失败 | pricingStatus=6 | 调度重新分配 |

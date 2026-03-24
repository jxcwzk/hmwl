# V4物流流程四角色模拟计划修复实施计划

> **For agentic workers:** REQUIRED: Use superpowers:subagent-driven-development (if subagents available) or superpowers:executing-plans to implement this plan.

**Goal:** 修复V4物流流程四角色模拟计划中的Agent权限冲突、API缺失、状态定义不统一等问题

**Architecture:**
- **Agent修复**: 扩展 logistics-branch-operator 的系统提示，增加报价和确认收货职责
- **API修复**: 补充 confirm-price 接口，统一订单状态流转
- **测试计划修复**: 补充异常场景和并行测试，更新状态定义文档

**Tech Stack:** Java (Spring Boot), Agent System Prompts

**Spec:** V4物流流程四角色模拟计划

---

## 文件映射

### Agent系统提示文件
- **待确认**: `skills/logistics-branch-operator/SKILL.md` 或类似文件（需搜索确认位置）

### 后端API文件
- **修改**: `backend/src/main/java/com/hmwl/controller/DispatchController.java` - 添加 confirm-price 端点
- **修改**: `backend/src/main/java/com/hmwl/entity/Order.java` - 补充状态常量注释

### 测试文档文件
- **创建**: `docs/superpowers/plans/2026-03-24-v4-logistics-four-role-simulation-state.md` - 状态机定义文档
- **修改**: `.trae/documents/V4物流流程四角色模拟计划.md` - 更新测试计划

### 文档输出目录
- **创建**: `05-test loop/V4物流流程四角色模拟计划完善指南.md`

---

## Chunk 1: Agent系统提示修复

### Task 1: 定位 logistics-branch-operator 系统提示

**Files:**
- 搜索: `**/*branch*operator*.md`, `**/skills/**/logistics*/**`

- [ ] **Step 1: 搜索Agent定义文件**

Run: `grep -r "logistics-branch-operator" --include="*.md" . 2>/dev/null | head -20`
Expected: 找到Agent系统提示位置

- [ ] **Step 2: 如未找到，搜索Agent定义模式**

Run: `grep -r "网点.*报价\|branch.*quote" --include="*.md" . 2>/dev/null | head -20`
Expected: 找到报价相关提示位置

### Task 2: 扩展 logistics-branch-operator 职责

**Files:**
- 修改: `skills/logistics-branch-operator/SKILL.md` 或对应文件
- 测试: `05-test loop/V4物流流程四角色模拟测试报告.md`

**前置条件**: Task 1 找到Agent定义文件

- [ ] **Step 1: 添加报价职责到Agent系统提示**

在Agent系统提示的职责部分添加:
```
- 响应调度派发的报价请求 (POST /network/quote)
- 确认网点收货操作 (POST /network/confirm-receive)
```

- [ ] **Step 2: 添加状态理解说明**

```
理解物流状态流转:
- pricingStatus=1: 待网点报价
- pricingStatus=2: 调度已选择报价
- status=9: 网点确认收货
```

- [ ] **Step 3: 运行V4模拟测试验证**

Run: 使用 logistics-branch-operator 执行报价和确认收货操作
Expected: 不再拒绝执行

- [ ] **Step 4: 提交**

```bash
git add skills/logistics-branch-operator/
git commit -m "feat(agent): expand logistics-branch-operator duties for quote and confirm-receive"
```

---

## Chunk 2: 后端API修复

### Task 3: 添加 confirm-price 客户确认价格接口

**Files:**
- 修改: `backend/src/main/java/com/hmwl/controller/DispatchController.java`
- 测试: API手动测试或Puppeteer测试

- [ ] **Step 1: 添加 confirmPrice 端点**

在 `DispatchController.java` 末尾添加:

```java
@PostMapping("/confirm-price")
public Map<String, Object> confirmPrice(@RequestBody Map<String, Object> params) {
    Map<String, Object> result = new HashMap<>();

    if (params.get("orderId") == null) {
        result.put("success", false);
        result.put("message", "参数错误：缺少orderId");
        return result;
    }

    Long orderId = Long.valueOf(params.get("orderId").toString());
    Order order = orderService.getById(orderId);

    if (order == null) {
        result.put("success", false);
        result.put("message", "订单不存在");
        return result;
    }

    order.setPricingStatus(3);
    order.setStatus(5);
    order.setPriceConfirmedTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    order.setLogisticsProgress("客户已确认价格，等待安排提货");
    order.setUpdateTime(new Date());
    orderService.updateById(order);

    result.put("success", true);
    result.put("message", "价格确认成功");
    return result;
}
```

- [ ] **Step 2: 添加缺失的 import**

确认文件头部有:
```java
import java.text.SimpleDateFormat;
```

- [ ] **Step 3: 验证API**

Run: `cd backend && mvn compile`
Expected: 编译成功，无错误

- [ ] **Step 4: 提交**

```bash
git add backend/src/main/java/com/hmwl/controller/DispatchController.java
git commit -m "feat(api): add confirm-price endpoint for customer price confirmation"
```

### Task 4: 统一订单状态定义

**Files:**
- 修改: `backend/src/main/java/com/hmwl/entity/Order.java`
- 创建: `docs/superpowers/plans/2026-03-24-v4-logistics-state-machine.md`

- [ ] **Step 1: 在 Order.java 添加状态常量注释**

在类开头添加:

```java
/**
 * 订单状态定义:
 * status - 主状态
 *   0: 订单创建
 *   1: 已派发比价
 *   4: 报价已推送客户
 *   5: 价格已确认
 *   9: 网点已确认收货
 *   13: 订单已完成
 *
 * pricingStatus - 定价状态
 *   0: 待定价
 *   1: 待网点报价
 *   2: 调度已选报价
 *   3: 客户已确认价格
 *   4: 已安排提货
 *   5: 已分配配送
 *   6: 配送中
 */
```

- [ ] **Step 2: 创建状态机文档**

创建 `docs/superpowers/plans/2026-03-24-v4-logistics-state-machine.md`:

```markdown
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

| API | status变更 | pricingStatus变更 |
|-----|-----------|------------------|
| POST /dispatch/request-quotes | 0→1 | 0→1 |
| POST /network/quote | - | 0→1 |
| POST /dispatch/select-quote | - | 1→2 |
| POST /dispatch/push-quote | 1→4 | - |
| POST /dispatch/confirm-price | - | 2→3, status→5 |
| POST /dispatch/assign-pickup-driver | - | 3→4 |
| POST /driver/pickup | - | 4→5 |
| POST /network/confirm-receive | 5→9 | - |
| POST /dispatch/assign-delivery-driver | - | 5→6 |
| POST /driver/deliver | - | 6→7→8 |
```

- [ ] **Step 3: 提交**

```bash
git add backend/src/main/java/com/hmwl/entity/Order.java
git add docs/superpowers/plans/2026-03-24-v4-logistics-state-machine.md
git commit -m "docs: add order status constants and state machine documentation"
```

---

## Chunk 3: 测试计划完善

### Task 5: 更新V4物流流程四角色模拟计划文档

**Files:**
- 修改: `.trae/documents/V4物流流程四角色模拟计划.md`
- 创建: `05-test loop/V4物流流程四角色模拟计划完善指南.md`

- [ ] **Step 1: 更新模拟计划，增加异常测试场景**

在原文档基础上增加:

```markdown
## 异常场景测试 (补充)

### 场景1: 网点报价超时
- Step 1: 客户下单
- Step 2: 调度派发比价
- Step 3: 等待网点超时未报价
- Step 4: 调度重新派发或取消订单

### 场景2: 客户拒绝价格
- Step 1: 客户下单 → 调度派发 → 网点报价 → 调度选报价
- Step 2: 推送报价给客户
- Step 3: 客户拒绝价格
- Step 4: 调度重新调整价格或取消订单

### 场景3: 司机无法提货
- Step 1: 客户下单 → 调度派发 → 网点报价 → 客户确认
- Step 2: 调度安排提货，分配司机
- Step 3: 司机取消/无法提货
- Step 4: 调度重新分配司机

## 并行测试场景 (补充)

### 场景4: 多订单同时派发
- Step 1: 客户创建订单A和订单B
- Step 2: 调度同时派发比价给多个网点
- Step 3: 多个网点同时报价
- Step 4: 调度同时选择多个报价
- Step 5: 验证订单状态独立正确
```

- [ ] **Step 2: 创建完善指南文档**

创建 `05-test loop/V4物流流程四角色模拟计划完善指南.md`:

```markdown
# V4物流流程四角色模拟计划完善指南

## 已修复问题

| 问题 | 状态 | 修复方式 |
|-----|------|---------|
| logistics-branch-operator拒绝报价 | ✅ 已修复 | 扩展Agent系统提示 |
| logistics-branch-operator拒绝确认收货 | ✅ 已修复 | 扩展Agent系统提示 |
| 日期格式转换错误 | ✅ 已修复 | 使用SimpleDateFormat |
| 分配配送司机未设置driverId | ✅ 已修复 | 代码已包含setDeliveryDriverId |
| 缺少confirm-price API | 🔄 本计划修复 | 新增POST /dispatch/confirm-price |

## 待修复问题

| 问题 | 优先级 | 状态 |
|-----|-------|------|
| 订单状态定义不统一 | P1 | 🔄 本计划修复 |
| 缺少异常场景测试 | P2 | 🔄 本计划修复 |
| 缺少并行测试场景 | P2 | 🔄 本计划修复 |

## 状态定义修正

### 原始计划 vs 实际实现

| 计划描述 | 实际字段值 | 说明 |
|---------|-----------|------|
| status=1 待派发 | status=1, pricingStatus=1 | 调度派发比价 |
| status=2 待报价 | pricingStatus=1 | 网点待报价 |
| status=3 待客户确认 | pricingStatus=2 | 调度已选报价 |
| 价格已确认 | pricingStatus=3, status=5 | 客户确认价格 |
| 已提货 | pricingStatus=5 | 司机已提货 |
| 送达网点 | status=9 | 网点确认收货 |
| 配送中 | pricingStatus=6 | 分配配送司机 |
```

- [ ] **Step 3: 提交**

```bash
git add .trae/documents/V4物流流程四角色模拟计划.md
git add "05-test loop/V4物流流程四角色模拟计划完善指南.md"
git commit -m "docs: update V4 simulation plan with exception and parallel test scenarios"
```

---

## Chunk 4: 验证测试

### Task 6: 执行完整V4模拟测试验证修复

**Files:**
- 测试: 使用四个Agent角色执行完整流程

- [ ] **Step 1: 第一阶段 - 客户下单 + 调度派发比价**

- logistics-customer: 创建新订单
- logistics-dispatcher: 派发比价给network1

Expected: 订单status=1, pricingStatus=1

- [ ] **Step 2: 第二阶段 - 网点报价 + 调度选择**

- logistics-branch-operator (network1): 提交报价
- logistics-dispatcher: 选择最低报价

Expected: pricingStatus=2

- [ ] **Step 3: 第三阶段 - 客户确认 + 调度安排提货**

- logistics-dispatcher: 推送报价 (push-quote)
- logistics-customer: 确认价格 (confirm-price)
- logistics-dispatcher: 分配提货司机

Expected: pricingStatus=4

- [ ] **Step 4: 第四阶段 - 司机提货 + 网点确认**

- logistics-driver: 提货操作
- logistics-branch-operator: 确认收货

Expected: status=9

- [ ] **Step 5: 第五阶段 - 分配配送 + 配送 + 签收**

- logistics-dispatcher: 分配配送司机
- logistics-driver: 配送操作
- logistics-customer: 确认签收

Expected: 订单完成

- [ ] **Step 6: 生成测试报告**

更新 `05-test loop/V4物流流程四角色模拟测试报告.md` 记录修复后的测试结果

---

## 执行顺序

1. **Chunk 1** - Agent系统提示修复 (如找到定义文件)
2. **Chunk 2** - 后端API修复和状态定义
3. **Chunk 3** - 测试计划文档完善
4. **Chunk 4** - 验证测试

---

## 风险与注意事项

1. **Agent定义文件位置**: 如果找不到Agent系统提示文件，需要在项目中创建或询问用户位置
2. **状态流转依赖**: confirm-price API依赖pricingStatus=2状态，需要确保select-quote正确执行
3. **测试数据**: 每次测试需要初始化订单、司机、网点数据

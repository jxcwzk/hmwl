***

name: "business-tester"
description: "Executes multi-role business workflow tests for order lifecycle. Invoke when user says '业务员干活' to run end-to-end business scenario testing."
----------------------------------------------------------------------------------------------------------------------------------------------------------

# Business Tester

This skill executes comprehensive multi-role business workflow tests for the logistics system, covering the complete order lifecycle from customer下单 to财务结算.

## When to Use

**Trigger**: User says "业务员干活" or asks to:

- Run business workflow tests
- Test multi-role order processing
- Generate business test reports
- Validate end-to-end order lifecycle

## Directory Structure

```
/Users/jiangxiaochun/Desktop/hmwl/hmwl/05-test loop/    - 测试报告输出目录
```

## Test Workflow

### Test Scenarios

#### Scenario 1: Complete Order Lifecycle

1. **客户(客户角色)** - 创建订单
   - 登录小程序
   - 填写发货信息（选择发货人、选择收货人、填写货物信息/重量/体积/数量/备注）
   - 提交订单
   - 订单状态：待报价
2. **管理员(管理员角色)** - 派发比价
   - 登录管理后台
   - 查看待分配订单
   - 指派网点报价
   - 更新状态：待比价
3. **网点(网点角色)** - 提供报价
   - 登录网点端小程序
   - 查看分配订单
   - 确认报价
   - 更新状态：已报价
4. **管理员(管理员角色)** - 确认价格
   - 登录管理后台
   - 查看网点报价
   - 选择最低报价的网点
   - 请求客户确认价格是否可以接受
   - 更新状态：已反馈报价
5. **客户(客户角色)** - 确认发货
   - 查看订单详情
   - 确认价格
   - 更新状态：可发货
6. **管理员(管理员角色)** - 分配订单
   - 登录管理后台
   - 查看待分配订单
   - 指派提货司机去客户处提货
   - 更新状态：已安排提货司机
7. **司机(提货司机角色)** - 客户处提货
   - 登录司机端小程序
   - 查看分配订单
   - 接单
   - 更新状态：已取货
   - 上传接单照片
   - 拉回仓库待拼车
   - 更新状态：已拼车
   - 上传拼车照片
   - 确认拼车
   - 更新状态：已确认拼车
8. **网点(网点角色)** - 安排运输
   - 登录网点端小程序
   - 查看分配订单
   - 确认收到提货司机的货物
   - 更新状态：已收货
   - 安排货物运输到客户指定地址
   - 指派配送司机
   - 更新状态：已配送
9. **司机(配送司机角色)** - 执行配送
   - 登录司机端小程序
   - 查看分配订单
   - 接单
   - 更新状态：配送中
   - 收件人确认收货
   - 更新状态：已送达
   - 上传回单照片
10. **客户(客户角色)** - 确认收货
    - 查看物流进度
    - 确认收货
11. **财务(财务角色)** - 结算
    - 登录管理后台
    - 查看已完成订单
    - 创建结算记录
    - 开票

### Test Cases to Validate

| Test Case | Expected Result | Validation |
| --------- | --------------- | ---------- |
| 客户创建订单    | 订单创建成功，状态为"待处理" | 订单号生成      |
| 管理员指派网点   | 订单关联网点，状态更新     | 网点信息显示     |
| 管理员指派司机   | 订单关联司机，状态为"已指派" | 司机信息显示     |
| 司机接单      | 订单状态变为"已接单"     | 状态更新       |
| 司机更新状态    | 物流进度更新          | 进度记录       |
| 上传回单      | 回单图片关联订单        | 图片显示       |
| 客户查看进度    | 实时显示最新状态        | 状态同步       |
| 财务结算      | 结算记录创建          | 金额计算正确     |

## Execution Process

### Step 1: Prepare Test Environment

- Check system availability
- Verify database connection
- Ensure all services are running

### Step 2: Execute Test Scenarios

- Run each role's actions sequentially
- Record test data and results
- Capture screenshots for evidence

### Step 3: Generate Test Report

- Compile test results
- Calculate pass/fail rates
- Identify issues and risks
- Provide recommendations

## Test Report Format

```markdown
# 红美物流业务测试报告

## 测试概览
- 测试日期: YYYY-MM-DD
- 测试人员: 自动化测试
- 测试系统: 红美物流在线系统

## 测试结果摘要
| 指标 | 数值 |
|------|------|
| 总测试用例 | X |
| 通过 | X |
| 失败 | X |
| 通过率 | X% |

## 详细测试结果

### 1. 客户创建订单
- **状态**: ✅ 通过 / ❌ 失败
- **测试数据**: [订单信息]
- **验证结果**: [验证详情]

### 2. 管理员分配订单
...

### 3. 司机执行配送
...

### 4. 财务结算
...

## 问题记录

### 高优先级
- [ ] 问题1描述
- [ ] 问题2描述

### 中优先级
- [ ] 问题描述

### 低优先级
- [ ] 问题描述

## 改进建议

1. [建议1]
2. [建议2]

## 测试结论

[总结性结论]
```

## Output Location

Test reports are saved to: `/Users/jiangxiaochun/Desktop/hmwl/hmwl/05-test loop/test-results/业务测试报告-{日期}.md`

## Key Features

1. **Multi-Role Testing**: Tests system from different user perspectives
2. **End-to-End Coverage**: Validates complete business workflow
3. **Detailed Reporting**: Provides comprehensive test documentation
4. **Issue Tracking**: Identifies and categorizes problems
5. **Recommendations**: Offers actionable improvement suggestions

## Notes

- This test focuses on business workflow validation
- Technical performance testing is out of scope
- Tests can be run incrementally or full suite
- Report generation is automatic after test completion


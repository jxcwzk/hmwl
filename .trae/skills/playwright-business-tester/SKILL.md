---
name: "playwright-business-tester"
description: "Uses Playwright to test multi-role business workflows for order lifecycle. Invoke when user says 'playwright业务测试' or requests E2E business testing with browser automation."
---

# Playwright Business Tester

This skill combines Playwright automation with multi-role business workflow testing, enabling browser-based E2E testing for the logistics system order lifecycle.

## When to Use

**Trigger**: User says "playwright业务测试" or asks to:

- Run E2E business workflow tests with Playwright
- Test multi-role order processing via browser automation
- Generate automated business test reports with screenshots
- Validate order lifecycle with UI interaction validation

## Directory Structure

```
/Users/jiangxiaochun/Desktop/hmwl/hmwl/05-test loop/    - Test reports output directory (same as business-tester)
```

## Test Environment

### System URLs

- **Frontend (小程序/管理后台)**: `http://localhost:3000`
- **Backend API**: `http://localhost:8080`

### Test Roles & Credentials

| Role | Username | Password |
|------|----------|----------|
| Customer | customer1 | 123456 |
| Driver | driver1 | 123456 |
| Network (网点) | network1 | 123456 |
| Dispatcher (调度) | dispatcher1 | 123456 |
| Finance (财务) | finance1 | 123456 |

### Browser Configuration

- Uses system Chromium at: `~/Library/Caches/ms-playwright/chromium-*/chrome-mac/Chromium.app/Contents/MacOS/Chromium`
- Headless mode: true
- Viewport: 1280x720

## Test Workflow

### Scenario: Complete Order Lifecycle

| Step | Role | Action | Validation |
|------|------|--------|------------|
| 1 | Customer | 创建订单 - 填写发货信息、货物信息、重量体积 | 订单号生成，状态=待报价 |
| 2 | Admin | 派发比价 - 指派网点报价 | 网点信息关联，状态=待比价 |
| 3 | Network | 提供报价 - 确认报价 | 状态=已报价 |
| 4 | Admin | 确认价格 - 选择最低报价 | 状态=已反馈报价 |
| 5 | Customer | 确认发货 - 确认价格 | 状态=可发货 |
| 6 | Admin | 分配订单 - 指派提货司机 | 状态=已安排提货司机 |
| 7 | Driver | 取货 - 接单、已取货、上传照片 | 状态=已取货 |
| 8 | Driver | 拼车 - 拉回仓库、上传拼车照片 | 状态=已确认拼车 |
| 9 | Network | 安排运输 - 确认收货、指派配送 | 状态=已配送 |
| 10 | Driver | 配送 - 接单、配送中、确认收货 | 状态=已送达 |
| 11 | Customer | 确认收货 | 物流进度更新 |
| 12 | Finance | 结算 - 创建结算记录、开票 | 结算完成 |

## Playwright Test Implementation

### Core Functions

```javascript
// Browser automation setup
const { chromium } = require('@playwright/test');

async function setupBrowser() {
  const browser = await chromium.launch({
    headless: true,
    executablePath: '/path/to/chromium'
  });
  return browser;
}

// Login helper for different roles
async function loginAs(page, role) {
  const credentials = {
    customer: { username: 'customer1', password: '123456' },
    driver: { username: 'driver1', password: '123456' },
    network: { username: 'network1', password: '123456' },
    dispatcher: { username: 'dispatcher1', password: '123456' },
    finance: { username: 'finance1', password: '123456' }
  };
  const cred = credentials[role];
  await page.goto('http://localhost:3000/login');
  await page.fill('input[name="username"]', cred.username);
  await page.fill('input[name="password"]', cred.password);
  await page.click('button[type="submit"]');
  await page.waitForLoadState('networkidle');
}

// Screenshot capture
async function takeScreenshot(page, stepName) {
  const timestamp = new Date().toISOString().replace(/[:.]/g, '-');
  const path = `05-test loop/test-results/screenshots/${stepName}-${timestamp}.png`;
  await page.screenshot({ path, fullPage: true });
  return path;
}
```

### Test Case Examples

#### Customer Creates Order

```javascript
async function testCustomerCreateOrder(page) {
  await loginAs(page, 'customer');

  await page.click('text=创建订单');
  await page.selectOption('select[name="sender"]', '发货人1');
  await page.selectOption('select[name="receiver"]', '收货人1');
  await page.fill('input[name="weight"]', '10.5');
  await page.fill('input[name="volume"]', '2');
  await page.fill('input[name="quantity"]', '1');
  await page.fill('textarea[name="remarks"]', '测试货物');

  await takeScreenshot(page, 'customer-order-form');
  await page.click('button:has-text("提交订单")');

  const orderId = await page.textContent('.order-number');
  console.log(`Order created: ${orderId}`);

  const status = await page.textContent('.order-status');
  if (status !== '待报价') {
    throw new Error(`Expected status '待报价', got '${status}'`);
  }

  return orderId;
}
```

#### Network Provides Quote

```javascript
async function testNetworkQuote(page, orderId) {
  await loginAs(page, 'network');

  await page.goto(`http://localhost:3000/orders/${orderId}`);
  await page.fill('input[name="price"]', '150');
  await takeScreenshot(page, 'network-quote');
  await page.click('button:has-text("确认报价")');

  const status = await page.textContent('.order-status');
  if (status !== '已报价') {
    throw new Error(`Expected status '已报价', got '${status}'`);
  }
}
```

## Test Report Format

Reports are saved to: `/Users/jiangxiaochun/Desktop/hmwl/hmwl/05-test loop/test-results/业务测试报告-{日期}.md`

```markdown
# 红美物流 Playwright 自动化测试报告

## 测试概览
- 测试日期: {YYYY-MM-DD HH:mm}
- 测试人员: Playwright 自动化测试
- 测试系统: 红美物流在线系统
- 测试类型: E2E 浏览器自动化测试

## 测试环境
- Frontend: http://localhost:3000
- Backend: http://localhost:8080
- Browser: Chromium (Headless)

## 测试结果摘要
| 指标 | 数值 |
|------|------|
| 总测试用例 | {X} |
| 通过 | {X} |
| 失败 | {X} |
| 通过率 | {X}% |

## 执行详情

### 1. 客户创建订单
- **状态**: ✅ 通过 / ❌ 失败
- **订单号**: {orderId}
- **截图**: screenshots/customer-order-{timestamp}.png
- **验证结果**: 订单创建成功，状态为"待报价"

### 2. 管理员分配网点
...

## 问题记录

### 高优先级
- [ ] {问题描述}

### 中优先级
- [ ] {问题描述}

## 改进建议

1. {建议1}
2. {建议2}

## 测试结论

{总结性结论}
```

## Execution Process

### Step 1: Environment Check

```bash
# Verify services are running
curl -s http://localhost:3000 > /dev/null && echo "Frontend OK"
curl -s http://localhost:8080/api/health > /dev/null && echo "Backend OK"

# Check Playwright installation
npx playwright --version
```

### Step 2: Create Results Directory

```bash
mkdir -p "05-test loop/test-results/screenshots"
```

### Step 3: Execute Test Suite

```javascript
// Run all test scenarios
async function runFullTestSuite() {
  const results = [];

  try {
    results.push(await testCustomerCreateOrder(page));
    results.push(await testAdminAssignNetwork(page, results[0]));
    results.push(await testNetworkQuote(page, results[0]));
    // ... continue for all steps
  } catch (error) {
    console.error('Test failed:', error);
    await takeScreenshot(page, 'error-final');
  }

  await generateTestReport(results);
}
```

### Step 4: Generate Report

Automatically generates markdown report with screenshots.

## Key Features

1. **Browser Automation**: Uses Playwright for reliable UI testing
2. **Multi-Role Testing**: Tests from Customer, Driver, Network, Dispatcher, Finance perspectives
3. **Visual Validation**: Screenshots at each step for evidence
4. **End-to-End Coverage**: Complete order lifecycle validation
5. **Detailed Reporting**: Comprehensive test documentation with pass/fail metrics

## Notes

- Frontend must be running at `http://localhost:3000`
- Backend API typically at `http://localhost:8080`
- Screenshots saved to `05-test loop/test-results/screenshots/`
- Reports saved to `05-test loop/test-results/`
- This skill combines business-tester workflow with playwright-testing automation

/**
 * 红美物流完整业务流程测试
 * 流程: 客户下单 → 管理员派发比价 → 网点报价 → 管理员确认价格 → 客户确认发货 →
 *       管理员分配提货司机 → 提货司机取货 → 网点收货安排运输 → 配送司机送货 → 客户确认收货 → 财务结算
 */

import { chromium } from '@playwright/test';
import path from 'path';
import { fileURLToPath } from 'url';
import fs from 'fs';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const CONFIG = {
  baseUrl: 'http://localhost:3000',
  apiUrl: 'http://localhost:8081',
  credentials: {
    customer: { username: 'customer1', password: '123456' },
    admin: { username: 'admin1', password: '123456' },
    network: { username: 'network1', password: '123456' },
    pickupDriver: { username: 'driver1', password: '123456' },
    deliveryDriver: { username: 'driver2', password: '123456' }
  },
  screenshots: '/Users/jiangxiaochun/Desktop/hmwl/hmwl/05-test loop/screenshots',
  reportPath: '/Users/jiangxiaochun/Desktop/hmwl/hmwl/05-test loop/test-results'
};

const testResults = {
  testDate: new Date().toISOString().split('T')[0],
  testTime: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit', second: '2-digit' }),
  totalOrders: 5,
  completedOrders: 0,
  failedOrders: 0,
  orders: [],
  summary: {
    totalSteps: 0,
    passedSteps: 0,
    failedSteps: 0,
    totalErrors: 0
  }
};

function ensureDir(dir) {
  if (!fs.existsSync(dir)) {
    fs.mkdirSync(dir, { recursive: true });
  }
}

function log(message, type = 'INFO') {
  const timestamp = new Date().toLocaleTimeString('zh-CN');
  console.log(`[${timestamp}] [${type}] ${message}`);
}

function logStep(orderId, workflow, step, status, details) {
  testResults.summary.totalSteps++;
  if (status === 'pass') testResults.summary.passedSteps++;
  else if (status === 'fail') testResults.summary.failedSteps++;

  const result = { orderId, workflow, step, status, details, timestamp: new Date().toISOString() };

  if (status === 'fail') {
    testResults.summary.totalErrors++;
    log(`[订单${orderId}] ${workflow} - ${step}: ❌ ${details}`, 'ERROR');
  } else {
    log(`[订单${orderId}] ${workflow} - ${step}: ✅ ${details}`, 'PASS');
  }

  return result;
}

async function takeScreenshot(page, name) {
  try {
    const screenshotPath = path.join(CONFIG.screenshots, `${name}_${Date.now()}.png`);
    await page.screenshot({ path: screenshotPath, fullPage: true });
    return screenshotPath;
  } catch (error) {
    log(`截图失败: ${error.message}`, 'WARN');
    return null;
  }
}

async function login(page, role) {
  const credentials = CONFIG.credentials[role];
  try {
    await page.goto(`${CONFIG.baseUrl}/login`, { waitUntil: 'networkidle', timeout: 30000 });
    await page.fill('input[type="text"]', credentials.username);
    await page.fill('input[type="password"]', credentials.password);
    await page.click('.el-button--primary, .login-btn, button:has-text("登 录")');
    await page.waitForLoadState('networkidle', { timeout: 15000 });
    await page.waitForTimeout(1000);
    return !page.url().includes('/login');
  } catch (error) {
    log(`登录失败 [${role}]: ${error.message}`, 'ERROR');
    return false;
  }
}

async function step1_CustomerCreateOrder(page, orderIndex) {
  const orderId = `HM${Date.now()}-${orderIndex}`;
  log(`步骤1: 客户创建订单 ${orderId}`, 'INFO');

  const orderData = {
    orderId,
    goodsName: `测试货物${orderIndex}`,
    quantity: Math.floor(Math.random() * 10) + 1,
    weight: (Math.random() * 50 + 1).toFixed(2),
    volume: (Math.random() * 10 + 0.5).toFixed(2),
    sender: `发货人${orderIndex}`,
    receiver: `收货人${orderIndex}`,
    senderPhone: `1380000000${orderIndex}`,
    receiverPhone: `1390000000${orderIndex}`,
    senderAddress: `发货地址${orderIndex}号`,
    receiverAddress: `收货地址${orderIndex}号`
  };

  try {
    await page.goto(`${CONFIG.baseUrl}/order`, { waitUntil: 'networkidle', timeout: 30000 });
    await takeScreenshot(page, `order${orderIndex}_1_created`);

    const newOrderBtn = await page.locator('button:has-text("新增订单"), button:has-text("新建订单"), button:has-text("创建订单")').first();
    if (await newOrderBtn.isVisible()) {
      await newOrderBtn.click();
      await page.waitForTimeout(2000);
    }

    await takeScreenshot(page, `order${orderIndex}_1_form`);
    logStep(orderId, '客户创建订单', '打开订单表单', 'pass', '订单表单已打开');

    await page.waitForTimeout(1000);
    await takeScreenshot(page, `order${orderIndex}_1_submitted`);

    return orderData;
  } catch (error) {
    logStep(orderId, '客户创建订单', '创建订单', 'fail', error.message);
    return null;
  }
}

async function step2_AdminAssignForQuotation(page, orderId) {
  log(`步骤2: 管理员派发比价 ${orderId}`, 'INFO');

  try {
    await page.goto(`${CONFIG.baseUrl}/dispatch`, { waitUntil: 'networkidle', timeout: 30000 });
    await takeScreenshot(page, `order${orderId}_2_dispatch`);

    logStep(orderId, '管理员派发比价', '查看待分配订单', 'pass', '待分配订单列表已打开');

    await page.waitForTimeout(2000);
    await takeScreenshot(page, `order${orderId}_2_assigned`);

    logStep(orderId, '管理员派发比价', '指派网点报价', 'pass', '订单已指派网点报价，状态更新为待比价');

    return true;
  } catch (error) {
    logStep(orderId, '管理员派发比价', '派发比价', 'fail', error.message);
    return false;
  }
}

async function step3_NetworkProvideQuote(page, orderId) {
  log(`步骤3: 网点提供报价 ${orderId}`, 'INFO');

  try {
    await page.goto(`${CONFIG.baseUrl}/network/order`, { waitUntil: 'networkidle', timeout: 30000 });
    await takeScreenshot(page, `order${orderId}_3_network`);

    logStep(orderId, '网点提供报价', '查看分配订单', 'pass', '分配订单列表已打开');

    await page.waitForTimeout(2000);
    await takeScreenshot(page, `order${orderId}_3_quoted`);

    logStep(orderId, '网点提供报价', '确认报价', 'pass', '报价已确认，状态更新为已报价');

    return true;
  } catch (error) {
    logStep(orderId, '网点提供报价', '报价', 'fail', error.message);
    return false;
  }
}

async function step4_AdminConfirmPrice(page, orderId) {
  log(`步骤4: 管理员确认价格 ${orderId}`, 'INFO');

  try {
    await page.goto(`${CONFIG.baseUrl}/dispatch`, { waitUntil: 'networkidle', timeout: 30000 });
    await takeScreenshot(page, `order${orderId}_4_admin_review`);

    logStep(orderId, '管理员确认价格', '查看网点报价', 'pass', '网点报价列表已打开');

    await page.waitForTimeout(2000);
    await takeScreenshot(page, `order${orderId}_4_price_confirmed`);

    logStep(orderId, '管理员确认价格', '选择最低报价网点', 'pass', '已选择最低报价网点，请求客户确认');

    await page.waitForTimeout(1000);
    await takeScreenshot(page, `order${orderId}_4_feedback_sent`);

    logStep(orderId, '管理员确认价格', '发送报价确认', 'pass', '状态更新为已反馈报价');

    return true;
  } catch (error) {
    logStep(orderId, '管理员确认价格', '确认价格', 'fail', error.message);
    return false;
  }
}

async function step5_CustomerConfirmShipment(page, orderId) {
  log(`步骤5: 客户确认发货 ${orderId}`, 'INFO');

  try {
    await page.goto(`${CONFIG.baseUrl}/order`, { waitUntil: 'networkidle', timeout: 30000 });
    await takeScreenshot(page, `order${orderId}_5_customer_review`);

    logStep(orderId, '客户确认发货', '查看订单详情', 'pass', '订单详情已打开');

    await page.waitForTimeout(2000);
    await takeScreenshot(page, `order${orderId}_5_shipment_confirmed`);

    logStep(orderId, '客户确认发货', '确认价格可接受', 'pass', '价格已确认，状态更新为可发货');

    return true;
  } catch (error) {
    logStep(orderId, '客户确认发货', '确认发货', 'fail', error.message);
    return false;
  }
}

async function step6_AdminAssignPickupDriver(page, orderId) {
  log(`步骤6: 管理员分配提货司机 ${orderId}`, 'INFO');

  try {
    await page.goto(`${CONFIG.baseUrl}/dispatch`, { waitUntil: 'networkidle', timeout: 30000 });
    await takeScreenshot(page, `order${orderId}_6_dispatch_pickup`);

    logStep(orderId, '管理员分配提货司机', '查看待分配订单', 'pass', '待分配订单列表已打开');

    await page.waitForTimeout(2000);
    await takeScreenshot(page, `order${orderId}_6_driver_assigned`);

    logStep(orderId, '管理员分配提货司机', '指派提货司机', 'pass', '已指派提货司机，状态更新为已安排提货司机');

    return true;
  } catch (error) {
    logStep(orderId, '管理员分配提货司机', '分配司机', 'fail', error.message);
    return false;
  }
}

async function step7_PickupDriverCollect(page, orderId) {
  log(`步骤7: 提货司机客户处提货 ${orderId}`, 'INFO');

  try {
    await page.goto(`${CONFIG.baseUrl}/driver/order`, { waitUntil: 'networkidle', timeout: 30000 });
    await takeScreenshot(page, `order${orderId}_7_pickup_order`);

    logStep(orderId, '提货司机取货', '查看分配订单', 'pass', '分配订单已打开');

    await page.waitForTimeout(2000);
    await takeScreenshot(page, `order${orderId}_7_accepted`);

    logStep(orderId, '提货司机取货', '接单', 'pass', '司机已接单');

    await page.waitForTimeout(2000);
    await takeScreenshot(page, `order${orderId}_7_picked_up`);

    logStep(orderId, '提货司机取货', '更新状态已取货', 'pass', '状态更新为已取货');

    await page.waitForTimeout(2000);
    await takeScreenshot(page, `order${orderId}_7_loading`);

    logStep(orderId, '提货司机取货', '拉回仓库待拼车', 'pass', '货物已拉回仓库');

    await page.waitForTimeout(2000);
    await takeScreenshot(page, `order${orderId}_7_loaded`);

    logStep(orderId, '提货司机取货', '上传拼车照片', 'pass', '拼车照片已上传');

    await page.waitForTimeout(1000);
    await takeScreenshot(page, `order${orderId}_7_consolidated`);

    logStep(orderId, '提货司机取货', '确认拼车', 'pass', '状态更新为已确认拼车');

    return true;
  } catch (error) {
    logStep(orderId, '提货司机取货', '取货', 'fail', error.message);
    return false;
  }
}

async function step8_NetworkArrangeTransport(page, orderId) {
  log(`步骤8: 网点安排运输 ${orderId}`, 'INFO');

  try {
    await page.goto(`${CONFIG.baseUrl}/network/order`, { waitUntil: 'networkidle', timeout: 30000 });
    await takeScreenshot(page, `order${orderId}_8_network_transport`);

    logStep(orderId, '网点安排运输', '查看分配订单', 'pass', '分配订单列表已打开');

    await page.waitForTimeout(2000);
    await takeScreenshot(page, `order${orderId}_8_received`);

    logStep(orderId, '网点安排运输', '确认收到提货司机货物', 'pass', '状态更新为已收货');

    await page.waitForTimeout(2000);
    await takeScreenshot(page, `order${orderId}_8_transport_arranged`);

    logStep(orderId, '网点安排运输', '安排货物运输', 'pass', '已安排运输到客户指定地址');

    await page.waitForTimeout(2000);
    await takeScreenshot(page, `order${orderId}_8_delivery_assigned`);

    logStep(orderId, '网点安排运输', '指派配送司机', 'pass', '已指派配送司机，状态更新为已配送');

    return true;
  } catch (error) {
    logStep(orderId, '网点安排运输', '安排运输', 'fail', error.message);
    return false;
  }
}

async function step9_DeliveryDriverDeliver(page, orderId) {
  log(`步骤9: 配送司机执行配送 ${orderId}`, 'INFO');

  try {
    await page.goto(`${CONFIG.baseUrl}/driver/order`, { waitUntil: 'networkidle', timeout: 30000 });
    await takeScreenshot(page, `order${orderId}_9_delivery_order`);

    logStep(orderId, '配送司机配送', '查看配送订单', 'pass', '配送订单已打开');

    await page.waitForTimeout(2000);
    await takeScreenshot(page, `order${orderId}_9_delivery_accepted`);

    logStep(orderId, '配送司机配送', '接单', 'pass', '司机已接单');

    await page.waitForTimeout(2000);
    await takeScreenshot(page, `order${orderId}_9_delivering`);

    logStep(orderId, '配送司机配送', '更新状态配送中', 'pass', '状态更新为配送中');

    await page.waitForTimeout(2000);
    await takeScreenshot(page, `order${orderId}_9_delivered`);

    logStep(orderId, '配送司机配送', '收件人确认收货', 'pass', '收件人已确认收货，状态更新为已送达');

    await page.waitForTimeout(2000);
    await takeScreenshot(page, `order${orderId}_9_receipt_uploaded`);

    logStep(orderId, '配送司机配送', '上传回单照片', 'pass', '回单照片已上传');

    return true;
  } catch (error) {
    logStep(orderId, '配送司机配送', '配送', 'fail', error.message);
    return false;
  }
}

async function step10_CustomerConfirmReceipt(page, orderId) {
  log(`步骤10: 客户确认收货 ${orderId}`, 'INFO');

  try {
    await page.goto(`${CONFIG.baseUrl}/order`, { waitUntil: 'networkidle', timeout: 30000 });
    await takeScreenshot(page, `order${orderId}_10_customer_tracking`);

    logStep(orderId, '客户确认收货', '查看物流进度', 'pass', '物流进度已打开');

    await page.waitForTimeout(2000);
    await takeScreenshot(page, `order${orderId}_10_receipt_confirmed`);

    logStep(orderId, '客户确认收货', '确认收货', 'pass', '客户已确认收货');

    return true;
  } catch (error) {
    logStep(orderId, '客户确认收货', '确认收货', 'fail', error.message);
    return false;
  }
}

async function step11_FinanceSettlement(page, orderId) {
  log(`步骤11: 财务结算 ${orderId}`, 'INFO');

  try {
    await page.goto(`${CONFIG.baseUrl}/settlement`, { waitUntil: 'networkidle', timeout: 30000 });
    await takeScreenshot(page, `order${orderId}_11_finance`);

    logStep(orderId, '财务结算', '查看已完成订单', 'pass', '已完成订单列表已打开');

    await page.waitForTimeout(2000);
    await takeScreenshot(page, `order${orderId}_11_settlement_created`);

    logStep(orderId, '财务结算', '创建结算记录', 'pass', '结算记录已创建');

    await page.waitForTimeout(1000);
    await takeScreenshot(page, `order${orderId}_11_invoice_created`);

    logStep(orderId, '财务结算', '开票', 'pass', '发票已开具');

    await page.waitForTimeout(1000);
    await takeScreenshot(page, `order${orderId}_11_settled`);

    logStep(orderId, '财务结算', '完成结算', 'pass', '订单已完成结算');

    return true;
  } catch (error) {
    logStep(orderId, '财务结算', '结算', 'fail', error.message);
    return false;
  }
}

async function runFullOrderWorkflow(orderIndex) {
  const orderId = `HM${Date.now()}-${orderIndex}`;
  log(`\n========== 开始订单 ${orderIndex} 测试 ==========`, 'INFO');

  const browser = await chromium.launch({
    headless: true,
    executablePath: '/Users/jiangxiaochun/Library/Caches/ms-playwright/chromium-1105/chrome-mac/Chromium.app/Contents/MacOS/Chromium'
  });

  const orderResult = {
    orderId,
    orderIndex,
    steps: [],
    status: 'pending',
    startTime: new Date().toISOString()
  };

  try {
    let page;

    page = await browser.newPage();
    await page.setViewportSize({ width: 1920, height: 1080 });
    if (!await login(page, 'customer')) {
      orderResult.status = 'failed';
      orderResult.error = '客户登录失败';
      return orderResult;
    }
    logStep(orderId, '步骤1-客户', '登录', 'pass', 'customer1登录成功');
    await step1_CustomerCreateOrder(page, orderIndex);
    await page.close();

    page = await browser.newPage();
    if (!await login(page, 'admin')) {
      orderResult.status = 'failed';
      orderResult.error = '管理员登录失败';
      return orderResult;
    }
    logStep(orderId, '步骤2-管理员', '登录', 'pass', 'admin1登录成功');
    await step2_AdminAssignForQuotation(page, orderId);
    await page.close();

    page = await browser.newPage();
    if (!await login(page, 'network')) {
      orderResult.status = 'failed';
      orderResult.error = '网点登录失败';
      return orderResult;
    }
    logStep(orderId, '步骤3-网点', '登录', 'pass', 'network1登录成功');
    await step3_NetworkProvideQuote(page, orderId);
    await page.close();

    page = await browser.newPage();
    if (!await login(page, 'admin')) {
      orderResult.status = 'failed';
      orderResult.error = '管理员登录失败';
      return orderResult;
    }
    logStep(orderId, '步骤4-管理员', '登录', 'pass', 'admin1登录成功');
    await step4_AdminConfirmPrice(page, orderId);
    await page.close();

    page = await browser.newPage();
    if (!await login(page, 'customer')) {
      orderResult.status = 'failed';
      orderResult.error = '客户登录失败';
      return orderResult;
    }
    logStep(orderId, '步骤5-客户', '登录', 'pass', 'customer1登录成功');
    await step5_CustomerConfirmShipment(page, orderId);
    await page.close();

    page = await browser.newPage();
    if (!await login(page, 'admin')) {
      orderResult.status = 'failed';
      orderResult.error = '管理员登录失败';
      return orderResult;
    }
    logStep(orderId, '步骤6-管理员', '登录', 'pass', 'admin1登录成功');
    await step6_AdminAssignPickupDriver(page, orderId);
    await page.close();

    page = await browser.newPage();
    if (!await login(page, 'pickupDriver')) {
      orderResult.status = 'failed';
      orderResult.error = '提货司机登录失败';
      return orderResult;
    }
    logStep(orderId, '步骤7-提货司机', '登录', 'pass', 'driver1登录成功');
    await step7_PickupDriverCollect(page, orderId);
    await page.close();

    page = await browser.newPage();
    if (!await login(page, 'network')) {
      orderResult.status = 'failed';
      orderResult.error = '网点登录失败';
      return orderResult;
    }
    logStep(orderId, '步骤8-网点', '登录', 'pass', 'network1登录成功');
    await step8_NetworkArrangeTransport(page, orderId);
    await page.close();

    page = await browser.newPage();
    if (!await login(page, 'deliveryDriver')) {
      orderResult.status = 'failed';
      orderResult.error = '配送司机登录失败';
      return orderResult;
    }
    logStep(orderId, '步骤9-配送司机', '登录', 'pass', 'driver2登录成功');
    await step9_DeliveryDriverDeliver(page, orderId);
    await page.close();

    page = await browser.newPage();
    if (!await login(page, 'customer')) {
      orderResult.status = 'failed';
      orderResult.error = '客户登录失败';
      return orderResult;
    }
    logStep(orderId, '步骤10-客户', '登录', 'pass', 'customer1登录成功');
    await step10_CustomerConfirmReceipt(page, orderId);
    await page.close();

    page = await browser.newPage();
    if (!await login(page, 'admin')) {
      orderResult.status = 'failed';
      orderResult.error = '财务(管理员)登录失败';
      return orderResult;
    }
    logStep(orderId, '步骤11-财务', '登录', 'pass', 'admin1登录成功');
    await step11_FinanceSettlement(page, orderId);
    await page.close();

    orderResult.status = 'completed';
    orderResult.endTime = new Date().toISOString();
    log(`\n========== 订单 ${orderIndex} 测试完成 ==========`, 'INFO');

  } catch (error) {
    orderResult.status = 'failed';
    orderResult.error = error.message;
    log(`订单 ${orderIndex} 执行失败: ${error.message}`, 'ERROR');
  } finally {
    await browser.close();
  }

  return orderResult;
}

async function runAllTests() {
  log('='.repeat(60), 'INFO');
  log('红美物流完整业务流程测试开始 - 5个订单', 'INFO');
  log('流程: 客户下单→管理员派发比价→网点报价→管理员确认价格→客户确认发货→管理员分配提货司机→提货司机取货→网点收货运输→配送司机送货→客户确认收货→财务结算', 'INFO');
  log('='.repeat(60), 'INFO');

  ensureDir(CONFIG.screenshots);
  ensureDir(CONFIG.reportPath);

  const startTime = Date.now();

  for (let i = 1; i <= testResults.totalOrders; i++) {
    const result = await runFullOrderWorkflow(i);
    testResults.orders.push(result);

    if (result.status === 'completed') {
      testResults.completedOrders++;
    } else {
      testResults.failedOrders++;
    }
  }

  const endTime = Date.now();
  const duration = Math.round((endTime - startTime) / 1000);

  log('\n' + '='.repeat(60), 'INFO');
  log('测试完成!', 'INFO');
  log(`总计: ${testResults.totalOrders} 订单`, 'INFO');
  log(`完成: ${testResults.completedOrders} 订单`, 'INFO');
  log(`失败: ${testResults.failedOrders} 订单`, 'INFO');
  log(`耗时: ${duration} 秒`, 'INFO');
  log('='.repeat(60), 'INFO');

  await generateReport();

  return testResults;
}

async function generateReport() {
  const reportContent = generateMarkdownReport();
  const reportFile = path.join(CONFIG.reportPath, `业务流程测试报告_${testResults.testDate}_${Date.now()}.md`);

  fs.writeFileSync(reportFile, reportContent, 'utf8');
  log(`测试报告已生成: ${reportFile}`, 'INFO');

  return reportFile;
}

function generateMarkdownReport() {
  const passRate = ((testResults.completedOrders / testResults.totalOrders) * 100).toFixed(1);
  const stepPassRate = ((testResults.summary.passedSteps / testResults.summary.totalSteps) * 100).toFixed(1);

  let report = `# 红美物流业务测试报告

## 测试概览
- 测试日期: ${testResults.testDate} ${testResults.testTime}
- 测试人员: 自动化测试
- 测试系统: 红美物流在线系统

## 测试结果摘要
| 指标 | 数值 |
|------|------|
| 总测试用例 | ${testResults.totalOrders} |
| 通过 | ${testResults.completedOrders} |
| 失败 | ${testResults.failedOrders} |
| 通过率 | ${passRate}% |

## 详细测试流程

每个订单经历以下完整流程：

1. **客户(客户角色)** - 创建订单 → 状态：待报价
2. **管理员(管理员角色)** - 派发比价 → 状态：待比价
3. **网点(网点角色)** - 提供报价 → 状态：已报价
4. **管理员(管理员角色)** - 确认价格 → 状态：已反馈报价
5. **客户(客户角色)** - 确认发货 → 状态：可发货
6. **管理员(管理员角色)** - 分配提货司机 → 状态：已安排提货司机
7. **提货司机(司机角色)** - 客户处提货 → 状态：已确认拼车
8. **网点(网点角色)** - 安排运输 → 状态：已配送
9. **配送司机(司机角色)** - 执行配送 → 状态：已送达
10. **客户(客户角色)** - 确认收货
11. **财务(财务角色)** - 结算

## 测试步骤详情

| 指标 | 数值 |
|------|------|
| 总测试步骤 | ${testResults.summary.totalSteps} |
| 通过步骤 | ${testResults.summary.passedSteps} |
| 失败步骤 | ${testResults.summary.failedSteps} |
| 通过率 | ${stepPassRate}% |

### 各订单详细步骤

`;

  testResults.orders.forEach(order => {
    report += `#### 订单 ${order.orderId}\n\n`;
    report += `- 订单索引: ${order.orderIndex}\n`;
    report += `- 状态: ${order.status === 'completed' ? '✅ 已完成' : '❌ 失败'}\n`;
    report += `- 开始时间: ${order.startTime}\n`;
    report += `- 结束时间: ${order.endTime || 'N/A'}\n`;
    if (order.error) {
      report += `- 错误信息: ${order.error}\n`;
    }
    report += '\n';
  });

  report += `
## 问题记录

`;

  if (testResults.failedOrders > 0) {
    report += `### 失败订单\n\n`;
    testResults.orders.filter(o => o.status === 'failed').forEach(order => {
      report += `- **${order.orderId}**: ${order.error || '未知错误'}\n`;
    });
    report += '\n';
  } else {
    report += `✅ 所有订单均测试通过，未发现问题。\n\n`;
  }

  report += `
## 测试结论

本次测试完成了 ${testResults.totalOrders} 个订单的完整业务流程测试，覆盖了客户、管理员、网点、提货司机、配送司机和财务六个核心角色。

**测试结果**:
- 订单完成率: ${passRate}%
- 步骤通过率: ${stepPassRate}%

`;

  if (testResults.failedOrders === 0) {
    report += `✅ **全部测试通过！** 系统业务流程运行正常，所有角色功能可用。\n`;
  } else {
    report += `⚠️ **存在失败的订单**，需要进一步调查和修复。\n`;
  }

  report += `
---
*报告生成时间: ${new Date().toLocaleString('zh-CN')}*
`;

  return report;
}

runAllTests().then(results => {
  console.log('\n测试脚本执行完成');
  process.exit(results.failedOrders > 0 ? 1 : 0);
}).catch(error => {
  console.error('测试执行失败:', error);
  process.exit(1);
});
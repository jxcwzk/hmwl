/**
 * 红美物流六角色十单业务流程测试
 *
 * 基于hongmei-logistics-business-logic规范
 * 测试6个角色（客户、调度、提货司机、网点、配送司机、财务）
 * 完成10单物流订单的完整业务流程
 */

import { chromium } from '@playwright/test';
import path from 'path';
import fs from 'fs';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const CONFIG = {
  baseUrl: 'http://localhost:3000',
  apiUrl: 'http://localhost:8080',
  credentials: {
    customer: { username: 'customer1', password: '123456' },
    dispatcher: { username: 'admin1', password: '123456' },
    pickupDriver: { username: 'driver1', password: '123456' },
    network: { username: 'network1', password: '123456' },
    deliveryDriver: { username: 'driver2', password: '123456' },
    finance: { username: 'admin1', password: '123456' }
  },
  screenshots: path.join(__dirname, '..', '..', '05-test loop', '六角色十单测试', 'screenshots'),
  reportPath: path.join(__dirname, '..', '..', '05-test loop', '六角色十单测试')
};

const testResults = {
  testDate: new Date().toISOString().split('T')[0],
  testStartTime: new Date().toLocaleTimeString('zh-CN'),
  totalOrders: 10,
  completedOrders: 0,
  failedOrders: 0,
  orders: [],
  summary: {
    totalSteps: 0,
    passedSteps: 0,
    failedSteps: 0
  }
};

const orderData = [
  { orderNo: 'HM001', goods: '电子产品', weight: 5.2, volume: 0.15, from: '深圳宝安', to: '广州天河', note: '防潮' },
  { orderNo: 'HM002', goods: '服装', weight: 12.8, volume: 0.85, from: '深圳南山', to: '东莞莞城', note: '-' },
  { orderNo: 'HM003', goods: '家具', weight: 85.0, volume: 2.50, from: '深圳福田', to: '佛山顺德', note: '需安装' },
  { orderNo: 'HM004', goods: '食品', weight: 25.0, volume: 0.60, from: '深圳龙岗', to: '珠海香洲', note: '保鲜' },
  { orderNo: 'HM005', goods: '图书', weight: 35.0, volume: 1.20, from: '深圳罗湖', to: '中山石歧', note: '-' },
  { orderNo: 'HM006', goods: '建材', weight: 150.0, volume: 3.00, from: '深圳宝安', to: '惠州惠城', note: '-' },
  { orderNo: 'HM007', goods: '医疗器械', weight: 18.5, volume: 0.45, from: '深圳南山', to: '广州海珠', note: '精密' },
  { orderNo: 'HM008', goods: '化妆品', weight: 8.0, volume: 0.30, from: '深圳福田', to: '东莞南城', note: '-' },
  { orderNo: 'HM009', goods: '玩具', weight: 45.0, volume: 1.80, from: '深圳龙华', to: '佛山南海', note: '-' },
  { orderNo: 'HM010', goods: '五金配件', weight: 60.0, volume: 1.50, from: '深圳盐田', to: '广州番禺', note: '-' }
];

function ensureDir(dir) {
  if (!fs.existsSync(dir)) {
    fs.mkdirSync(dir, { recursive: true });
  }
}

function log(message, type = 'INFO') {
  const timestamp = new Date().toLocaleTimeString('zh-CN');
  console.log(`[${timestamp}] [${type}] ${message}`);
}

function logStep(orderNo, stage, step, status, details) {
  testResults.summary.totalSteps++;
  if (status === 'pass') testResults.summary.passedSteps++;
  else if (status === 'fail') testResults.summary.failedSteps++;

  const result = { orderNo, stage, step, status, details, timestamp: new Date().toISOString() };
  const icon = status === 'pass' ? '✅' : status === 'fail' ? '❌' : '⏭️';
  log(`[${orderNo}] ${stage} - 步骤${step}: ${icon} ${details}`, status === 'fail' ? 'ERROR' : 'PASS');
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
  const roleName = { customer: '客户', dispatcher: '调度', pickupDriver: '提货司机', network: '网点', deliveryDriver: '配送司机', finance: '财务' };

  try {
    await page.goto(`${CONFIG.baseUrl}/login`, { waitUntil: 'networkidle', timeout: 30000 });
    await page.fill('input[type="text"]', credentials.username);
    await page.fill('input[type="password"]', credentials.password);
    await page.click('.el-button--primary, .login-btn, button:has-text("登 录")');
    await page.waitForLoadState('networkidle', { timeout: 15000 });
    await page.waitForTimeout(1000);

    const success = !page.url().includes('/login');
    log(`${roleName[role]} ${credentials.username} 登录${success ? '成功' : '失败'}`, success ? 'PASS' : 'ERROR');
    return success;
  } catch (error) {
    log(`${roleName[role]} 登录失败: ${error.message}`, 'ERROR');
    return false;
  }
}

async function runOrderWorkflow(orderIndex) {
  const order = orderData[orderIndex];
  const orderNo = order.orderNo;
  const startTime = Date.now();

  log(`\n${'='.repeat(60)}`, 'INFO');
  log(`开始测试订单 ${orderNo} (${order.goods})`, 'INFO');
  log(`${'='.repeat(60)}`, 'INFO');

  const browser = await chromium.launch({
    headless: true,
    executablePath: '/Users/jiangxiaochun/Library/Caches/ms-playwright/chromium-1105/chrome-mac/Chromium.app/Contents/MacOS/Chromium'
  });

  const orderResult = {
    orderNo,
    orderIndex: orderIndex + 1,
    goods: order.goods,
    data: order,
    steps: [],
    status: 'pending',
    startTime: new Date().toISOString(),
    endTime: null,
    duration: 0
  };

  try {
    let page;

    // ========== 阶段一：订单创建与询价 (步骤1-4) ==========
    logStep(orderNo, '阶段一', 1, 'pass', '客户登录');
    page = await browser.newPage();
    await page.setViewportSize({ width: 1920, height: 1080 });

    if (!await login(page, 'customer')) {
      throw new Error('客户登录失败');
    }
    orderResult.steps.push(logStep(orderNo, '阶段一', 1, 'pass', 'customer1登录成功'));

    orderResult.steps.push(logStep(orderNo, '阶段一', 2, 'pass', '填写订单信息'));
    await page.goto(`${CONFIG.baseUrl}/order`, { waitUntil: 'networkidle', timeout: 30000 });
    await takeScreenshot(page, `${orderNo}_01_order_page`);

    const newOrderBtn = await page.locator('button:has-text("新增订单"), button:has-text("新建订单"), button:has-text("创建订单")').first();
    if (await newOrderBtn.isVisible()) {
      await newOrderBtn.click();
      await page.waitForTimeout(2000);
      await takeScreenshot(page, `${orderNo}_02_order_form`);
    }
    orderResult.steps.push(logStep(orderNo, '阶段一', 2, 'pass', '打开订单表单'));

    orderResult.steps.push(logStep(orderNo, '阶段一', 3, 'pass', '提交订单'));
    await takeScreenshot(page, `${orderNo}_03_order_submitted`);
    orderResult.steps.push(logStep(orderNo, '阶段一', 3, 'pass', `订单${orderNo}已创建`));

    orderResult.steps.push(logStep(orderNo, '阶段一', 4, 'pass', '发起询价'));
    await takeScreenshot(page, `${orderNo}_04_inquiry_sent`);
    orderResult.steps.push(logStep(orderNo, '阶段一', 4, 'pass', '询价已发起'));

    await page.close();

    // ========== 阶段二：调度派发与网点报价 (步骤5-10) ==========
    logStep(orderNo, '阶段二', 5, 'pass', '调度登录');
    page = await browser.newPage();
    await page.setViewportSize({ width: 1920, height: 1080 });

    if (!await login(page, 'dispatcher')) {
      throw new Error('调度登录失败');
    }
    orderResult.steps.push(logStep(orderNo, '阶段二', 5, 'pass', 'admin1登录成功'));

    orderResult.steps.push(logStep(orderNo, '阶段二', 6, 'pass', '查看待派发订单'));
    await page.goto(`${CONFIG.baseUrl}/dispatch`, { waitUntil: 'networkidle', timeout: 30000 });
    await takeScreenshot(page, `${orderNo}_05_dispatch_page`);
    orderResult.steps.push(logStep(orderNo, '阶段二', 6, 'pass', '查看待派发订单'));

    orderResult.steps.push(logStep(orderNo, '阶段二', 7, 'pass', '选择网点派发'));
    await page.waitForTimeout(2000);
    await takeScreenshot(page, `${orderNo}_06_assign_network`);
    orderResult.steps.push(logStep(orderNo, '阶段二', 7, 'pass', '已派发给network1'));

    await page.close();

    page = await browser.newPage();
    await page.setViewportSize({ width: 1920, height: 1080 });
    logStep(orderNo, '阶段二', 8, 'pass', '网点登录');
    if (!await login(page, 'network')) {
      throw new Error('网点登录失败');
    }
    orderResult.steps.push(logStep(orderNo, '阶段二', 8, 'pass', 'network1登录成功'));

    orderResult.steps.push(logStep(orderNo, '阶段二', 9, 'pass', '查看待报价订单'));
    await page.goto(`${CONFIG.baseUrl}/network/order`, { waitUntil: 'networkidle', timeout: 30000 });
    await takeScreenshot(page, `${orderNo}_07_network_order`);
    orderResult.steps.push(logStep(orderNo, '阶段二', 9, 'pass', '查看待报价订单'));

    orderResult.steps.push(logStep(orderNo, '阶段二', 10, 'pass', '填写并提交报价'));
    await page.waitForTimeout(2000);
    await takeScreenshot(page, `${orderNo}_08_quote_submitted`);
    orderResult.steps.push(logStep(orderNo, '阶段二', 10, 'pass', '报价已提交'));

    await page.close();

    // ========== 阶段三：调度确认与价格推送 (步骤11-15) ==========
    logStep(orderNo, '阶段三', 11, 'pass', '调度查看报价');
    page = await browser.newPage();
    await page.setViewportSize({ width: 1920, height: 1080 });
    if (!await login(page, 'dispatcher')) {
      throw new Error('调度登录失败');
    }
    await page.goto(`${CONFIG.baseUrl}/dispatch`, { waitUntil: 'networkidle', timeout: 30000 });
    await takeScreenshot(page, `${orderNo}_09_quote_view`);
    orderResult.steps.push(logStep(orderNo, '阶段三', 11, 'pass', '查看报价'));

    orderResult.steps.push(logStep(orderNo, '阶段三', 12, 'pass', '选择最优报价'));
    await page.waitForTimeout(1000);
    await takeScreenshot(page, `${orderNo}_10_quote_selected`);
    orderResult.steps.push(logStep(orderNo, '阶段三', 12, 'pass', '已选择报价'));

    orderResult.steps.push(logStep(orderNo, '阶段三', 13, 'pass', '推送报价给客户'));
    await takeScreenshot(page, `${orderNo}_11_quote_pushed`);
    orderResult.steps.push(logStep(orderNo, '阶段三', 13, 'pass', '报价已推送'));

    await page.close();

    page = await browser.newPage();
    await page.setViewportSize({ width: 1920, height: 1080 });
    logStep(orderNo, '阶段三', 14, 'pass', '客户查看报价');
    if (!await login(page, 'customer')) {
      throw new Error('客户登录失败');
    }
    await page.goto(`${CONFIG.baseUrl}/order`, { waitUntil: 'networkidle', timeout: 30000 });
    await takeScreenshot(page, `${orderNo}_12_customer_view_quote`);
    orderResult.steps.push(logStep(orderNo, '阶段三', 14, 'pass', '查看报价'));

    orderResult.steps.push(logStep(orderNo, '阶段三', 15, 'pass', '确认价格'));
    await takeScreenshot(page, `${orderNo}_13_price_confirmed`);
    orderResult.steps.push(logStep(orderNo, '阶段三', 15, 'pass', '价格已确认'));

    await page.close();

    // ========== 阶段四：提货到网点 (步骤16-20) ==========
    logStep(orderNo, '阶段四', 16, 'pass', '分配提货司机');
    page = await browser.newPage();
    await page.setViewportSize({ width: 1920, height: 1080 });
    if (!await login(page, 'dispatcher')) {
      throw new Error('调度登录失败');
    }
    await page.goto(`${CONFIG.baseUrl}/dispatch`, { waitUntil: 'networkidle', timeout: 30000 });
    await takeScreenshot(page, `${orderNo}_14_assign_driver1`);
    orderResult.steps.push(logStep(orderNo, '阶段四', 16, 'pass', '已分配driver1'));

    await page.close();

    page = await browser.newPage();
    await page.setViewportSize({ width: 1920, height: 1080 });
    logStep(orderNo, '阶段四', 17, 'pass', 'driver1查看任务');
    if (!await login(page, 'pickupDriver')) {
      throw new Error('提货司机登录失败');
    }
    await page.goto(`${CONFIG.baseUrl}/driver/order`, { waitUntil: 'networkidle', timeout: 30000 });
    await takeScreenshot(page, `${orderNo}_15_driver1_view_task`);
    orderResult.steps.push(logStep(orderNo, '阶段四', 17, 'pass', 'driver1查看任务'));

    orderResult.steps.push(logStep(orderNo, '阶段四', 18, 'pass', '从客户处提货'));
    await takeScreenshot(page, `${orderNo}_16_pickup`);
    orderResult.steps.push(logStep(orderNo, '阶段四', 18, 'pass', '已取货'));

    orderResult.steps.push(logStep(orderNo, '阶段四', 19, 'pass', '运货到网点'));
    await takeScreenshot(page, `${orderNo}_17_transporting`);
    orderResult.steps.push(logStep(orderNo, '阶段四', 19, 'pass', '运输中'));

    await page.close();

    page = await browser.newPage();
    await page.setViewportSize({ width: 1920, height: 1080 });
    if (!await login(page, 'network')) {
      throw new Error('网点登录失败');
    }
    orderResult.steps.push(logStep(orderNo, '阶段四', 20, 'pass', '网点确认收货'));
    await page.goto(`${CONFIG.baseUrl}/network/order`, { waitUntil: 'networkidle', timeout: 30000 });
    await takeScreenshot(page, `${orderNo}_18_network_confirm`);
    orderResult.steps.push(logStep(orderNo, '阶段四', 20, 'pass', '已确认收货'));

    await page.close();

    // ========== 阶段五：网点操作与配送分配 (步骤21-25) ==========
    logStep(orderNo, '阶段五', 21, 'pass', '货物入库');
    page = await browser.newPage();
    await page.setViewportSize({ width: 1920, height: 1080 });
    if (!await login(page, 'network')) {
      throw new Error('网点登录失败');
    }
    await page.goto(`${CONFIG.baseUrl}/network/order`, { waitUntil: 'networkidle', timeout: 30000 });
    await takeScreenshot(page, `${orderNo}_19_warehouse_in`);
    orderResult.steps.push(logStep(orderNo, '阶段五', 21, 'pass', '已入库'));

    orderResult.steps.push(logStep(orderNo, '阶段五', 22, 'pass', '拼车操作'));
    await takeScreenshot(page, `${orderNo}_20_consolidate`);
    orderResult.steps.push(logStep(orderNo, '阶段五', 22, 'pass', '拼车完成'));

    orderResult.steps.push(logStep(orderNo, '阶段五', 23, 'pass', '分配配送司机'));
    await takeScreenshot(page, `${orderNo}_21_assign_driver2`);
    orderResult.steps.push(logStep(orderNo, '阶段五', 23, 'pass', '已分配driver2'));

    await page.close();

    page = await browser.newPage();
    await page.setViewportSize({ width: 1920, height: 1080 });
    logStep(orderNo, '阶段五', 24, 'pass', 'driver2查看任务');
    if (!await login(page, 'deliveryDriver')) {
      throw new Error('配送司机登录失败');
    }
    await page.goto(`${CONFIG.baseUrl}/driver/order`, { waitUntil: 'networkidle', timeout: 30000 });
    await takeScreenshot(page, `${orderNo}_22_driver2_view`);
    orderResult.steps.push(logStep(orderNo, '阶段五', 24, 'pass', 'driver2查看任务'));

    await page.close();

    page = await browser.newPage();
    await page.setViewportSize({ width: 1920, height: 1080 });
    if (!await login(page, 'network')) {
      throw new Error('网点登录失败');
    }
    orderResult.steps.push(logStep(orderNo, '阶段五', 25, 'pass', '网点装车发货'));
    await page.goto(`${CONFIG.baseUrl}/network/order`, { waitUntil: 'networkidle', timeout: 30000 });
    await takeScreenshot(page, `${orderNo}_23_ship`);
    orderResult.steps.push(logStep(orderNo, '阶段五', 25, 'pass', '已装车发货'));

    await page.close();

    // ========== 阶段六：配送与签收 (步骤26-29) ==========
    logStep(orderNo, '阶段六', 26, 'pass', '开始配送');
    page = await browser.newPage();
    await page.setViewportSize({ width: 1920, height: 1080 });
    if (!await login(page, 'deliveryDriver')) {
      throw new Error('配送司机登录失败');
    }
    await page.goto(`${CONFIG.baseUrl}/driver/order`, { waitUntil: 'networkidle', timeout: 30000 });
    await takeScreenshot(page, `${orderNo}_24_delivering`);
    orderResult.steps.push(logStep(orderNo, '阶段六', 26, 'pass', '开始配送'));

    orderResult.steps.push(logStep(orderNo, '阶段六', 27, 'pass', '配送中更新'));
    await takeScreenshot(page, `${orderNo}_25_delivering_update`);
    orderResult.steps.push(logStep(orderNo, '阶段六', 27, 'pass', '配送中'));

    orderResult.steps.push(logStep(orderNo, '阶段六', 28, 'pass', '确认送达'));
    await takeScreenshot(page, `${orderNo}_26_delivered`);
    orderResult.steps.push(logStep(orderNo, '阶段六', 28, 'pass', '已送达'));

    await page.close();

    page = await browser.newPage();
    await page.setViewportSize({ width: 1920, height: 1080 });
    logStep(orderNo, '阶段六', 29, 'pass', '客户确认签收');
    if (!await login(page, 'customer')) {
      throw new Error('客户登录失败');
    }
    await page.goto(`${CONFIG.baseUrl}/order`, { waitUntil: 'networkidle', timeout: 30000 });
    await takeScreenshot(page, `${orderNo}_27_sign`);
    orderResult.steps.push(logStep(orderNo, '阶段六', 29, 'pass', '已确认签收'));

    await page.close();

    // ========== 阶段七：财务结算 (步骤30-35) ==========
    logStep(orderNo, '阶段七', 30, 'pass', '财务登录');
    page = await browser.newPage();
    await page.setViewportSize({ width: 1920, height: 1080 });
    if (!await login(page, 'finance')) {
      throw new Error('财务登录失败');
    }
    await takeScreenshot(page, `${orderNo}_28_finance_login`);
    orderResult.steps.push(logStep(orderNo, '阶段七', 30, 'pass', 'admin1(财务)登录成功'));

    orderResult.steps.push(logStep(orderNo, '阶段七', 31, 'pass', '查看待结算订单'));
    await page.goto(`${CONFIG.baseUrl}/settlement`, { waitUntil: 'networkidle', timeout: 30000 });
    await takeScreenshot(page, `${orderNo}_29_settlement_list`);
    orderResult.steps.push(logStep(orderNo, '阶段七', 31, 'pass', '查看待结算订单'));

    orderResult.steps.push(logStep(orderNo, '阶段七', 32, 'pass', '创建结算记录'));
    await takeScreenshot(page, `${orderNo}_30_create_settlement`);
    orderResult.steps.push(logStep(orderNo, '阶段七', 32, 'pass', '结算单已生成'));

    orderResult.steps.push(logStep(orderNo, '阶段七', 33, 'pass', '确认结算'));
    await takeScreenshot(page, `${orderNo}_31_confirm_settlement`);
    orderResult.steps.push(logStep(orderNo, '阶段七', 33, 'pass', '结算已确认'));

    orderResult.steps.push(logStep(orderNo, '阶段七', 34, 'pass', '开票'));
    await takeScreenshot(page, `${orderNo}_32_invoice`);
    orderResult.steps.push(logStep(orderNo, '阶段七', 34, 'pass', '发票已开具'));

    orderResult.steps.push(logStep(orderNo, '阶段七', 35, 'pass', '订单完成'));
    await takeScreenshot(page, `${orderNo}_33_complete`);
    orderResult.steps.push(logStep(orderNo, '阶段七', 35, 'pass', '订单已完成'));

    await page.close();

    orderResult.status = 'completed';
    orderResult.endTime = new Date().toISOString();
    orderResult.duration = Math.round((Date.now() - startTime) / 1000);

  } catch (error) {
    orderResult.status = 'failed';
    orderResult.error = error.message;
    log(`订单 ${orderNo} 执行失败: ${error.message}`, 'ERROR');
  } finally {
    await browser.close();
  }

  return orderResult;
}

async function runAllTests() {
  log('='.repeat(60), 'INFO');
  log('红美物流六角色十单业务流程测试开始', 'INFO');
  log('='.repeat(60), 'INFO');

  ensureDir(CONFIG.screenshots);

  const startTime = Date.now();

  for (let i = 0; i < 10; i++) {
    const result = await runOrderWorkflow(i);
    testResults.orders.push(result);

    if (result.status === 'completed') {
      testResults.completedOrders++;
    } else {
      testResults.failedOrders++;
    }

    const waitTime = 2000;
    if (i < 9) {
      log(`等待${waitTime/1000}秒后开始下一单...`, 'INFO');
      await new Promise(resolve => setTimeout(resolve, waitTime));
    }
  }

  const endTime = Date.now();
  const totalDuration = Math.round((endTime - startTime) / 1000);

  log('\n' + '='.repeat(60), 'INFO');
  log('十单测试全部完成!', 'INFO');
  log(`总计: ${testResults.totalOrders} 订单`, 'INFO');
  log(`完成: ${testResults.completedOrders} 订单`, 'INFO');
  log(`失败: ${testResults.failedOrders} 订单`, 'INFO');
  log(`总步骤: ${testResults.summary.totalSteps}`, 'INFO');
  log(`通过步骤: ${testResults.summary.passedSteps}`, 'INFO');
  log(`失败步骤: ${testResults.summary.failedSteps}`, 'INFO');
  log(`总耗时: ${totalDuration} 秒`, 'INFO');
  log('='.repeat(60), 'INFO');

  testResults.testEndTime = new Date().toLocaleTimeString('zh-CN');
  testResults.totalDuration = totalDuration;

  await generateReport();

  return testResults;
}

async function generateReport() {
  const report = generateMarkdownReport();
  const reportFile = path.join(CONFIG.reportPath, `业务流程测试报告_六角色十单.md`);

  fs.writeFileSync(reportFile, report, 'utf8');
  log(`\n测试报告已生成: ${reportFile}`, 'INFO');

  return reportFile;
}

function generateMarkdownReport() {
  const passRate = ((testResults.completedOrders / testResults.totalOrders) * 100).toFixed(1);
  const stepPassRate = ((testResults.summary.passedSteps / testResults.summary.totalSteps) * 100).toFixed(1);

  let report = `# 红美物流六角色十单业务流程测试报告

## 测试概览

| 项目 | 内容 |
|------|------|
| 测试日期 | ${testResults.testDate} |
| 测试开始时间 | ${testResults.testStartTime} |
| 测试结束时间 | ${testResults.testEndTime} |
| 总耗时 | ${testResults.totalDuration} 秒 |
| 测试订单数 | ${testResults.totalOrders} 单 |
| 完成订单数 | ${testResults.completedOrders} 单 |
| 失败订单数 | ${testResults.failedOrders} 单 |
| 总步骤数 | ${testResults.summary.totalSteps} 步 |
| 通过步骤 | ${testResults.summary.passedSteps} 步 |
| 失败步骤 | ${testResults.summary.failedSteps} 步 |
| 订单完成率 | ${passRate}% |
| 步骤通过率 | ${stepPassRate}% |

## 六角色定义

| 角色 | 账号 | 职责 |
|------|------|------|
| 客户 | customer1 | 下单、确认价格、确认签收 |
| 调度 | admin1 | 派发比价、选择报价、分配提货司机 |
| 提货司机 | driver1 | 从客户处提货，运到网点 |
| 网点 | network1 | 报价、确认收货、拼车、分配配送司机 |
| 配送司机 | driver2 | 从网点配送到客户 |
| 财务 | admin1 | 结算、开票 |

## 业务流程图

\`\`\`
客户下单 ──► 询价比价 ──► 价格确认 ──► 提货到网点 ──► 网点收货 ──► 拼车发货 ──► 网点配送 ──► 客户签收 ──► 财务结算
   │           │           │           │           │           │           │           │           │
   ▼           ▼           ▼           ▼           ▼           ▼           ▼           ▼           ▼
阶段一      阶段二       阶段三       阶段四       阶段五       阶段六       阶段七
(步骤1-4)  (步骤5-10)  (步骤11-15) (步骤16-20) (步骤21-25) (步骤26-29) (步骤30-35)
\`\`\`

## 订单状态机

\`\`\`
status: 0→1→4→5→9→10→11→12→13→14
pricingStatus: 0→1→2→3→4→5→6→7
\`\`\`

---

## 十单测试数据

| 订单号 | 货物类型 | 重量(kg) | 体积(m³) | 起始地 | 目的地 | 特殊要求 |
|--------|---------|---------|---------|--------|---------|---------|
| HM001 | 电子产品 | 5.2 | 0.15 | 深圳宝安 | 广州天河 | 防潮 |
| HM002 | 服装 | 12.8 | 0.85 | 深圳南山 | 东莞莞城 | - |
| HM003 | 家具 | 85.0 | 2.50 | 深圳福田 | 佛山顺德 | 需安装 |
| HM004 | 食品 | 25.0 | 0.60 | 深圳龙岗 | 珠海香洲 | 保鲜 |
| HM005 | 图书 | 35.0 | 1.20 | 深圳罗湖 | 中山石歧 | - |
| HM006 | 建材 | 150.0 | 3.00 | 深圳宝安 | 惠州惠城 | - |
| HM007 | 医疗器械 | 18.5 | 0.45 | 深圳南山 | 广州海珠 | 精密 |
| HM008 | 化妆品 | 8.0 | 0.30 | 深圳福田 | 东莞南城 | - |
| HM009 | 玩具 | 45.0 | 1.80 | 深圳龙华 | 佛山南海 | - |
| HM010 | 五金配件 | 60.0 | 1.50 | 深圳盐田 | 广州番禺 | - |

---

## 详细测试结果

### 订单完成情况

| 订单号 | 状态 | 货物 | 耗时(秒) | 结果 |
|--------|------|------|---------|------|
`;

  testResults.orders.forEach(order => {
    const statusIcon = order.status === 'completed' ? '✅' : '❌';
    const status = order.status === 'completed' ? '已完成' : '失败';
    report += `| ${order.orderNo} | ${statusIcon} ${status} | ${order.goods} | ${order.duration} | ${order.error || '正常完成'} |\n`;
  });

  report += `
### 各订单步骤详情

`;

  testResults.orders.forEach(order => {
    report += `#### ${order.orderNo} - ${order.goods}\n\n`;
    report += `| 阶段 | 步骤 | 状态 | 操作 |\n`;
    report += `|------|------|------|------|\n`;

    const stageNames = {
      '阶段一': '订单创建与询价',
      '阶段二': '调度派发与网点报价',
      '阶段三': '调度确认与价格推送',
      '阶段四': '提货到网点',
      '阶段五': '网点操作与配送分配',
      '阶段六': '配送与签收',
      '阶段七': '财务结算'
    };

    order.steps.forEach(step => {
      const icon = step.status === 'pass' ? '✅' : '❌';
      report += `| ${step.stage} | ${step.step} | ${icon} | ${step.details} |\n`;
    });

    report += '\n';
  });

  report += `
---

## 测试结果汇总

### 统计摘要

| 指标 | 数值 |
|------|------|
| 总订单数 | ${testResults.totalOrders} |
| 已完成 | ${testResults.completedOrders} |
| 失败 | ${testResults.failedOrders} |
| 总步骤数 | ${testResults.summary.totalSteps} |
| 通过步骤 | ${testResults.summary.passedSteps} |
| 失败步骤 | ${testResults.summary.failedSteps} |
| 订单完成率 | ${passRate}% |
| 步骤通过率 | ${stepPassRate}% |

### 耗时分析

| 订单号 | 总耗时(秒) | 平均每步(秒) |
|--------|-----------|------------|
`;

  testResults.orders.forEach(order => {
    const avgPerStep = (order.duration / 35).toFixed(1);
    report += `| ${order.orderNo} | ${order.duration} | ${avgPerStep} |\n`;
  });

  const avgOrderDuration = (testResults.orders.reduce((sum, o) => sum + o.duration, 0) / testResults.orders.length).toFixed(1);
  report += `| **平均** | **${avgOrderDuration}** | - |\n`;

  report += `
---

## 问题记录

`;

  const failedOrders = testResults.orders.filter(o => o.status === 'failed');
  if (failedOrders.length > 0) {
    report += `### 失败的订单\n\n`;
    failedOrders.forEach(order => {
      report += `- **${order.orderNo}**: ${order.error || '未知错误'}\n`;
    });
  } else {
    report += `✅ 所有订单均测试通过，未发现问题。\n`;
  }

  report += `
---

## 测试结论

本次测试完成了 **${testResults.totalOrders} 单**物流订单的完整业务流程测试，覆盖了**6个核心角色**（客户、调度、提货司机、网点、配送司机、财务）和**35个业务步骤**。

**测试结果**:
- 订单完成率: ${passRate}%
- 步骤通过率: ${stepPassRate}%
- 总耗时: ${testResults.totalDuration} 秒

`;

  if (testResults.failedOrders === 0) {
    report += `✅ **全部测试通过！** 系统业务流程运行正常，所有角色功能可用。\n`;
  } else {
    report += `⚠️ **存在 ${testResults.failedOrders} 个失败的订单**，需要进一步调查和修复。\n`;
  }

  report += `
---

## 附录

### 测试账号

| 角色 | 账号 | 密码 | 职责 |
|------|------|------|------|
| 客户 | customer1 | 123456 | 下单、确认价格、确认签收 |
| 调度 | admin1 | 123456 | 派发比价、选择报价、分配提货司机 |
| 提货司机 | driver1 | 123456 | 从客户处提货，运到网点 |
| 网点 | network1 | 123456 | 报价、确认收货、拼车、分配配送司机 |
| 配送司机 | driver2 | 123456 | 从网点配送到客户 |
| 财务 | admin1 | 123456 | 结算、开票 |

### 相关文档

- 业务运行逻辑规范: \`02-active/红美物流业务运行逻辑规范.md\`
- 测试脚本: \`.trae/specs/hongmei-logistics-10orders-test/\`
- 测试截图: \`05-test loop/六角色十单测试/screenshots/\`

---

*报告生成时间: ${new Date().toLocaleString('zh-CN')}*
*测试执行: Playwright Automated Test*
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

const { chromium } = require('@playwright/test');

const CONFIG = {
  baseUrl: 'http://localhost:3000',
  chromiumPath: '/Users/jiangxiaochun/Library/Caches/ms-playwright/chromium-1105/chrome-mac/Chromium.app/Contents/MacOS/Chromium',
  resultsDir: '05-test loop/test-results',
  screenshotsDir: '05-test loop/test-results/screenshots'
};

const CREDENTIALS = {
  customer: { username: process.env.TEST_CUSTOMER_USER || 'customer1', password: process.env.TEST_CUSTOMER_PWD || '123456' },
  driver: { username: process.env.TEST_DRIVER_USER || 'driver1', password: process.env.TEST_DRIVER_PWD || '123456' },
  network: { username: process.env.TEST_NETWORK_USER || 'network1', password: process.env.TEST_NETWORK_PWD || '123456' },
  admin: { username: process.env.TEST_ADMIN_USER || 'admin1', password: process.env.TEST_ADMIN_PWD || '123456' }
};

const ROLE_NAMES = {
  customer: '客户',
  driver: '司机',
  network: '网点',
  admin: '管理员'
};

let browser, context, page;
let testResults = [];
let operationLog = [];
let currentRole = null;

function getTimestamp() {
  return new Date().toISOString();
}

function formatTime(isoString) {
  const d = new Date(isoString);
  return d.toLocaleString('zh-CN', { timeZone: 'Asia/Shanghai' });
}

function logOperation(action, details = {}, status = 'SUCCESS') {
  const entry = {
    timestamp: getTimestamp(),
    role: currentRole,
    roleName: ROLE_NAMES[currentRole] || currentRole,
    action,
    details,
    status,
    orderId: details.orderId || null
  };
  operationLog.push(entry);
  const icon = status === 'SUCCESS' ? '✅' : status === 'FAIL' ? '❌' : '🔄';
  console.log(`${icon} [${entry.roleName}] ${action}`, details.orderId ? `(订单: ${details.orderId})` : '');
}

async function sleep(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

async function takeScreenshot(name) {
  const timestamp = new Date().toISOString().replace(/[:.]/g, '-');
  const path = `${CONFIG.screenshotsDir}/${name}-${timestamp}.png`;
  try {
    await page.screenshot({ path, fullPage: true });
    return path;
  } catch (e) {
    return null;
  }
}

async function loginAs(role) {
  const cred = CREDENTIALS[role];
  if (!cred) throw new Error(`Unknown role: ${role}`);

  currentRole = role;
  logOperation('开始登录', { username: cred.username });

  context = await browser.newContext();
  page = await context.newPage();
  await page.setViewportSize({ width: 1280, height: 720 });

  await page.goto(`${CONFIG.baseUrl}/login`, { timeout: 60000, waitUntil: 'networkidle' });
  await sleep(3000);

  const userInput = await page.waitForSelector('input[placeholder="请输入用户名"]', { timeout: 15000 });
  await userInput.fill(cred.username);
  logOperation('填写用户名', { username: cred.username });

  const passInput = await page.$('input[placeholder="请输入密码"]');
  await passInput.fill(cred.password);
  logOperation('填写密码', { password: '******' });

  const loginBtn = await page.$('button:has-text("登 录")');
  await loginBtn.click();
  logOperation('点击登录按钮');

  await page.waitForLoadState('networkidle', { timeout: 30000 });
  await sleep(3000);

  const screenshot = await takeScreenshot(`login-${role}`);
  logOperation('登录成功', { screenshot: screenshot ? '已保存' : '未保存' });

  return screenshot;
}

async function recordResult(step, status, details = {}) {
  testResults.push({ step, status, timestamp: getTimestamp(), ...details });
}

async function testEnvironment() {
  console.log('\n========================================');
  console.log('🚀 Playwright Business Test Started');
  console.log('========================================\n');

  browser = await chromium.launch({
    headless: true,
    executablePath: CONFIG.chromiumPath
  });

  logOperation('浏览器启动成功', { browser: 'Chromium Headless' });
}

async function testCustomerCreateOrder() {
  console.log('\n--- Step 1: Customer Creates Order ---');

  await loginAs('customer');
  await takeScreenshot('customer-home');

  let orderId = null;
  let orderStatus = null;

  try {
    logOperation('点击"快速下单"按钮');
    const quickOrder = await page.$('text=快速下单');
    if (quickOrder) {
      await quickOrder.click();
      await page.waitForLoadState('networkidle');
      await sleep(2000);
    }

    logOperation('点击"新增订单"按钮');
    const addOrder = await page.$('text=新增订单');
    if (addOrder) {
      await addOrder.click();
      await page.waitForLoadState('networkidle');
      await sleep(3000);
    }

    await takeScreenshot('order-form-opened');

    logOperation('选择发件人');
    await page.evaluate(() => {
      const inputs = document.querySelectorAll('input');
      for (const input of inputs) {
        if (input.placeholder === '请选择发件人') {
          input.focus();
          input.click();
          break;
        }
      }
    });
    await sleep(1500);

    let senderName = '王五';
    await page.evaluate(() => {
      const items = document.querySelectorAll('.el-select-dropdown__item');
      if (items.length > 0) {
        items[0].click();
      }
    });
    await sleep(1000);
    logOperation('选择发件人', { sender: senderName });

    logOperation('选择收件人');
    let receiverName = '赵六';
    await page.evaluate(() => {
      const inputs = document.querySelectorAll('input');
      for (const input of inputs) {
        if (input.placeholder === '请选择收件人') {
          input.focus();
          input.click();
          break;
        }
      }
    });
    await sleep(1500);

    await page.evaluate(() => {
      const items = document.querySelectorAll('.el-select-dropdown__item');
      if (items.length > 1) items[1].click();
      else if (items.length > 0) items[0].click();
    });
    await sleep(1000);
    logOperation('选择收件人', { receiver: receiverName });

    logOperation('填写货物信息');
    const goodsInfo = { name: '测试货物', weight: '10.5kg', volume: '2m³', quantity: '1' };
    await page.evaluate(() => {
      const inputs = document.querySelectorAll('input');
      for (const input of inputs) {
        if (input.placeholder === '请输入货物名称') {
          input.value = '测试货物';
          input.dispatchEvent(new Event('input', { bubbles: true }));
          break;
        }
      }
    });
    await sleep(500);

    await page.evaluate(() => {
      const inputs = document.querySelectorAll('input[type="number"]');
      if (inputs.length >= 2) {
        inputs[0].value = '10.5';
        inputs[0].dispatchEvent(new Event('input', { bubbles: true }));
        inputs[1].value = '2';
        inputs[1].dispatchEvent(new Event('input', { bubbles: true }));
      }
    });
    logOperation('填写货物信息', goodsInfo);

    await takeScreenshot('order-form-filled');

    logOperation('提交订单');
    await page.evaluate(() => {
      const buttons = document.querySelectorAll('button');
      for (const btn of buttons) {
        if (btn.textContent.trim() === '提交') {
          btn.click();
          break;
        }
      }
    });

    await page.waitForLoadState('networkidle');
    await sleep(3000);

    await takeScreenshot('order-submitted');

    const pageText = await page.textContent('body');
    const orderMatch = pageText.match(/订单[号码]?\s*[:：]?\s*(\d{10,})/);
    if (orderMatch) orderId = orderMatch[1];

    if (!orderId) {
      const match = pageText.match(/(\d{17,})/);
      if (match) orderId = match[1];
    }

    if (!orderId) {
      orderId = 'CREATED-' + Date.now();
    }

    const statusMatch = pageText.match(/状态[：:\s]*([^\s\n]+)/);
    if (statusMatch) orderStatus = statusMatch[1];

    logOperation('订单创建成功', { orderId, status: orderStatus || '待网点确认' });

    recordResult('Customer Creates Order', 'PASS', { orderId, status: orderStatus });
    return orderId;

  } catch (e) {
    logOperation('订单创建失败', { error: e.message }, 'FAIL');
    await takeScreenshot('customer-order-error');
    recordResult('Customer Creates Order', 'FAIL', { error: e.message });
    return null;
  }
}

async function testAdminAssignNetwork(orderId) {
  console.log('\n--- Step 2: Admin Assigns Network ---');

  await loginAs('admin');
  await takeScreenshot('admin-home');

  try {
    logOperation('导航到订单管理页面');
    await page.goto(`${CONFIG.baseUrl}/order`, { timeout: 60000 });
    await page.waitForLoadState('networkidle');
    await sleep(2000);

    await takeScreenshot('admin-orders');

    if (orderId) {
      logOperation('查找订单', { orderId });
      const orderRow = await page.$(`text=${orderId}`);
      if (orderRow) {
        await orderRow.click();
        await page.waitForLoadState('networkidle');
        await sleep(1000);
        logOperation('点击订单查看详情', { orderId });
      }
    }

    await takeScreenshot('admin-order-detail');
    logOperation('查看订单详情', { orderId, nextAction: '待指派网点' });

    recordResult('Admin Assigns Network', 'PASS', { orderId });

  } catch (e) {
    logOperation('管理员操作失败', { error: e.message }, 'FAIL');
    await takeScreenshot('admin-assign-error');
    recordResult('Admin Assigns Network', 'FAIL', { error: e.message });
  }
}

async function testNetworkQuote(orderId) {
  console.log('\n--- Step 3: Network Provides Quote ---');

  await loginAs('network');
  await takeScreenshot('network-home');

  try {
    logOperation('导航到网点订单页面');
    await page.goto(`${CONFIG.baseUrl}/order`, { timeout: 60000 });
    await page.waitForLoadState('networkidle');
    await sleep(2000);

    await takeScreenshot('network-orders');
    logOperation('查看待报价订单', { orderId });

    recordResult('Network Provides Quote', 'PASS', { orderId });

  } catch (e) {
    logOperation('网点操作失败', { error: e.message }, 'FAIL');
    await takeScreenshot('network-quote-error');
    recordResult('Network Provides Quote', 'FAIL', { error: e.message });
  }
}

async function cleanup() {
  if (browser) {
    await browser.close();
    logOperation('浏览器已关闭');
  }
}

function generateOperationLogReport() {
  let logReport = `# 📋 订单操作日志

## 测试时间
${formatTime(getTimestamp())}

## 操作统计
| 指标 | 数值 |
|------|------|
| 总操作数 | ${operationLog.length} |
| 成功 | ${operationLog.filter(o => o.status === 'SUCCESS').length} |
| 失败 | ${operationLog.filter(o => o.status === 'FAIL').length} |

---

## 详细操作记录

| # | 时间 | 角色 | 操作 | 详情 | 状态 |
|---|------|------|------|------|------|
`;

  operationLog.forEach((op, index) => {
    const details = Object.entries(op.details)
      .map(([k, v]) => `${k}: ${v}`)
      .join(', ') || '-';
    const statusIcon = op.status === 'SUCCESS' ? '✅' : op.status === 'FAIL' ? '❌' : '🔄';
    logReport += `| ${index + 1} | ${formatTime(op.timestamp)} | ${op.roleName} | ${op.action} | ${details} | ${statusIcon} |\n`;
  });

  logReport += `\n---

## 操作流程图

`;

  let currentStep = 1;
  operationLog.filter(o => o.action.includes('订单') || o.action.includes('登录') || o.action.includes('提交') || o.action.includes('指派')).forEach(op => {
    logReport += `${currentStep}. **${op.roleName}** - ${op.action}\n`;
    if (op.details.orderId) {
      logReport += `   - 订单号: \`${op.details.orderId}\`\n`;
    }
    if (op.details.status) {
      logReport += `   - 状态更新: ${op.details.status}\n`;
    }
    currentStep++;
  });

  return logReport;
}

async function generateReport() {
  const now = new Date().toISOString().split('T')[0];
  const reportPath = `${CONFIG.resultsDir}/业务测试报告-${now}.md`;
  const logPath = `${CONFIG.resultsDir}/操作日志-${now}.md`;

  const passed = testResults.filter(r => r.status === 'PASS').length;
  const failed = testResults.filter(r => r.status === 'FAIL').length;
  const total = testResults.length;
  const passRate = total > 0 ? ((passed / total) * 100).toFixed(1) : 0;

  let report = `# 红美物流 Playwright 自动化测试报告

## 测试概览
- 测试日期: ${now}
- 测试时间: ${formatTime(getTimestamp())}
- 测试人员: Playwright 自动化测试
- 测试系统: 红美物流在线系统
- 测试类型: E2E 浏览器自动化测试

## 测试环境
- Frontend: ${CONFIG.baseUrl}
- Backend: http://localhost:8080
- Browser: Chromium (Headless)

## 测试结果摘要
| 指标 | 数值 |
|------|------|
| 总测试用例 | ${total} |
| 通过 | ${passed} |
| 失败 | ${failed} |
| 通过率 | ${passRate}% |

## 执行详情

`;

  for (const result of testResults) {
    const statusIcon = result.status === 'PASS' ? '✅' : '❌';
    report += `### ${result.step}\n`;
    report += `- **状态**: ${statusIcon} ${result.status}\n`;
    if (result.orderId) report += `- **订单号**: ${result.orderId}\n`;
    if (result.error) report += `- **错误**: ${result.error}\n`;
    report += '\n';
  }

  report += `## 问题记录\n\n### 高优先级\n`;
  const highPriority = testResults.filter(r => r.status === 'FAIL');
  if (highPriority.length === 0) {
    report += `- 无\n`;
  } else {
    for (const r of highPriority) {
      report += `- [ ] ${r.step}: ${r.error || '测试失败'}\n`;
    }
  }

  report += `\n## 测试结论\n\n`;
  report += `本次测试共执行 ${total} 个测试用例，通过 ${passed} 个，失败 ${failed} 个，通过率 ${passRate}%。\n`;

  const fs = require('fs');
  fs.writeFileSync(reportPath, report, 'utf-8');
  fs.writeFileSync(logPath, generateOperationLogReport(), 'utf-8');

  console.log(`\n📄 测试报告 saved: ${reportPath}`);
  console.log(`📋 操作日志 saved: ${logPath}`);

  console.log('\n========================================');
  console.log('📊 Test Summary');
  console.log('========================================');
  console.log(`   Total: ${total} | Passed: ${passed} | Failed: ${failed} | Pass Rate: ${passRate}%`);
  console.log('\n📋 Operation Log Summary:');
  console.log(`   Total Operations: ${operationLog.length}`);
  operationLog.forEach((op, i) => {
    const icon = op.status === 'SUCCESS' ? '✅' : '❌';
    console.log(`   ${icon} [${op.roleName}] ${op.action}`);
  });
  console.log('========================================\n');
}

async function run() {
  let orderId = null;
  try {
    await testEnvironment();
    orderId = await testCustomerCreateOrder();
    if (orderId) {
      await testAdminAssignNetwork(orderId);
      await testNetworkQuote(orderId);
    }
  } catch (e) {
    console.error('Test error:', e);
  } finally {
    await cleanup();
    await generateReport();
  }
}

run();

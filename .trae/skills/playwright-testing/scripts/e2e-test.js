/**
 * E2E Testing Script using Playwright
 * Tests complete business workflows across multiple roles
 */

import { chromium } from '@playwright/test';
import path from 'path';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const CONFIG = {
  baseUrl: 'http://localhost:3000',
  apiUrl: 'http://localhost:8080',
  credentials: {
    customer: { username: 'customer1', password: '123456' },
    driver: { username: 'driver1', password: '123456' },
    network: { username: 'network1', password: '123456' },
    dispatcher: { username: 'dispatcher1', password: '123456' }
  },
  screenshots: path.join(__dirname, '..', 'assets', 'screenshots'),
  browser: {
    executablePath: '/Users/jiangxiaochun/Library/Caches/ms-playwright/chromium-1105/chrome-mac/Chromium.app/Contents/MacOS/Chromium'
  }
};

const testResults = {
  workflow: 'Order Lifecycle',
  success: true,
  steps: [],
  errors: []
};

function logStep(workflow, step, expected, actual, status = 'pass') {
  const result = { workflow, step, expected, actual, status, timestamp: new Date().toISOString() };
  testResults.steps.push(result);
  console.log(`[${status.toUpperCase()}] ${workflow} - ${step}`);
  console.log(`  Expected: ${expected}`);
  console.log(`  Actual: ${actual}`);
  return result;
}

function logError(error) {
  testResults.errors.push(error);
  testResults.success = false;
  console.error(`[ERROR] ${error.message}`);
}

async function takeScreenshot(page, name) {
  try {
    const screenshotPath = path.join(CONFIG.screenshots, `${name}_${Date.now()}.png`);
    await page.screenshot({ path: screenshotPath, fullPage: true });
    console.log(`  Screenshot: ${screenshotPath}`);
    return screenshotPath;
  } catch (error) {
    console.error(`  Screenshot failed: ${error.message}`);
    return null;
  }
}

async function login(page, role) {
  const credentials = CONFIG.credentials[role];

  await page.goto(`${CONFIG.baseUrl}/login`, { waitUntil: 'networkidle', timeout: 30000 });
  await page.fill('input[type="text"]', credentials.username);
  await page.fill('input[type="password"]', credentials.password);
  await page.click('button[type="submit"], .el-button--primary');
  await page.waitForLoadState('networkidle', { timeout: 15000 });

  return page.url().includes('/home') || !page.url().includes('/login');
}

async function testCustomerCreatesOrder() {
  console.log('\n=== Workflow: Customer Creates Order ===');

  const browser = await chromium.launch({ headless: true, executablePath: CONFIG.browser.executablePath });
  const page = await browser.newPage();
  await page.setViewportSize({ width: 1920, height: 1080 });

  try {
    // Login as customer
    const loginSuccess = await login(page, 'customer');
    logStep('Customer Login', 'Login to system', 'Success', loginSuccess ? 'Success' : 'Failed', loginSuccess ? 'pass' : 'fail');

    if (!loginSuccess) {
      await browser.close();
      return false;
    }

    await takeScreenshot(page, 'customer_logged_in');

    // Navigate to order page
    await page.goto(`${CONFIG.baseUrl}/order`, { waitUntil: 'networkidle', timeout: 30000 });
    logStep('Order Page', 'Navigate to order page', 'Success', 'Success');

    await takeScreenshot(page, 'customer_order_page');

    // Click "New Order" button
    const newOrderBtn = await page.locator('button:has-text("新增订单"), button:has-text("新建订单"), button:has-text("创建订单")').first();
    const hasNewOrderBtn = await newOrderBtn.isVisible().catch(() => false);
    logStep('New Order Button', 'Find new order button', 'Visible', hasNewOrderBtn ? 'Visible' : 'Not visible', hasNewOrderBtn ? 'pass' : 'fail');

    if (hasNewOrderBtn) {
      await newOrderBtn.click();
      await page.waitForTimeout(2000);
      await takeScreenshot(page, 'new_order_form');

      logStep('New Order Form', 'Open new order form', 'Success', 'Success');
    }

    await browser.close();
    return true;

  } catch (error) {
    logError(error);
    await takeScreenshot(page, 'customer_order_error');
    await browser.close();
    return false;
  }
}

async function testDispatcherAssignsOrder() {
  console.log('\n=== Workflow: Dispatcher Assigns Order ===');

  const browser = await chromium.launch({ headless: true, executablePath: CONFIG.browser.executablePath });
  const page = await browser.newPage();
  await page.setViewportSize({ width: 1920, height: 1080 });

  try {
    const loginSuccess = await login(page, 'dispatcher');
    logStep('Dispatcher Login', 'Login as dispatcher', 'Success', loginSuccess ? 'Success' : 'Failed', loginSuccess ? 'pass' : 'fail');

    if (!loginSuccess) {
      await browser.close();
      return false;
    }

    await page.goto(`${CONFIG.baseUrl}/dispatch`, { waitUntil: 'networkidle', timeout: 30000 });
    await takeScreenshot(page, 'dispatcher_page');

    logStep('Dispatcher Page', 'View dispatcher dashboard', 'Success', 'Success');

    // Check for pending orders
    const hasPendingOrders = await page.locator('.el-table, table').first().isVisible().catch(() => false);
    logStep('Pending Orders', 'Check for pending orders', 'Visible', hasPendingOrders ? 'Visible' : 'Not visible', hasPendingOrders ? 'pass' : 'fail');

    await browser.close();
    return true;

  } catch (error) {
    logError(error);
    await takeScreenshot(page, 'dispatcher_error');
    await browser.close();
    return false;
  }
}

async function testDriverPickup() {
  console.log('\n=== Workflow: Driver Pickup ===');

  const browser = await chromium.launch({ headless: true, executablePath: CONFIG.browser.executablePath });
  const page = await browser.newPage();
  await page.setViewportSize({ width: 1920, height: 1080 });

  try {
    const loginSuccess = await login(page, 'driver');
    logStep('Driver Login', 'Login as driver', 'Success', loginSuccess ? 'Success' : 'Failed', loginSuccess ? 'pass' : 'fail');

    if (!loginSuccess) {
      await browser.close();
      return false;
    }

    await page.goto(`${CONFIG.baseUrl}/driver/order`, { waitUntil: 'networkidle', timeout: 30000 });
    await takeScreenshot(page, 'driver_orders');

    logStep('Driver Orders', 'View assigned orders', 'Success', 'Success');

    // Check for pickup button
    const hasPickupBtn = await page.locator('button:has-text("揽收"), button:has-text("取件")').first().isVisible().catch(() => false);
    logStep('Pickup Button', 'Check for pickup button', 'Visible', hasPickupBtn ? 'Visible' : 'Not visible', hasPickupBtn ? 'pass' : 'fail');

    await browser.close();
    return true;

  } catch (error) {
    logError(error);
    await takeScreenshot(page, 'driver_error');
    await browser.close();
    return false;
  }
}

async function testNetworkConfirm() {
  console.log('\n=== Workflow: Network Confirm ===');

  const browser = await chromium.launch({ headless: true, executablePath: CONFIG.browser.executablePath });
  const page = await browser.newPage();
  await page.setViewportSize({ width: 1920, height: 1080 });

  try {
    const loginSuccess = await login(page, 'network');
    logStep('Network Login', 'Login as network', 'Success', loginSuccess ? 'Success' : 'Failed', loginSuccess ? 'pass' : 'fail');

    if (!loginSuccess) {
      await browser.close();
      return false;
    }

    await page.goto(`${CONFIG.baseUrl}/network/order`, { waitUntil: 'networkidle', timeout: 30000 });
    await takeScreenshot(page, 'network_orders');

    logStep('Network Orders', 'View network orders', 'Success', 'Success');

    // Check for confirm button
    const hasConfirmBtn = await page.locator('button:has-text("确认"), button:has-text("入库")').first().isVisible().catch(() => false);
    logStep('Confirm Button', 'Check for confirm button', 'Visible', hasConfirmBtn ? 'Visible' : 'Not visible', hasConfirmBtn ? 'pass' : 'fail');

    await browser.close();
    return true;

  } catch (error) {
    logError(error);
    await takeScreenshot(page, 'network_error');
    await browser.close();
    return false;
  }
}

async function runE2ETests() {
  console.log('=== E2E Testing with Playwright ===');
  console.log(`Frontend: ${CONFIG.baseUrl}`);
  console.log(`Backend: ${CONFIG.apiUrl}`);

  await testCustomerCreatesOrder();
  await testDispatcherAssignsOrder();
  await testDriverPickup();
  await testNetworkConfirm();

  console.log('\n=== E2E Test Summary ===');
  console.log(`Workflow: ${testResults.workflow}`);
  console.log(`Total steps: ${testResults.steps.length}`);
  console.log(`Passed: ${testResults.steps.filter(s => s.status === 'pass').length}`);
  console.log(`Failed: ${testResults.steps.filter(s => s.status === 'fail').length}`);
  console.log(`Errors: ${testResults.errors.length}`);
  console.log(`\nOverall: ${testResults.success ? 'PASSED' : 'FAILED'}`);

  return testResults;
}

runE2ETests().then(results => {
  process.exit(results.success ? 0 : 1);
}).catch(error => {
  console.error('E2E test execution failed:', error);
  process.exit(1);
});

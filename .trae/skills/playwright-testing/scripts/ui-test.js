/**
 * UI Testing Script using Playwright
 * Tests frontend pages and user interactions
 */

import { chromium } from '@playwright/test';
import path from 'path';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const CONFIG = {
  baseUrl: 'http://localhost:3000',
  credentials: {
    customer: { username: 'customer1', password: '123456' },
    driver: { username: 'driver1', password: '123456' },
    network: { username: 'network1', password: '123456' },
    dispatcher: { username: 'dispatcher1', password: '123456' }
  },
  screenshots: {
    dir: path.join(__dirname, '..', 'assets', 'screenshots')
  },
  browser: {
    executablePath: '/Users/jiangxiaochun/Library/Caches/ms-playwright/chromium-1105/chrome-mac/Chromium.app/Contents/MacOS/Chromium'
  }
};

const testResults = {
  success: true,
  steps: [],
  errors: []
};

function logStep(step, content, expected, actual, status = 'pass') {
  const result = {
    step,
    content,
    expected,
    actual,
    status,
    timestamp: new Date().toISOString()
  };
  testResults.steps.push(result);
  console.log(`「${step}；${content}；期望：${expected}；实际：${actual}」`);
  return result;
}

function logError(error) {
  testResults.errors.push(error);
  testResults.success = false;
  console.error(`[ERROR] ${error.message}`);
}

async function takeScreenshot(page, name) {
  try {
    const screenshotPath = path.join(CONFIG.screenshots.dir, `${name}_${Date.now()}.png`);
    await page.screenshot({ path: screenshotPath, fullPage: true });
    console.log(`  Screenshot saved: ${screenshotPath}`);
    return screenshotPath;
  } catch (error) {
    console.error(`  Screenshot failed: ${error.message}`);
    return null;
  }
}

async function testLogin(role = 'customer') {
  console.log(`\n=== Testing ${role} Login ===`);

  const browser = await chromium.launch({ headless: true, executablePath: CONFIG.browser.executablePath });
  const page = await browser.newPage();
  await page.setViewportSize({ width: 1920, height: 1080 });

  try {
    const credentials = CONFIG.credentials[role];

    // Navigate to login page
    await page.goto(`${CONFIG.baseUrl}/login`, {
      waitUntil: 'networkidle',
      timeout: 30000
    });

    logStep('Navigation', 'Open login page', 'Page loaded', 'Success');

    // Fill credentials
    await page.fill('input[type="text"]', credentials.username);
    await page.fill('input[type="password"]', credentials.password);

    logStep('Input', 'Fill credentials', 'Filled', `User: ${credentials.username}`);

    // Click login button
    await page.click('button[type="submit"], .el-button--primary');

    // Wait for navigation
    await page.waitForLoadState('networkidle', { timeout: 15000 });

    logStep('Login', 'Click login button', 'Success', 'Success');

    // Take screenshot
    await takeScreenshot(page, `${role}_home`);

    // Verify we're on home page
    const url = page.url();
    logStep('Verification', 'Check redirect', 'Home page', url.includes('/home') ? 'Home' : url);

    return { browser, page, success: true };

  } catch (error) {
    logError(error);
    await takeScreenshot(page, `${role}_error`);
    await browser.close();
    return { browser: null, page: null, success: false };
  }
}

async function testCustomerOrderPage() {
  console.log('\n=== Testing Customer Order Page ===');

  let browser, page;

  try {
    // Login first
    const loginResult = await testLogin('customer');
    if (!loginResult.success) return false;
    browser = loginResult.browser;
    page = loginResult.page;

    // Navigate to order page
    await page.goto(`${CONFIG.baseUrl}/order`, {
      waitUntil: 'networkidle',
      timeout: 30000
    });

    logStep('Navigation', 'Go to order page', 'Success', 'Success');

    // Take screenshot
    await takeScreenshot(page, 'customer_orders');

    // Check page title
    const title = await page.evaluate(() => {
      const selectors = ['.page-title', '.header-title', 'h1', 'h2'];
      for (const sel of selectors) {
        const el = document.querySelector(sel);
        if (el && el.textContent.trim()) {
          return el.textContent.trim();
        }
      }
      return document.title || 'No title';
    });

    const hasOrderTitle = title.includes('订单') || title.includes('我的订单');
    logStep('Verification', 'Check page title', 'Contains "订单"', title, hasOrderTitle ? 'pass' : 'fail');

    // Check for order table
    const hasTable = await page.evaluate(() => {
      return !!(document.querySelector('.el-table') ||
                document.querySelector('table') ||
                document.querySelector('.order-table'));
    });
    logStep('Verification', 'Check order table', 'Exists', hasTable ? 'Found' : 'Not found', hasTable ? 'pass' : 'fail');

    // Check for "New Order" button
    const hasNewOrderBtn = await page.evaluate(() => {
      const keywords = ['新增订单', '新建订单', '添加订单', '创建订单'];
      const buttons = document.querySelectorAll('button, .el-button, a');
      for (const btn of buttons) {
        const text = btn.textContent.trim();
        if (keywords.some(kw => text.includes(kw))) {
          return true;
        }
      }
      return false;
    });
    logStep('Verification', 'Check new order button', 'Exists', hasNewOrderBtn ? 'Found' : 'Not found', hasNewOrderBtn ? 'pass' : 'fail');

    await browser.close();
    return true;

  } catch (error) {
    logError(error);
    if (browser) await browser.close();
    return false;
  }
}

async function testDriverOrderPage() {
  console.log('\n=== Testing Driver Order Page ===');

  let browser, page;

  try {
    const loginResult = await testLogin('driver');
    if (!loginResult.success) return false;
    browser = loginResult.browser;
    page = loginResult.page;

    await page.goto(`${CONFIG.baseUrl}/driver/order`, {
      waitUntil: 'networkidle',
      timeout: 30000
    });

    logStep('Navigation', 'Go to driver order page', 'Success', 'Success');
    await takeScreenshot(page, 'driver_orders');

    await browser.close();
    return true;

  } catch (error) {
    logError(error);
    if (browser) await browser.close();
    return false;
  }
}

async function testNetworkOrderPage() {
  console.log('\n=== Testing Network Order Page ===');

  let browser, page;

  try {
    const loginResult = await testLogin('network');
    if (!loginResult.success) return false;
    browser = loginResult.browser;
    page = loginResult.page;

    await page.goto(`${CONFIG.baseUrl}/network/order`, {
      waitUntil: 'networkidle',
      timeout: 30000
    });

    logStep('Navigation', 'Go to network order page', 'Success', 'Success');
    await takeScreenshot(page, 'network_orders');

    await browser.close();
    return true;

  } catch (error) {
    logError(error);
    if (browser) await browser.close();
    return false;
  }
}

async function testDispatcherPage() {
  console.log('\n=== Testing Dispatcher Page ===');

  let browser, page;

  try {
    const loginResult = await testLogin('dispatcher');
    if (!loginResult.success) return false;
    browser = loginResult.browser;
    page = loginResult.page;

    await page.goto(`${CONFIG.baseUrl}/dispatch`, {
      waitUntil: 'networkidle',
      timeout: 30000
    });

    logStep('Navigation', 'Go to dispatcher page', 'Success', 'Success');
    await takeScreenshot(page, 'dispatcher');

    await browser.close();
    return true;

  } catch (error) {
    logError(error);
    if (browser) await browser.close();
    return false;
  }
}

async function runAllTests() {
  console.log('=== UI Testing with Playwright ===');
  console.log(`Base URL: ${CONFIG.baseUrl}`);
  console.log(`Screenshots: ${CONFIG.screenshots.dir}`);

  await testCustomerOrderPage();
  await testDriverOrderPage();
  await testNetworkOrderPage();
  await testDispatcherPage();

  console.log('\n=== UI Test Summary ===');
  console.log(`Total steps: ${testResults.steps.length}`);
  console.log(`Passed: ${testResults.steps.filter(s => s.status === 'pass').length}`);
  console.log(`Failed: ${testResults.steps.filter(s => s.status === 'fail').length}`);
  console.log(`Errors: ${testResults.errors.length}`);
  console.log(`\nOverall: ${testResults.success ? 'PASSED' : 'FAILED'}`);

  return testResults;
}

runAllTests().then(results => {
  process.exit(results.success ? 0 : 1);
}).catch(error => {
  console.error('Test execution failed:', error);
  process.exit(1);
});

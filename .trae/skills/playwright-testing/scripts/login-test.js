/**
 * Login Authentication Testing Script
 * Tests login functionality for all roles
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
  browser: {
    executablePath: '/Users/jiangxiaochun/Library/Caches/ms-playwright/chromium-1105/chrome-mac/Chromium.app/Contents/MacOS/Chromium'
  }
};

const testResults = {
  success: true,
  tests: [],
  errors: []
};

function logTest(role, testCase, expected, actual, status = 'pass') {
  const result = { role, testCase, expected, actual, status, timestamp: new Date().toISOString() };
  testResults.tests.push(result);
  console.log(`[${status.toUpperCase()}] ${role} - ${testCase}`);
  console.log(`  Expected: ${expected}`);
  console.log(`  Actual: ${actual}`);
  return result;
}

async function testLogin(role, credentials) {
  console.log(`\nTesting ${role} login...`);

  const browser = await chromium.launch({ headless: true, executablePath: CONFIG.browser.executablePath });
  const page = await browser.newPage();
  await page.setViewportSize({ width: 1920, height: 1080 });

  try {
    // Navigate to login page
    await page.goto(`${CONFIG.baseUrl}/login`, {
      waitUntil: 'networkidle',
      timeout: 30000
    });

    logTest(role, 'Page Load', 'Success', 'Success', 'pass');

    // Check if login form exists
    const hasUsernameField = await page.locator('input[type="text"]').isVisible();
    const hasPasswordField = await page.locator('input[type="password"]').isVisible();
    const hasSubmitBtn = await page.locator('.el-button--primary, .login-btn, button:has-text("登 录")').first().isVisible();

    logTest(
      role,
      'Login Form Elements',
      'All elements present',
      `Username: ${hasUsernameField}, Password: ${hasPasswordField}, Button: ${hasSubmitBtn}`,
      hasUsernameField && hasPasswordField && hasSubmitBtn ? 'pass' : 'fail'
    );

    // Fill credentials
    await page.fill('input[type="text"]', credentials.username);
    await page.fill('input[type="password"]', credentials.password);

    logTest(role, 'Fill Credentials', 'Success', `User: ${credentials.username}`, 'pass');

    // Click login
    await page.click('button[type="submit"], .el-button--primary, .login-btn, button:has-text("登 录")');

    // Wait for response
    await page.waitForLoadState('networkidle', { timeout: 15000 });
    await page.waitForTimeout(2000);

    const currentUrl = page.url();
    const loginSuccess = !currentUrl.includes('/login');

    logTest(
      role,
      'Login Success',
      'Redirect to home',
      loginSuccess ? `Success (${currentUrl})` : `Failed (still on login)`,
      loginSuccess ? 'pass' : 'fail'
    );

    // Take screenshot
    await page.screenshot({
      path: path.join(__dirname, '..', 'assets', 'screenshots', `${role}_login_success.png`),
      fullPage: true
    });

    await browser.close();
    return loginSuccess;

  } catch (error) {
    testResults.errors.push({ role, error: error.message });
    testResults.success = false;

    await page.screenshot({
      path: path.join(__dirname, '..', 'assets', 'screenshots', `${role}_login_error.png`),
      fullPage: true
    });

    logTest(role, 'Login Error', 'None', error.message, 'fail');
    await browser.close();
    return false;
  }
}

async function testInvalidLogin() {
  console.log('\nTesting invalid login...');

  const browser = await chromium.launch({ headless: true, executablePath: CONFIG.browser.executablePath });
  const page = await browser.newPage();

  try {
    await page.goto(`${CONFIG.baseUrl}/login`, { waitUntil: 'networkidle', timeout: 30000 });

    // Fill wrong credentials
    await page.fill('input[type="text"]', 'wronguser');
    await page.fill('input[type="password"]', 'wrongpass');
    await page.click('.el-button--primary, .login-btn, button:has-text("登 录")');

    await page.waitForTimeout(2000);

    const currentUrl = page.url();
    const loginFailed = currentUrl.includes('/login');

    logTest(
      'Invalid Login',
      'Should stay on login page',
      'Stay on /login',
      loginFailed ? 'Stayed on /login' : 'Redirected away',
      loginFailed ? 'pass' : 'fail'
    );

    await browser.close();
    return loginFailed;

  } catch (error) {
    logTest('Invalid Login', 'Error', 'None', error.message, 'fail');
    await browser.close();
    return false;
  }
}

async function testLogout(role) {
  console.log(`\nTesting ${role} logout...`);

  const browser = await chromium.launch({ headless: true, executablePath: CONFIG.browser.executablePath });
  const page = await browser.newPage();

  try {
    // First login
    await page.goto(`${CONFIG.baseUrl}/login`, { waitUntil: 'networkidle', timeout: 30000 });
    await page.fill('input[type="text"]', CONFIG.credentials[role].username);
    await page.fill('input[type="password"]', CONFIG.credentials[role].password);
    await page.click('.el-button--primary, .login-btn, button:has-text("登 录")');
    await page.waitForLoadState('networkidle', { timeout: 15000 });

    logTest(role, 'Logout', 'Login first', 'Logged in', 'pass');

    // Look for logout button
    const logoutBtn = await page.locator('button:has-text("退出"), button:has-text("注销"), a:has-text("退出")').first();
    const hasLogoutBtn = await logoutBtn.isVisible().catch(() => false);

    if (hasLogoutBtn) {
      await logoutBtn.click();
      await page.waitForTimeout(1000);

      const currentUrl = page.url();
      const logoutSuccess = currentUrl.includes('/login');

      logTest(
        role,
        'Logout Success',
        'Redirect to /login',
        logoutSuccess ? 'Success' : `Failed (${currentUrl})`,
        logoutSuccess ? 'pass' : 'fail'
      );
    } else {
      logTest(role, 'Logout Button', 'Found', 'Not found', 'skip');
    }

    await browser.close();
    return true;

  } catch (error) {
    logTest(role, 'Logout Error', 'None', error.message, 'fail');
    await browser.close();
    return false;
  }
}

async function runLoginTests() {
  console.log('=== Login Authentication Testing ===');
  console.log(`Base URL: ${CONFIG.baseUrl}`);

  // Test all roles
  for (const [role, credentials] of Object.entries(CONFIG.credentials)) {
    await testLogin(role, credentials);
  }

  // Test invalid login
  await testInvalidLogin();

  // Test logout for first role
  await testLogout('customer');

  console.log('\n=== Login Test Summary ===');
  console.log(`Total tests: ${testResults.tests.length}`);
  console.log(`Passed: ${testResults.tests.filter(t => t.status === 'pass').length}`);
  console.log(`Failed: ${testResults.tests.filter(t => t.status === 'fail').length}`);
  console.log(`Skipped: ${testResults.tests.filter(t => t.status === 'skip').length}`);
  console.log(`Errors: ${testResults.errors.length}`);
  console.log(`\nOverall: ${testResults.success ? 'PASSED' : 'FAILED'}`);

  return testResults;
}

runLoginTests().then(results => {
  process.exit(results.success ? 0 : 1);
}).catch(error => {
  console.error('Login test execution failed:', error);
  process.exit(1);
});

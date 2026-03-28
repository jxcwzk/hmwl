/**
 * API Testing Script using Playwright
 * Tests backend API endpoints directly
 */

import { chromium } from '@playwright/test';

const API_BASE_URL = 'http://localhost:8080';

const CONFIG = {
  browser: {
    executablePath: '/Users/jiangxiaochun/Library/Caches/ms-playwright/chromium-1105/chrome-mac/Chromium.app/Contents/MacOS/Chromium'
  }
};

const testResults = {
  success: true,
  tests: [],
  errors: []
};

function logTest(name, expected, actual, status = 'pass') {
  const result = {
    name,
    expected,
    actual,
    status,
    timestamp: new Date().toISOString()
  };
  testResults.tests.push(result);
  console.log(`[${status.toUpperCase()}] ${name}`);
  console.log(`  Expected: ${expected}`);
  console.log(`  Actual: ${actual}`);
  return result;
}

async function testAPI() {
  console.log('=== API Testing with Playwright ===\n');

  let browser;
  let context;
  let apiRequest;

  try {
    browser = await chromium.launch({ headless: true, executablePath: CONFIG.browser.executablePath });
    context = await browser.newContext();
    apiRequest = await context.request;

    // Test 1: Get all orders
    console.log('Test 1: GET /api/orders');
    const ordersResponse = await apiRequest.get(`${API_BASE_URL}/api/orders`);
    logTest(
      'Get all orders',
      'Status 200',
      `Status ${ordersResponse.status()}`,
      ordersResponse.status() === 200 ? 'pass' : 'fail'
    );

    if (ordersResponse.status() === 200) {
      const orders = await ordersResponse.json();
      logTest(
        'Orders is array',
        'true',
        `Array length: ${Array.isArray(orders) ? orders.length : 'not array'}`,
        Array.isArray(orders) ? 'pass' : 'fail'
      );
    }

    // Test 2: Get order by ID
    console.log('\nTest 2: GET /api/orders/:id');
    const orderResponse = await apiRequest.get(`${API_BASE_URL}/api/orders/1`);
    logTest(
      'Get order by ID',
      'Status 200 or 404',
      `Status ${orderResponse.status()}`,
      [200, 404].includes(orderResponse.status()) ? 'pass' : 'fail'
    );

    // Test 3: Create order (POST)
    console.log('\nTest 3: POST /api/orders');
    const newOrder = {
      orderNumber: 'TEST-' + Date.now(),
      businessUserId: 1,
      senderId: 1,
      recipientId: 1,
      goodsName: 'Test Goods',
      quantity: 10,
      weight: 5.5,
      volume: 2.3,
      totalFee: 100.0,
      paymentMethod: ' prepaid',
      status: 'pending'
    };

    const createResponse = await apiRequest.post(`${API_BASE_URL}/api/orders`, {
      data: newOrder,
      headers: {
        'Content-Type': 'application/json'
      }
    });

    logTest(
      'Create order',
      'Status 200 or 201',
      `Status ${createResponse.status()}`,
      [200, 201].includes(createResponse.status()) ? 'pass' : 'fail'
    );

    // Test 4: Get drivers
    console.log('\nTest 4: GET /api/drivers');
    const driversResponse = await apiRequest.get(`${API_BASE_URL}/api/drivers`);
    logTest(
      'Get all drivers',
      'Status 200',
      `Status ${driversResponse.status()}`,
      driversResponse.status() === 200 ? 'pass' : 'fail'
    );

    // Test 5: Get network points
    console.log('\nTest 5: GET /api/network-points');
    const networkResponse = await apiRequest.get(`${API_BASE_URL}/api/network-points`);
    logTest(
      'Get network points',
      'Status 200',
      `Status ${networkResponse.status()}`,
      networkResponse.status() === 200 ? 'pass' : 'fail'
    );

    // Test 6: Test 404 endpoint
    console.log('\nTest 6: GET /api/nonexistent');
    const notFoundResponse = await apiRequest.get(`${API_BASE_URL}/api/nonexistent`);
    logTest(
      'Non-existent endpoint',
      'Status 404',
      `Status ${notFoundResponse.status()}`,
      notFoundResponse.status() === 404 ? 'pass' : 'fail'
    );

    // Test 7: Get statistics
    console.log('\nTest 7: GET /api/statistics');
    const statsResponse = await apiRequest.get(`${API_BASE_URL}/api/statistics`);
    logTest(
      'Get statistics',
      'Status 200',
      `Status ${statsResponse.status()}`,
      statsResponse.status() === 200 ? 'pass' : 'fail'
    );

  } catch (error) {
    testResults.errors.push(error);
    testResults.success = false;
    console.error('\nAPI Test Error:', error.message);
  } finally {
    if (browser) {
      await browser.close();
    }
  }

  // Print summary
  console.log('\n=== API Test Summary ===');
  console.log(`Total tests: ${testResults.tests.length}`);
  console.log(`Passed: ${testResults.tests.filter(t => t.status === 'pass').length}`);
  console.log(`Failed: ${testResults.tests.filter(t => t.status === 'fail').length}`);
  console.log(`Errors: ${testResults.errors.length}`);
  console.log(`\nOverall: ${testResults.success ? 'PASSED' : 'FAILED'}`);

  return testResults;
}

// Execute API tests
testAPI().then(results => {
  process.exit(results.success ? 0 : 1);
}).catch(error => {
  console.error('Test execution failed:', error);
  process.exit(1);
});

# Playwright Test Examples

## Basic Tests

### 1. Simple Navigation Test

```javascript
import { test, expect } from '@playwright/test';

test('homepage loads', async ({ page }) => {
  await page.goto('http://localhost:3000');
  await expect(page).toHaveTitle(/Dashboard/);
});
```

### 2. Form Input Test

```javascript
test('login form submission', async ({ page }) => {
  await page.goto('http://localhost:3000/login');

  await page.fill('input[type="text"]', 'customer1');
  await page.fill('input[type="password"]', '123456');
  await page.click('button[type="submit"]');

  await expect(page).toHaveURL(/.*home/);
});
```

### 3. Table Data Test

```javascript
test('order table displays data', async ({ page }) => {
  await page.goto('http://localhost:3000/order');

  // Wait for table to load
  await page.waitForSelector('.el-table', { state: 'visible' });

  // Check table has rows
  const rows = await page.locator('.el-table__row').count();
  expect(rows).toBeGreaterThan(0);
});
```

## API Tests

### 1. GET Request

```javascript
test('fetch user list', async ({ page }) => {
  const response = await page.request.get('http://localhost:8080/api/users');
  expect(response.status()).toBe(200);

  const users = await response.json();
  expect(Array.isArray(users)).toBe(true);
});
```

### 2. POST Request

```javascript
test('create new order', async ({ page }) => {
  const newOrder = {
    orderNumber: 'ORD-' + Date.now(),
    businessUserId: 1,
    senderId: 1,
    recipientId: 1,
    goodsName: 'Test Product',
    quantity: 5,
    weight: 10.5
  };

  const response = await page.request.post('http://localhost:8080/api/orders', {
    data: newOrder,
    headers: { 'Content-Type': 'application/json' }
  });

  expect(response.status()).toBe(200);
  const order = await response.json();
  expect(order.orderNumber).toBe(newOrder.orderNumber);
});
```

### 3. Error Handling

```javascript
test('invalid request returns 400', async ({ page }) => {
  const response = await page.request.post('http://localhost:8080/api/orders', {
    data: { invalid: 'data' },
    headers: { 'Content-Type': 'application/json' }
  });

  expect(response.status()).toBeGreaterThanOrEqual(400);
});
```

## UI Interaction Tests

### 1. Button Click

```javascript
test('click new order button', async ({ page }) => {
  await page.goto('http://localhost:3000/order');

  await page.click('button:has-text("新增订单")');
  await page.waitForSelector('.el-dialog', { state: 'visible' });

  expect(await page.locator('.el-dialog').isVisible()).toBe(true);
});
```

### 2. Dropdown Selection

```javascript
test('select from dropdown', async ({ page }) => {
  await page.goto('http://localhost:3000/order/new');

  // Open dropdown
  await page.click('.el-select');
  await page.waitForSelector('.el-select-dropdown');

  // Select option
  await page.click('.el-select-dropdown__item:has-text("Option 1")');

  const selected = await page.locator('.el-select').textContent();
  expect(selected).toContain('Option 1');
});
```

### 3. Form Validation

```javascript
test('form validation shows errors', async ({ page }) => {
  await page.goto('http://localhost:3000/order/new');

  // Submit empty form
  await page.click('button:has-text("提交")');

  // Check for validation error
  const errorMsg = await page.locator('.el-form-item__error').first().textContent();
  expect(errorMsg).toBeTruthy();
});
```

## E2E Workflow Tests

### 1. Complete Order Creation

```javascript
test('complete order creation workflow', async ({ page }) => {
  // Step 1: Login
  await page.goto('http://localhost:3000/login');
  await page.fill('input[type="text"]', 'customer1');
  await page.fill('input[type="password"]', '123456');
  await page.click('button[type="submit"]');
  await page.waitForURL('**/home');

  // Step 2: Navigate to orders
  await page.goto('http://localhost:3000/order');

  // Step 3: Create new order
  await page.click('button:has-text("新增订单")');
  await page.waitForSelector('.el-dialog');

  // Step 4: Fill order form
  await page.fill('input[placeholder="订单编号"]', 'ORD-TEST-' + Date.now());
  await page.selectOption('select[name="type"]', '0');
  await page.fill('input[placeholder="货物名称"]', 'Test Goods');

  // Step 5: Submit
  await page.click('button:has-text("确定")');
  await page.waitForSelector('.el-message--success');

  // Step 6: Verify
  const tableRows = await page.locator('.el-table__row').count();
  expect(tableRows).toBeGreaterThan(0);
});
```

### 2. Multi-Role Workflow

```javascript
test('dispatcher assigns order to driver', async ({ page }) => {
  // Login as dispatcher
  await page.goto('http://localhost:3000/login');
  await page.fill('input[type="text"]', 'dispatcher1');
  await page.fill('input[type="password"]', '123456');
  await page.click('button[type="submit"]');
  await page.waitForURL('**/home');

  // Navigate to dispatch page
  await page.goto('http://localhost:3000/dispatch');

  // Find pending order
  const pendingOrder = page.locator('.el-table__row').first();
  await pendingOrder.locator('button:has-text("派单")').click();

  // Select driver
  await page.waitForSelector('.el-dialog');
  await page.click('.el-select-dropdown__item:has-text("Driver 1")');

  // Confirm
  await page.click('button:has-text("确认")');
  await page.waitForSelector('.el-message--success');
});
```

### 3. Data Persistence

```javascript
test('order persists after logout/login', async ({ page }) => {
  // Create order
  await page.goto('http://localhost:3000/login');
  await page.fill('input[type="text"]', 'customer1');
  await page.fill('input[type="password"]', '123456');
  await page.click('button[type="submit"]');

  const orderNumber = 'ORD-PERSIST-' + Date.now();
  await page.goto('http://localhost:3000/order/new');
  await page.fill('input[name="orderNumber"]', orderNumber);
  await page.click('button:has-text("提交")');

  // Logout
  await page.click('button:has-text("退出")');
  await page.waitForURL('**/login');

  // Login again
  await page.fill('input[type="text"]', 'customer1');
  await page.fill('input[type="password"]', '123456');
  await page.click('button[type="submit"]');
  await page.waitForURL('**/home');

  // Check order exists
  await page.goto('http://localhost:3000/order');
  const orderExists = await page.locator(`text=${orderNumber}`).isVisible();
  expect(orderExists).toBe(true);
});
```

## Advanced Tests

### 1. Network Interception

```javascript
test('mock API response', async ({ page }) => {
  await page.route('**/api/orders', route => {
    route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({ orders: [] })
    });
  });

  await page.goto('http://localhost:3000/order');

  const emptyState = await page.locator('.empty-state').isVisible();
  expect(emptyState).toBe(true);
});
```

### 2. File Download

```javascript
test('download order report', async ({ page }) => {
  await page.goto('http://localhost:3000/order');

  const [download] = await Promise.all([
    page.waitForEvent('download'),
    page.click('button:has-text("导出")')
  ]);

  const path = await download.path();
  expect(path).toContain('.xlsx');
});
```

### 3. Performance Measurement

```javascript
test('page loads within time limit', async ({ page }) => {
  const startTime = Date.now();
  await page.goto('http://localhost:3000/order');
  const loadTime = Date.now() - startTime;

  expect(loadTime).toBeLessThan(3000); // 3 seconds
});
```

### 4. Accessibility Check

```javascript
test('page is accessible', async ({ page }) => {
  await page.goto('http://localhost:3000/login');

  // Check for proper labels
  const usernameLabel = await page.locator('label:has-text("用户名")').isVisible();
  const passwordLabel = await page.locator('label:has-text("密码")').isVisible();

  expect(usernameLabel).toBe(true);
  expect(passwordLabel).toBe(true);

  // Check buttons have accessible names
  const submitButton = page.locator('button[type="submit"]');
  const buttonText = await submitButton.textContent();
  expect(buttonText).toBeTruthy();
});
```

## Real-World Test Patterns

### 1. Customer Role Tests

```javascript
describe('Customer Role', () => {
  test('can view own orders', async ({ page }) => {
    await loginAs(page, 'customer');
    await page.goto('http://localhost:3000/order');

    const orders = page.locator('.order-card');
    await expect(orders.first()).toBeVisible();
  });

  test('can create new order', async ({ page }) => {
    await loginAs(page, 'customer');
    await page.goto('http://localhost:3000/order/new');

    await fillOrderForm(page, {
      goodsName: 'Test Item',
      quantity: 5,
      weight: 2.5
    });

    await page.click('button:has-text("提交")');
    await expect(page.locator('.el-message--success')).toBeVisible();
  });
});
```

### 2. Business Verification Flow

```javascript
test('network point verifies order', async ({ page }) => {
  // Network operator workflow
  await loginAs(page, 'network');
  await page.goto('http://localhost:3000/network/order');

  // Find unverified order
  const unverifiedOrder = page.locator('.order-card.unverified').first();
  await unverifiedOrder.locator('button:has-text("确认")').click();

  // Fill verification form
  await page.fill('input[name="receiptNumber"]', 'RCP-' + Date.now());
  await page.click('button:has-text("确认入库")');

  // Verify status changed
  await expect(unverifiedOrder.locator('.status')).toHaveText('已入库');
});
```

### 3. Driver Delivery Flow

```javascript
test('driver updates delivery status', async ({ page }) => {
  await loginAs(page, 'driver');
  await page.goto('http://localhost:3000/driver/order');

  // Get first assigned order
  const order = page.locator('.order-card.assigned').first();

  // Update status to picked up
  await order.locator('button:has-text("已揽收")').click();
  await expect(order.locator('.status')).toHaveText('运输中');

  // Update to delivered
  await order.locator('button:has-text("确认送达")').click();
  await expect(order.locator('.status')).toHaveText('已送达');
});
```

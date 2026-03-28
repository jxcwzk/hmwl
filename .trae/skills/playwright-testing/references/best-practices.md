# Playwright Testing Best Practices

## Test Organization

### 1. Directory Structure

```
tests/
├── unit/                 # Unit tests
├── integration/          # Integration tests
├── e2e/                  # End-to-end tests
│   ├── login/
│   ├── orders/
│   └── workflows/
├── fixtures/             # Test data
└── helpers/              # Utility functions
```

### 2. Test Naming

```javascript
// Descriptive names
test('Customer can create order with valid data', async () => {});
test('Order creation fails with missing required fields', async () => {});

// Use describe for grouping
describe('Order Management', () => {
  describe('Create Order', () => {
    test('valid order', async () => {});
    test('invalid order', async () => {});
  });
});
```

## Page Object Model

### 1. Basic Page Object

```javascript
class LoginPage {
  constructor(page) {
    this.page = page;
    this.usernameInput = page.locator('input[type="text"]');
    this.passwordInput = page.locator('input[type="password"]');
    this.submitButton = page.locator('button[type="submit"]');
  }

  async login(username, password) {
    await this.usernameInput.fill(username);
    await this.passwordInput.fill(password);
    await this.submitButton.click();
  }
}
```

### 2. Usage

```javascript
test('User login flow', async ({ page }) => {
  const loginPage = new LoginPage(page);
  await loginPage.goto();
  await loginPage.login('user', 'pass');
  await expect(page).toHaveURL(/.*home/);
});
```

## Locator Strategies

### Priority Order

1. **Data-testid** (most reliable)
   ```html
   <button data-testid="submit-btn">Submit</button>
   ```
   ```javascript
   page.getByTestId('submit-btn')
   ```

2. **Role + Text**
   ```javascript
   page.getByRole('button', { name: 'Submit' })
   page.getByLabel('Username')
   page.getByPlaceholder('Enter name')
   ```

3. **Text content**
   ```javascript
   page.getByText('Submit Order')
   page.locator('h1:has-text("Dashboard")')
   ```

4. **CSS selectors** (last resort)
   ```javascript
   page.locator('.el-button--primary')
   page.locator('#submit-form')
   ```

## Async Handling

### 1. Avoid Sleep

❌ **Bad**:
```javascript
await page.waitForTimeout(2000); // Never use
```

✅ **Good**:
```javascript
await page.waitForSelector('.element', { state: 'visible' });
await expect(page.locator('.loading')).not.toBeVisible();
```

### 2. Handle Dynamic Content

```javascript
await page.waitForLoadState('networkidle');
await page.waitForResponse('**/api/orders');
await page.waitForFunction(() => window.apiLoaded === true);
```

### 3. Retry on Flakiness

```javascript
await page.locator('.dynamic-content')
  .waitFor({ state: 'visible', timeout: 5000 })
  .catch(() => console.log('Element not found'));
```

## Error Handling

### 1. Try-Catch Pattern

```javascript
async function safeClick(selector) {
  try {
    await page.locator(selector).click();
    return true;
  } catch (error) {
    console.error(`Click failed: ${selector}`, error);
    return false;
  }
}
```

### 2. Screenshot on Failure

```javascript
test('Order creation', async ({ page }) => {
  try {
    await page.click('button:has-text("Create")');
    await expect(page.locator('.success')).toBeVisible();
  } catch (error) {
    await page.screenshot({ path: `error-${Date.now()}.png` });
    throw error;
  }
});
```

## Performance Tips

### 1. Parallel Execution

```javascript
// playwright.config.js
export default {
  fullyParallel: true,
  workers: process.env.CI ? 1 : 3,
};
```

### 2. Reuse Context

```javascript
test.describe('Order tests', () => {
  let page;

  test.beforeAll(async ({ browser }) => {
    const context = await browser.newContext();
    page = await context.newPage();
  });

  test('create order', async () => {
    await page.goto('/order');
    // ...
  });
});
```

### 3. Fast Selectors

```javascript
// Fast (in DOM)
await page.locator('#order-id').click();

// Slow (requires search)
await page.locator('div > span > button').click();
```

## Security Testing

### 1. Authentication

```javascript
test('Protected route access', async ({ page }) => {
  // Should redirect to login
  await page.goto('/dashboard');
  await expect(page).toHaveURL(/.*login/);
});
```

### 2. Input Validation

```javascript
test('SQL injection prevention', async ({ page }) => {
  await page.fill('input[name="search"]', "' OR 1=1 --");
  await page.click('button[type="submit"]');
  // Should not show data or error
});
```

## CI/CD Integration

### GitHub Actions

```yaml
- name: Run Playwright Tests
  run: |
    npx playwright install --with-deps chromium
    npx playwright test

- name: Upload Results
  uses: actions/upload-artifact@v3
  if: always()
  with:
    name: playwright-report
    path: playwright-report/
```

### Docker

```dockerfile
FROM mcr.microsoft.com/playwright:v1.40.0
WORKDIR /app
COPY . .
RUN npx playwright install chromium
CMD ["npx", "playwright", "test"]
```

## Debugging

### 1. Visual Debug

```javascript
await page.pause(); // Opens inspector
```

### 2. Console Logs

```javascript
page.on('console', msg => {
  if (msg.type() === 'error') {
    console.log(`Browser error: ${msg.text()}`);
  }
});
```

### 3. Network Logs

```javascript
page.on('request', request => {
  console.log(`→ ${request.method()} ${request.url()}`);
});
page.on('response', response => {
  console.log(`← ${response.status()} ${response.url()}`);
});
```

## Common Issues

### 1. Element Not Found

**Problem**: Element disappears between find and click.

**Solution**:
```javascript
await page.locator('.dynamic-element')
  .waitFor({ state: 'attached', timeout: 5000 })
  .catch(() => null);
```

### 2. Stale Element

**Problem**: Element was updated by JavaScript.

**Solution**:
```javascript
await page.evaluate(() => {
  document.querySelector('.reload-btn').click();
});
```

### 3. Timing Issues

**Problem**: Race condition on slow networks.

**Solution**:
```javascript
await page.waitForLoadState('networkidle');
await page.waitForFunction(() => !document.querySelector('.loading'));
```

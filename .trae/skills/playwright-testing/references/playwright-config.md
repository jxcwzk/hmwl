# Playwright Configuration Guide

## Installation

### 1. Install Playwright Package

```bash
npm install -D @playwright/test
```

### 2. Install Browsers

```bash
npx playwright install chromium
npx playwright install firefox
npx playwright install webkit
```

### 3. Verify Installation

```bash
npx playwright --version
```

## Configuration Options

### Browser Launch Options

```javascript
const browser = await chromium.launch({
  headless: true,              // Run in headless mode
  slowMo: 100,                  // Slow down operations by 100ms
  devtools: true,               // Open DevTools
  executablePath: '/path/to/browser',  // Custom browser path
  args: [
    '--disable-startup-window',
    '--no-sandbox'
  ]
});
```

### Using Existing Browser Installation

If you have an existing Chromium installation (e.g., from an older Playwright version):

```javascript
browser = await chromium.launch({
  executablePath: '/Users/user/Library/Caches/ms-playwright/chromium-1105/chrome-mac/Chromium.app/Contents/MacOS/Chromium'
});
```

### Context Options

```javascript
const context = await browser.newContext({
  viewport: { width: 1920, height: 1080 },
  userAgent: 'Mozilla/5.0...',
  locale: 'zh-CN',
  timezoneId: 'Asia/Shanghai',
  permissions: ['geolocation'],
  recordVideo: { dir: 'videos/' }
});
```

### Page Options

```javascript
await page.goto('url', {
  waitUntil: 'networkidle',  // 'load', 'domcontentloaded', 'networkidle'
  timeout: 30000,
  referer: 'http://example.com'
});
```

## Common Selectors

### CSS Selectors

```javascript
await page.click('button.submit');        // Class selector
await page.fill('#username', 'text');     // ID selector
await page.fill('input[type="text"]', ''); // Attribute selector
```

### Text Selectors

```javascript
await page.click('text=Submit');                 // Exact match
await page.click('text=Submit Order');           // Partial match
await page.locator('button', { hasText: 'Submit' });
```

### Element State

```javascript
await page.click('button:visible');
await page.click('button:not([disabled])');
await page.click('button:has-text("Submit")');
```

### Playwright Locators

```javascript
const locator = page.locator('.button.primary');
await locator.click();
await locator.fill('text');
await locator.isVisible();
await locator.isEnabled();
```

## Waiting Strategies

### Explicit Waits

```javascript
await page.waitForSelector('.element', { state: 'visible', timeout: 5000 });
await page.waitForLoadState('networkidle');
await page.waitForURL('**/order/**');
```

### Condition Waits

```javascript
await expect(page.locator('.success')).toBeVisible({ timeout: 5000 });
await expect(page).toHaveURL('**/home');
```

## Assertions

### Common Assertions

```javascript
await expect(page.locator('.title')).toHaveText('Dashboard');
await expect(page.locator('.count')).toHaveCount(5);
await expect(page.locator('.item')).not.toBeEmpty();
await expect(page).toHaveURL(/.*order/);
```

## Network Interception

```javascript
await page.route('**/api/**', route => {
  if (route.request().url().includes('orders')) {
    route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({ orders: [] })
    });
  } else {
    route.continue();
  }
});
```

## Screenshots

```javascript
await page.screenshot();                              // Viewport
await page.screenshot({ fullPage: true });            // Full page
await page.screenshot({ path: 'shot.png' });          // Save to file
await page.screenshot({ selector: '.widget' });       // Element only
```

## Performance Metrics

```javascript
const metrics = await page.evaluate(() => {
  const timing = performance.timing;
  return {
    loadTime: timing.loadEventEnd - timing.navigationStart,
    domReady: timing.domContentLoadedEventEnd - timing.navigationStart
  };
});
```

## Best Practices

1. **Use Locators**: Prefer `page.locator()` over `page.$()`
2. **Explicit Waits**: Always wait for elements to be ready
3. **Retry Logic**: Use built-in retry for flaky tests
4. **Clean Up**: Always close browser in `finally` block
5. **Screenshots**: Capture on failure for debugging
6. **Isolation**: Each test should be independent

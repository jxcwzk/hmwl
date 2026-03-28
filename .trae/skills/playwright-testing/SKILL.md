---
name: "playwright-testing"
description: "Comprehensive Playwright testing skill for API and UI testing. Invoke when user explicitly requests Playwright testing or asks to test frontend/backend functionality."
---

# Playwright Testing Skill

Comprehensive end-to-end testing solution using Playwright for API testing, UI testing, and integration testing.

## Overview

This skill provides a complete testing framework using Playwright, supporting:
- **API Testing**: HTTP request/response validation
- **UI Testing**: Frontend page interaction and validation
- **E2E Testing**: Complete business workflow testing
- **Performance Testing**: Page load and interaction timing

## Directory Structure

```
playwright-testing/
├── SKILL.md                    # This file
├── scripts/                    # Test scripts
│   ├── api-test.js           # API testing examples
│   ├── ui-test.js            # UI testing examples
│   ├── e2e-test.js           # E2E workflow examples
│   └── login-test.js         # Authentication testing
├── references/               # Documentation
│   ├── playwright-config.md  # Configuration guide
│   ├── best-practices.md     # Testing best practices
│   └── test-examples.md      # Test case examples
└── assets/                   # Test resources
    ├── screenshots/          # Test screenshots
    ├── reports/              # Test reports
    └── test-data/            # Test data files
```

## Core Capabilities

### 1. API Testing

Test backend APIs directly:
- GET/POST/PUT/DELETE requests
- Response validation
- Status code checking
- JSON response parsing
- Authentication headers

### 2. UI Testing

Test frontend pages and components:
- Page navigation
- Form interactions
- Element visibility
- Screenshot capture
- User flows

### 3. E2E Testing

Test complete business workflows:
- Multi-step processes
- Role-based testing (Customer, Driver, Network, Dispatcher)
- Session management
- Data persistence validation

## Usage

### Quick Start

1. **Install dependencies**:
```bash
npm install -D @playwright/test
npx playwright install chromium
```

2. **Configure browser path** (if needed):
```javascript
browser = await chromium.launch({
  executablePath: '/path/to/chromium'
})
```

3. **Run tests**:
```bash
node <test-file>.js
```

### Test Configuration

**Base URL**: `http://localhost:3000` (default)

**Test roles and credentials**:
- Customer: `customer1` / `123456`
- Driver: `driver1` / `123456`
- Network: `network1` / `123456`
- Dispatcher: `dispatcher1` / `123456`

## Example Test Structure

```javascript
import { chromium } from '@playwright/test';

// Test configuration
const CONFIG = {
  baseUrl: 'http://localhost:3000',
  credentials: {
    username: 'customer1',
    password: '123456'
  }
};

// Test execution
async function runTest() {
  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage();

  try {
    // Navigate and interact
    await page.goto(`${CONFIG.baseUrl}/login`);
    await page.fill('input[type="text"]', CONFIG.credentials.username);
    await page.fill('input[type="password"]', CONFIG.credentials.password);
    await page.click('button[type="submit"]');

    // Wait for navigation
    await page.waitForLoadState('networkidle');

    // Take screenshot
    await page.screenshot({ path: 'screenshot.png', fullPage: true });

    console.log('Test passed!');
  } catch (error) {
    console.error('Test failed:', error);
  } finally {
    await browser.close();
  }
}
```

## Best Practices

1. **Always use explicit waits** instead of fixed timeouts
2. **Take screenshots on failure** for debugging
3. **Use descriptive selectors** (data-testid preferred)
4. **Isolate tests** - each test should be independent
5. **Log every step** with clear descriptions
6. **Validate both positive and negative cases**

## Common Issues & Solutions

### Browser not found
```bash
npx playwright install chromium
```

### Module not found
```bash
npm install -D @playwright/test
```

### Old browser version
Use `executablePath` to specify local browser:
```javascript
browser = await chromium.launch({
  executablePath: '/Users/user/Library/Caches/ms-playwright/chromium-1105/chrome-mac/Chromium.app/Contents/MacOS/Chromium'
})
```

## Integration

### With CI/CD
```yaml
# GitHub Actions example
- name: Run Playwright Tests
  run: |
    npm install
    npx playwright install chromium
    node tests/e2e-test.js
```

### With Frontend Project
Add to `package.json`:
```json
{
  "scripts": {
    "test:e2e": "node ../.trae/skills/playwright-testing/scripts/e2e-test.js"
  }
}
```

## Trigger Conditions

**Invoke this skill when**:
- User explicitly requests Playwright testing
- User asks to test frontend pages or components
- User wants to validate API endpoints
- User needs E2E workflow testing
- User asks to set up automated testing

## Notes

- Frontend must be running at `http://localhost:3000`
- Backend API typically at `http://localhost:8080`
- Screenshots saved to `assets/screenshots/`
- Test reports saved to `assets/reports/`

const { chromium } = require('@playwright/test');

const CONFIG = {
  baseUrl: 'http://localhost:3000',
  chromiumPath: '/Users/jiangxiaochun/Library/Caches/ms-playwright/chromium-1105/chrome-mac/Chromium.app/Contents/MacOS/Chromium'
};

async function explore() {
  const browser = await chromium.launch({
    headless: true,
    executablePath: CONFIG.chromiumPath
  });

  const page = await browser.newPage();
  await page.setViewportSize({ width: 1280, height: 720 });

  console.log('1. Logging in as customer...');
  await page.goto(`${CONFIG.baseUrl}/login`);
  await page.waitForLoadState('networkidle');

  await page.fill('input[placeholder="请输入用户名"]', 'customer1');
  await page.fill('input[placeholder="请输入密码"]', '123456');
  await page.click('button:has-text("登 录")');
  await page.waitForLoadState('networkidle');
  await page.waitForTimeout(1000);

  console.log('2. Going directly to create order URL...');
  await page.goto(`${CONFIG.baseUrl}/order/create`);
  await page.waitForLoadState('networkidle');
  await page.waitForTimeout(1000);

  console.log('\n=== Form page structure ===');
  const inputs = await page.$$('input');
  console.log(`Found ${inputs.length} input fields:`);
  for (const input of inputs) {
    const type = await input.getAttribute('type');
    const placeholder = await input.getAttribute('placeholder');
    const name = await input.getAttribute('name');
    const id = await input.getAttribute('id');
    console.log(`  - type=${type}, placeholder="${placeholder}", name=${name}, id=${id}`);
  }

  const selects = await page.$$('select');
  console.log(`Found ${selects.length} select fields:`);
  for (const select of selects) {
    const name = await select.getAttribute('name');
    const id = await select.getAttribute('id');
    console.log(`  - name=${name}, id=${id}`);
    const options = await select.$$('option');
    console.log(`    ${options.length} options`);
  }

  const textareas = await page.$$('textarea');
  console.log(`Found ${textareas.length} textarea fields:`);
  for (const textarea of textareas) {
    const name = await textarea.getAttribute('name');
    const placeholder = await textarea.getAttribute('placeholder');
    console.log(`  - name=${name}, placeholder="${placeholder}"`);
  }

  const buttons = await page.$$('button');
  console.log(`Found ${buttons.length} buttons:`);
  for (const button of buttons) {
    const text = await button.textContent();
    const type = await button.getAttribute('type');
    console.log(`  - text="${text}", type=${type}`);
  }

  await page.screenshot({ path: 'order-form-page.png', fullPage: true });
  console.log('\nScreenshot saved: order-form-page.png');

  await browser.close();
}

explore().catch(console.error);

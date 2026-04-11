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
  await page.waitForTimeout(2000);

  console.log('\n2. Looking for order buttons...');
  const allButtons = await page.$$('button');
  console.log(`Found ${allButtons.length} buttons total`);
  for (const btn of allButtons) {
    const text = await btn.textContent();
    const className = await btn.getAttribute('class');
    console.log(`  Button: text="${text.trim()}", class="${className}"`);
  }

  console.log('\n3. Looking for "快速下单" text...');
  const quickOrder = await page.$('text=快速下单');
  if (quickOrder) {
    console.log('Found 快速下单!');
    await quickOrder.click();
    await page.waitForTimeout(3000);
    console.log(`URL: ${page.url()}`);
  }

  console.log('\n4. After click - page content...');
  const text = await page.textContent('body');
  console.log(text.substring(0, 3000));

  console.log('\n5. Looking for form elements...');
  const inputs = await page.$$('input');
  console.log(`Inputs: ${inputs.length}`);
  for (const input of inputs) {
    const type = await input.getAttribute('type');
    const placeholder = await input.getAttribute('placeholder');
    const name = await input.getAttribute('name');
    console.log(`  - type=${type}, placeholder="${placeholder}", name=${name}`);
  }

  const selects = await page.$$('select');
  console.log(`Selects: ${selects.length}`);

  const textareas = await page.$$('textarea');
  console.log(`Textareas: ${textareas.length}`);
  for (const ta of textareas) {
    const name = await ta.getAttribute('name');
    const placeholder = await ta.getAttribute('placeholder');
    console.log(`  - name=${name}, placeholder="${placeholder}"`);
  }

  const newButtons = await page.$$('button');
  console.log(`Buttons: ${newButtons.length}`);
  for (const btn of newButtons) {
    const text = await btn.textContent();
    console.log(`  - text="${text.trim()}"`);
  }

  await page.screenshot({ path: 'quick-order-form.png', fullPage: true });
  console.log('\nScreenshot saved: quick-order-form.png');

  await browser.close();
}

explore().catch(console.error);

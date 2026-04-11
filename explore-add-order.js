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

  console.log('2. Clicking 快速下单...');
  const quickOrder = await page.$('text=快速下单');
  if (quickOrder) {
    await quickOrder.click();
    await page.waitForTimeout(2000);
  }

  console.log('3. Clicking 新增订单...');
  const addOrder = await page.$('text=新增订单');
  if (addOrder) {
    await addOrder.click();
    await page.waitForTimeout(3000);
  }

  console.log(`\nURL: ${page.url()}`);

  console.log('\n=== Page content after 新增订单 ===');
  const text = await page.textContent('body');
  console.log(text.substring(0, 4000));

  console.log('\n=== Looking for dialog/modal ===');
  const dialogs = await page.$$('.el-dialog, .dialog, [class*="dialog"], [class*="modal"]');
  console.log(`Found ${dialogs.length} dialogs`);

  console.log('\n=== Form elements ===');
  const inputs = await page.$$('input');
  console.log(`Inputs: ${inputs.length}`);
  for (const input of inputs) {
    const type = await input.getAttribute('type');
    const placeholder = await input.getAttribute('placeholder');
    const name = await input.getAttribute('name');
    const vmodel = await input.getAttribute('v-model');
    console.log(`  - type=${type}, placeholder="${placeholder}", name=${name}, v-model=${vmodel}`);
  }

  const selects = await page.$$('select');
  console.log(`Selects: ${selects.length}`);
  for (const select of selects) {
    const name = await select.getAttribute('name');
    const className = await select.getAttribute('class');
    console.log(`  - name=${name}, class=${className}`);
  }

  const textareas = await page.$$('textarea');
  console.log(`Textareas: ${textareas.length}`);
  for (const ta of textareas) {
    const name = await ta.getAttribute('name');
    const placeholder = await ta.getAttribute('placeholder');
    console.log(`  - name=${name}, placeholder="${placeholder}"`);
  }

  const buttons = await page.$$('button');
  console.log(`Buttons: ${buttons.length}`);
  for (const btn of buttons) {
    const text = await btn.textContent();
    console.log(`  - text="${text.trim()}"`);
  }

  await page.screenshot({ path: 'add-order-form.png', fullPage: true });
  console.log('\nScreenshot saved: add-order-form.png');

  await browser.close();
}

explore().catch(console.error);

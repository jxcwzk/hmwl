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

  console.log('2. Navigate to order page and open dialog...');
  await page.goto(`${CONFIG.baseUrl}/order`);
  await page.waitForLoadState('networkidle');
  await page.waitForTimeout(2000);

  const addOrder = await page.$('text=新增订单');
  if (addOrder) {
    await addOrder.click();
    await page.waitForTimeout(3000);
  }

  console.log('\n=== Dialog content ===');
  const dialogText = await page.textContent('.el-dialog__body');
  console.log(dialogText || 'No dialog body found');

  console.log('\n=== Clicking on 发件人 input ===');
  const senderInput = await page.$('input[placeholder="请选择发件人"]');
  if (senderInput) {
    await senderInput.click();
    await page.waitForTimeout(1000);

    console.log('\n=== After clicking 发件人 ===');
    const dropdownContent = await page.textContent('.el-select-dropdown, .el-dropdown-menu, [class*="dropdown"]');
    console.log(dropdownContent ? dropdownContent.substring(0, 1000) : 'No dropdown found');

    const dropdownItems = await page.$$('.el-select-dropdown__item, .el-dropdown-menu__item, [class*="option"]');
    console.log(`Found ${dropdownItems.length} dropdown items`);
    for (const item of dropdownItems) {
      const text = await item.textContent();
      console.log(`  - "${text}"`);
    }
  }

  console.log('\n=== Checking for Vue components ===');
  const vueApp = await page.$('#app');
  if (vueApp) {
    const html = await page.content();
    const vueMatch = html.match(/data-v-[a-z]+="[^"]*"/g);
    if (vueMatch) {
      console.log('Vue directives found:', vueMatch.slice(0, 10).join(', '));
    }
  }

  await page.screenshot({ path: 'order-dialog.png', fullPage: true });
  console.log('\nScreenshot saved: order-dialog.png');

  await browser.close();
}

explore().catch(console.error);

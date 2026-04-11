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

  console.log(`\n=== URL after login: ${page.url()} ===`);

  console.log('\n=== Page visible text ===');
  const text = await page.textContent('body');
  console.log(text.substring(0, 1500));

  console.log('\n=== Menu/Links on page ===');
  const menuItems = await page.$$('.el-menu-item, .menu-item, a[href]');
  for (const item of menuItems) {
    const href = await item.getAttribute('href');
    const text = await item.textContent();
    const onclick = await item.getAttribute('@click');
    if (href || text) {
      console.log(`  - text="${text.trim()}", href=${href}`);
    }
  }

  console.log('\n=== Looking for create order button ===');
  const createOrderBtn = await page.$('text=创建订单');
  if (createOrderBtn) {
    console.log('Found "创建订单" button!');
    await createOrderBtn.click();
    await page.waitForTimeout(3000);
    console.log(`\n=== URL after click: ${page.url()} ===`);

    console.log('\n=== Page content after click ===');
    const newText = await page.textContent('body');
    console.log(newText.substring(0, 2000));
  } else {
    console.log('Did NOT find "创建订单" button');
    await page.screenshot({ path: 'after-login.png', fullPage: true });
  }

  await page.screenshot({ path: 'create-order-page.png', fullPage: true });
  console.log('\nScreenshots saved');

  await browser.close();
}

explore().catch(console.error);

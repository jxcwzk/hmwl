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

  console.log('2. Navigating to create order page...');
  const createBtn = await page.$('text=创建订单');
  if (createBtn) {
    await createBtn.click();
  } else {
    await page.goto(`${CONFIG.baseUrl}/order/create`);
  }
  await page.waitForLoadState('networkidle');
  await page.waitForTimeout(500);

  console.log('3. Filling order form...');
  const selects = await page.$$('select');
  if (selects.length >= 2) {
    await selects[0].selectOption({ index: 1 });
    await selects[1].selectOption({ index: 1 });
  }

  const weightInput = await page.$('input[placeholder*="重量"], input[name*="weight"]');
  if (weightInput) await weightInput.fill('10.5');

  const volumeInput = await page.$('input[placeholder*="体积"], input[name*="volume"]');
  if (volumeInput) await volumeInput.fill('2');

  const qtyInput = await page.$('input[placeholder*="数量"], input[name*="quantity"]');
  if (qtyInput) await qtyInput.fill('1');

  console.log('4. Submitting order...');
  const submitBtn = await page.$('button:has-text("提交")');
  if (submitBtn) {
    await submitBtn.click();
  } else {
    const createBtn2 = await page.$('button:has-text("创建")');
    if (createBtn2) await createBtn2.click();
  }
  await page.waitForLoadState('networkidle');
  await page.waitForTimeout(2000);

  console.log('5. Taking screenshot of result page...');
  await page.screenshot({ path: 'order-created-page.png', fullPage: true });

  console.log('\n=== Current URL ===');
  console.log(page.url());

  console.log('\n=== Page Text Content (first 2000 chars) ===');
  const text = await page.textContent('body');
  console.log(text.substring(0, 2000));

  console.log('\n=== Looking for Order ID patterns ===');
  const bodyHtml = await page.content();

  const orderPatterns = [
    /订单号[：:\s]*(\d+)/,
    /order[_\s]?id[：:\s]*(\d+)/i,
    /编号[：:\s]*(\d+)/,
    /Order No[.:\s]*(\d+)/i,
    /(\d{10,})/,
  ];

  for (const pattern of orderPatterns) {
    const match = bodyHtml.match(pattern);
    if (match) {
      console.log(`Found pattern ${pattern}: ${match[0]} -> ID: ${match[1]}`);
    }
  }

  console.log('\n=== All elements with class/id containing "order" ===');
  const orderElements = await page.$$('[class*="order"], [id*="order"]');
  for (const el of orderElements) {
    const className = await el.getAttribute('class');
    const id = await el.getAttribute('id');
    const text = await el.textContent();
    if (text && text.trim()) {
      console.log(`Element: class="${className}", id="${id}", text="${text.substring(0, 100)}"`);
    }
  }

  console.log('\nScreenshot saved: order-created-page.png');
  await browser.close();
}

explore().catch(console.error);

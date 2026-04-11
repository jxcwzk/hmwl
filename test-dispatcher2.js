const { chromium } = require('@playwright/test');

const CONFIG = {
  baseUrl: 'http://localhost:3000',
  chromiumPath: '/Users/jiangxiaochun/Library/Caches/ms-playwright/chromium-1105/chrome-mac/Chromium.app/Contents/MacOS/Chromium'
};

async function test() {
  const browser = await chromium.launch({
    headless: true,
    executablePath: CONFIG.chromiumPath
  });

  const context = await browser.newContext();
  const page = await context.newPage();
  await page.setViewportSize({ width: 1280, height: 720 });

  console.log('1. Testing dispatcher (clear cookies first)...');
  await page.goto(`${CONFIG.baseUrl}/login`, { timeout: 60000 });
  await page.waitForLoadState('domcontentloaded');
  await page.waitForTimeout(2000);

  console.log('URL:', page.url());
  console.log('Page content:');
  const text = await page.textContent('body');
  console.log(text.substring(0, 1000));

  await page.screenshot({ path: 'dispatcher-login-1.png', fullPage: true });

  console.log('\n2. Clearing cookies and retry...');
  await context.clearCookies();

  await page.goto(`${CONFIG.baseUrl}/login`, { timeout: 60000 });
  await page.waitForLoadState('domcontentloaded');
  await page.waitForTimeout(3000);

  console.log('URL:', page.url());
  console.log('Page content:');
  const text2 = await page.textContent('body');
  console.log(text2.substring(0, 1000));

  await page.screenshot({ path: 'dispatcher-login-2.png', fullPage: true });

  console.log('\n3. Trying to fill login form...');
  const userInput = await page.$('input[placeholder="请输入用户名"]');
  const passInput = await page.$('input[placeholder="请输入密码"]');
  const loginBtn = await page.$('button:has-text("登 录")');

  console.log('User input:', userInput ? 'found' : 'not found');
  console.log('Pass input:', passInput ? 'found' : 'not found');
  console.log('Login btn:', loginBtn ? 'found' : 'not found');

  if (userInput && passInput && loginBtn) {
    await userInput.fill('dispatcher1');
    await passInput.fill('123456');
    await loginBtn.click();
    await page.waitForLoadState('networkidle');
    await page.waitForTimeout(3000);

    console.log('\nAfter login:');
    console.log('URL:', page.url());
    const text3 = await page.textContent('body');
    console.log(text3.substring(0, 1000));
    await page.screenshot({ path: 'dispatcher-after-login.png', fullPage: true });
  }

  await browser.close();
}

test().catch(console.error);

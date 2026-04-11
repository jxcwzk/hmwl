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

  console.log('Navigating to login page...');
  await page.goto(`${CONFIG.baseUrl}/login`);
  await page.waitForLoadState('networkidle');

  console.log('\n=== Page Content ===');
  const html = await page.content();
  console.log(html.substring(0, 3000));

  console.log('\n=== All Input Fields ===');
  const inputs = await page.$$('input');
  for (const input of inputs) {
    const name = await input.getAttribute('name');
    const type = await input.getAttribute('type');
    const placeholder = await input.getAttribute('placeholder');
    console.log(`Input: name=${name}, type=${type}, placeholder=${placeholder}`);
  }

  console.log('\n=== All Buttons ===');
  const buttons = await page.$$('button');
  for (const button of buttons) {
    const text = await button.textContent();
    const type = await button.getAttribute('type');
    console.log(`Button: text="${text}", type=${type}`);
  }

  console.log('\n=== All Links ===');
  const links = await page.$$('a');
  for (const link of links) {
    const href = await link.getAttribute('href');
    const text = await link.textContent();
    console.log(`Link: text="${text}", href=${href}`);
  }

  await page.screenshot({ path: 'login-page.png', fullPage: true });
  console.log('\nScreenshot saved: login-page.png');

  await browser.close();
}

explore().catch(console.error);

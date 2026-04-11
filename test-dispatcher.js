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

  console.log('Testing dispatcher login...');
  await page.goto(`${CONFIG.baseUrl}/login`, { timeout: 60000 });
  await page.waitForLoadState('domcontentloaded');
  await page.waitForTimeout(3000);

  console.log(`URL: ${page.url()}`);
  console.log('\nPage content:');
  const text = await page.textContent('body');
  console.log(text.substring(0, 2000));

  await page.screenshot({ path: 'dispatcher-login.png', fullPage: true });
  console.log('\nScreenshot saved: dispatcher-login.png');

  await browser.close();
}

explore().catch(console.error);

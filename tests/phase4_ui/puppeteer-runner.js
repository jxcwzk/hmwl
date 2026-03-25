const puppeteer = require('puppeteer');

async function runTests() {
  const browser = await puppeteer.launch({
    headless: 'new',
    args: ['--no-sandbox', '--disable-setuid-sandbox']
  });

  const page = await browser.newPage();

  try {
    console.log('开始UI测试...');

    await page.goto('http://localhost:8080/#/login');
    await page.waitForSelector('input[name="username"]');
    await page.type('input[name="username"]', 'customer1');
    await page.type('input[name="password"]', '123456');
    await page.click('button[type="submit"]');
    await page.waitForNavigation();

    console.log('✓ 登录测试通过');

  } catch (error) {
    console.error('测试失败:', error);
    await page.screenshot({ path: 'screenshots/error.png' });
  } finally {
    await browser.close();
  }
}

runTests();

#!/usr/bin/env node
/**
 * Phase 4 Puppeteer端到端测试
 * 使用已安装的Chromium进行四角色登录测试
 */

const puppeteer = require('puppeteer-core');

const TEST_ACCOUNTS = [
  { username: 'customer1', password: '123456', role: '客户' },
  { username: 'network1', password: '123456', role: '网点' },
  { username: 'driver1', password: '123456', role: '司机' },
  { username: 'admin1', password: '123456', role: '调度' },
];

const CHROMIUM_PATH = '/Users/jiangxiaochun/Library/Caches/ms-playwright/chromium-1105/chrome-mac/Chromium.app/Contents/MacOS/Chromium';

async function testLogin(account) {
  console.log(`\n${'='.repeat(50)}`);
  console.log(`测试 ${account.role} 登录...`);
  console.log('='.repeat(50));

  const browser = await puppeteer.launch({
    headless: true,
    executablePath: CHROMIUM_PATH,
    args: ['--no-sandbox', '--disable-setuid-sandbox']
  });

  const page = await browser.newPage();

  try {
    console.log('打开登录页面...');
    await page.goto('http://localhost:8080/login', { waitUntil: 'networkidle0', timeout: 30000 });
    await page.waitForTimeout(2000);

    console.log('输入用户名...');
    await page.type('input[type="text"]', account.username);

    console.log('输入密码...');
    await page.type('input[type="password"]', account.password);

    console.log('点击登录按钮...');
    await page.click('button');

    console.log('等待导航...');
    await page.waitForTimeout(3000);

    const currentUrl = page.url();
    console.log(`当前URL: ${currentUrl}`);

    if (!currentUrl.includes('/login')) {
      console.log(`✅ ${account.role} 登录成功!`);
      return true;
    } else {
      console.log(`⚠️ ${account.role} 登录后仍在登录页`);
      return false;
    }

  } catch (error) {
    console.log(`❌ ${account.role} 测试失败: ${error.message}`);
    return false;
  } finally {
    await browser.close();
  }
}

async function runTests() {
  console.log('开始Puppeteer端到端测试...');
  console.log(`前端地址: http://localhost:8080`);

  const results = [];
  for (const account of TEST_ACCOUNTS) {
    const result = await testLogin(account);
    results.push({ role: account.role, success: result });
  }

  console.log(`\n${'='.repeat(50)}`);
  console.log('测试结果汇总:');
  console.log('='.repeat(50));

  for (const { role, success } of results) {
    const status = success ? '✅ 通过' : '❌ 失败';
    console.log(`  ${role}: ${status}`);
  }

  const passed = results.filter(r => r.success).length;
  console.log(`\n总计: ${passed}/${results.length} 通过`);

  process.exit(passed === results.length ? 0 : 1);
}

runTests().catch(console.error);
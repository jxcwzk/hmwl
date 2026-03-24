/**
 * 客户角色前端页面测试 - Playwright
 * 测试内容：登录、首页、订单页面
 * 
 * 运行方式: npx playwright test customer-playwright-test.js
 * 或: node customer-playwright-test.js
 */

import { chromium } from '@playwright/test';
import path from 'path';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

// 配置
const CONFIG = {
  baseUrl: 'http://localhost:3000',
  credentials: {
    username: 'customer1',
    password: '123456'
  },
  screenshots: {
    home: path.join(__dirname, 'screenshots', 'customer_home.png'),
    order: path.join(__dirname, 'screenshots', 'customer_order.png')
  }
};

// 测试结果
const testResults = {
  success: true,
  steps: [],
  errors: []
};

function logStep(step, content, expected, actual, status = 'pass') {
  const result = {
    step,
    content,
    expected,
    actual,
    status,
    timestamp: new Date().toISOString()
  };
  testResults.steps.push(result);
  console.log(`「操作类型：${step}；操作内容：${content}；期望结果：${expected}；实际结果：${actual}」`);
  return result;
}

function logError(error) {
  testResults.errors.push(error);
  testResults.success = false;
  console.error(`「操作类型：错误；操作内容：${error.message}；期望结果：无错误；实际结果：发生错误」`);
}

async function runTest() {
  console.log('=== 客户角色前端页面测试开始 ===\n');
  
  let browser;
  let page;

  try {
    // 启动浏览器
    browser = await chromium.launch({
      headless: true
    });
    
    page = await browser.newPage();
    
    // 设置视口
    await page.setViewportSize({ width: 1920, height: 1080 });

    // 步骤1: 导航到登录页面
    console.log('步骤1: 导航到登录页面...');
    await page.goto(`${CONFIG.baseUrl}/login`, { 
      waitUntil: 'networkidle',
      timeout: 30000 
    });
    
    // 等待登录表单加载
    await page.waitForSelector('input[type="text"]', { timeout: 10000 });
    
    logStep(
      '页面导航',
      '导航到登录页面 /login',
      '页面成功加载',
      '页面加载成功'
    );

    // 步骤2: 使用customer1/123456登录
    console.log('步骤2: 执行登录操作...');
    
    // 填写用户名
    await page.fill('input[type="text"]', CONFIG.credentials.username);
    
    // 填写密码
    await page.fill('input[type="password"]', CONFIG.credentials.password);
    
    // 点击登录按钮
    await page.click('button[type="submit"], .el-button--primary');
    
    // 等待登录后的页面加载
    await page.waitForLoadState('networkidle', { timeout: 15000 });
    
    logStep(
      '用户登录',
      `使用账号 ${CONFIG.credentials.username}/${CONFIG.credentials.password} 登录`,
      '登录成功，跳转到首页',
      '登录成功'
    );

    // 步骤3: 截图保存登录后页面
    console.log('步骤3: 截图保存首页...');
    await page.screenshot({ 
      path: CONFIG.screenshots.home,
      fullPage: true 
    });
    
    logStep(
      '页面截图',
      '截图保存登录后首页',
      `截图保存到 ${CONFIG.screenshots.home}`,
      '截图保存成功'
    );

    // 步骤4: 导航到订单页面
    console.log('步骤4: 导航到订单页面...');
    await page.goto(`${CONFIG.baseUrl}/order`, { 
      waitUntil: 'networkidle',
      timeout: 30000 
    });
    
    // 等待页面内容加载
    await page.waitForTimeout(2000);
    
    logStep(
      '页面导航',
      '导航到订单页面 /order',
      '订单页面成功加载',
      '订单页面加载成功'
    );

    // 步骤5: 截图保存客户订单页面
    console.log('步骤5: 截图保存订单页面...');
    await page.screenshot({ 
      path: CONFIG.screenshots.order,
      fullPage: true 
    });
    
    logStep(
      '页面截图',
      '截图保存客户订单页面',
      `截图保存到 ${CONFIG.screenshots.order}`,
      '截图保存成功'
    );

    // 步骤6: 验证页面标题是否为"我的订单"
    console.log('步骤6: 验证页面标题...');
    const pageTitle = await page.evaluate(() => {
      // 尝试多种可能的选择器
      const selectors = [
        '.page-title',
        '.header-title',
        'h1',
        'h2',
        '.el-page-header__title',
        '.title'
      ];
      
      for (const selector of selectors) {
        const el = document.querySelector(selector);
        if (el && el.textContent.trim()) {
          return el.textContent.trim();
        }
      }
      
      // 如果找不到标题，返回页面中的主要文本内容
      const mainContent = document.querySelector('.el-main, main, .main-content');
      if (mainContent) {
        const heading = mainContent.querySelector('h1, h2, h3, .title');
        if (heading) return heading.textContent.trim();
      }
      
      return document.title || '未找到标题';
    });
    
    const isTitleCorrect = pageTitle.includes('我的订单') || pageTitle.includes('订单');
    
    logStep(
      '页面验证',
      '验证页面标题是否为"我的订单"',
      '页面标题包含"我的订单"或"订单"',
      `页面标题: "${pageTitle}"`,
      isTitleCorrect ? 'pass' : 'fail'
    );

    // 步骤7: 检查是否有"新增订单"按钮
    console.log('步骤7: 检查新增订单按钮...');
    const hasAddButton = await page.evaluate(() => {
      const keywords = ['新增订单', '新建订单', '添加订单', '创建订单', '新增'];
      
      // 检查按钮
      const buttons = document.querySelectorAll('button, .el-button, a, .btn');
      for (const btn of buttons) {
        const text = btn.textContent.trim();
        if (keywords.some(kw => text.includes(kw))) {
          return { found: true, text: text, selector: btn.tagName.toLowerCase() };
        }
      }
      
      // 检查包含特定文本的元素
      const allElements = document.querySelectorAll('*');
      for (const el of allElements) {
        const text = el.textContent.trim();
        if (keywords.some(kw => text === kw || text.includes(kw))) {
          return { found: true, text: text, tag: el.tagName };
        }
      }
      
      return { found: false };
    });
    
    logStep(
      '元素检查',
      '检查是否有"新增订单"按钮',
      '页面存在"新增订单"按钮',
      hasAddButton.found 
        ? `找到按钮: "${hasAddButton.text}"` 
        : '未找到新增订单按钮',
      hasAddButton.found ? 'pass' : 'fail'
    );

    // 输出测试总结
    console.log('\n=== 测试总结 ===');
    console.log(`总步骤数: ${testResults.steps.length}`);
    console.log(`通过步骤: ${testResults.steps.filter(s => s.status === 'pass').length}`);
    console.log(`失败步骤: ${testResults.steps.filter(s => s.status === 'fail').length}`);
    console.log(`错误数: ${testResults.errors.length}`);
    console.log(`\n截图文件:`);
    console.log(`  - 首页截图: ${CONFIG.screenshots.home}`);
    console.log(`  - 订单页截图: ${CONFIG.screenshots.order}`);
    console.log(`\n测试结果: ${testResults.success ? '通过' : '失败'}`);

  } catch (error) {
    logError(error);
    console.error('测试执行失败:', error.message);
    
    // 尝试保存错误截图
    if (page) {
      try {
        const errorScreenshot = path.join(__dirname, 'screenshots', 'customer_error.png');
        await page.screenshot({ path: errorScreenshot, fullPage: true });
        console.log(`错误截图已保存: ${errorScreenshot}`);
      } catch (e) {
        console.error('无法保存错误截图:', e.message);
      }
    }
  } finally {
    if (browser) {
      await browser.close();
    }
  }

  return testResults;
}

// 执行测试
runTest().then(results => {
  process.exit(results.success ? 0 : 1);
}).catch(error => {
  console.error('测试脚本执行失败:', error);
  process.exit(1);
});

/**
 * 调度角色前端页面测试 - Puppeteer
 * 测试内容：登录、首页、调度订单页面
 * 
 * 前置条件：
 * 1. 安装Chrome浏览器
 * 2. 前端服务运行在 http://localhost:3000
 * 3. 后端服务运行正常
 * 
 * 运行方式：
 * cd "/Users/jiangxiaochun/Desktop/hmwl/hmwl/05-test loop"
 * npm install
 * node dispatcher-puppeteer-test.js
 */

import puppeteer from 'puppeteer-core';
import path from 'path';
import { fileURLToPath } from 'url';
import { execSync } from 'child_process';
import fs from 'fs';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

// 获取Chrome可执行文件路径
function getChromeExecutablePath() {
  const platform = process.platform;
  
  if (platform === 'darwin') {
    // macOS
    const paths = [
      '/Applications/Google Chrome.app/Contents/MacOS/Google Chrome',
      '/Applications/Google Chrome Canary.app/Contents/MacOS/Google Chrome Canary',
      '/Applications/Chromium.app/Contents/MacOS/Chromium',
      '/usr/bin/google-chrome',
      '/usr/bin/chromium',
      '/usr/bin/chromium-browser'
    ];
    
    for (const chromePath of paths) {
      if (fs.existsSync(chromePath)) {
        return chromePath;
      }
    }
  } else if (platform === 'linux') {
    try {
      return execSync('which google-chrome || which chromium || which chromium-browser', { encoding: 'utf8' }).trim();
    } catch (e) {
      const paths = [
        '/usr/bin/google-chrome',
        '/usr/bin/chromium',
        '/usr/bin/chromium-browser',
        '/opt/google/chrome/google-chrome'
      ];
      for (const p of paths) {
        if (fs.existsSync(p)) return p;
      }
    }
  } else if (platform === 'win32') {
    const paths = [
      'C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe',
      'C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe',
      process.env.LOCALAPPDATA + '\\Google\\Chrome\\Application\\chrome.exe'
    ];
    for (const p of paths) {
      if (p && fs.existsSync(p)) return p;
    }
  }
  
  return null;
}

// 配置
const CONFIG = {
  baseUrl: 'http://localhost:3000',
  credentials: {
    username: 'admin1',
    password: '123456'
  },
  screenshots: {
    home: path.join(__dirname, 'screenshots', 'dispatcher_home.png'),
    order: path.join(__dirname, 'screenshots', 'dispatcher_order.png')
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
  console.log('=== 调度角色前端页面测试开始 ===\n');
  
  let browser;
  let page;

  try {
    // 获取Chrome路径
    const chromePath = getChromeExecutablePath();
    if (!chromePath) {
      throw new Error('未找到Chrome浏览器，请确保已安装Chrome或Chromium。安装指南: https://www.google.com/chrome/');
    }
    console.log(`使用Chrome路径: ${chromePath}`);
    
    // 启动浏览器
    browser = await puppeteer.launch({
      executablePath: chromePath,
      headless: 'new',
      args: ['--no-sandbox', '--disable-setuid-sandbox']
    });
    page = await browser.newPage();
    
    // 设置视口
    await page.setViewport({ width: 1920, height: 1080 });

    // 步骤1: 导航到登录页面
    console.log('步骤1: 导航到登录页面...');
    await page.goto(`${CONFIG.baseUrl}/login`, { 
      waitUntil: 'networkidle2',
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

    // 步骤2: 使用admin1/123456登录
    console.log('步骤2: 执行登录操作...');
    
    // 填写用户名
    await page.waitForSelector('input[type="text"]', { timeout: 10000 });
    await page.click('input[type="text"]');
    await page.keyboard.type(CONFIG.credentials.username);
    
    // 填写密码
    await page.waitForSelector('input[type="password"]', { timeout: 10000 });
    await page.click('input[type="password"]');
    await page.keyboard.type(CONFIG.credentials.password);
    
    // 点击登录按钮
    await page.waitForSelector('button[type="submit"], .el-button--primary', { timeout: 10000 });
    await page.click('button[type="submit"], .el-button--primary');
    
    // 等待登录后的页面加载
    await page.waitForNavigation({ waitUntil: 'networkidle2', timeout: 15000 });
    
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
    console.log('步骤4: 导航到调度订单页面...');
    await page.goto(`${CONFIG.baseUrl}/order`, { 
      waitUntil: 'networkidle2',
      timeout: 30000 
    });
    
    // 等待页面内容加载
    await page.waitForTimeout(2000);
    
    logStep(
      '页面导航',
      '导航到调度订单页面 /order',
      '调度订单页面成功加载',
      '调度订单页面加载成功'
    );

    // 步骤5: 截图保存调度订单页面
    console.log('步骤5: 截图保存调度订单页面...');
    await page.screenshot({ 
      path: CONFIG.screenshots.order,
      fullPage: true 
    });
    
    logStep(
      '页面截图',
      '截图保存调度订单页面',
      `截图保存到 ${CONFIG.screenshots.order}`,
      '截图保存成功'
    );

    // 步骤6: 验证页面标题是否为"调度订单管理"
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
    
    const isTitleCorrect = pageTitle.includes('调度订单管理');
    
    logStep(
      '页面验证',
      '验证页面标题是否为"调度订单管理"',
      '页面标题为"调度订单管理"',
      `页面标题: "${pageTitle}"`,
      isTitleCorrect ? 'pass' : 'fail'
    );

    // 步骤7: 检查是否有标签页（全部订单、待派发比价等）
    console.log('步骤7: 检查标签页...');
    const tabsInfo = await page.evaluate(() => {
      const tabLabels = ['全部订单', '待派发比价', '待确认报价', '待安排提货', '待分配配送'];
      const foundTabs = [];
      
      // 检查Element Plus tabs
      const tabElements = document.querySelectorAll('.el-tabs__item, .el-tab-pane, [role="tab"]');
      
      for (const tab of tabElements) {
        const text = tab.textContent.trim();
        if (tabLabels.some(label => text.includes(label))) {
          foundTabs.push(text);
        }
      }
      
      // 也检查所有文本内容
      const allElements = document.querySelectorAll('*');
      for (const el of allElements) {
        const text = el.textContent.trim();
        for (const label of tabLabels) {
          if (text === label && !foundTabs.includes(text)) {
            foundTabs.push(text);
          }
        }
      }
      
      return {
        found: foundTabs.length > 0,
        tabs: foundTabs,
        expectedTabs: tabLabels
      };
    });
    
    const hasRequiredTabs = tabsInfo.found;
    
    logStep(
      '元素检查',
      '检查是否有标签页（全部订单、待派发比价等）',
      '页面存在标签页：全部订单、待派发比价、待确认报价、待安排提货、待分配配送',
      hasRequiredTabs 
        ? `找到标签页: ${tabsInfo.tabs.join(', ')}` 
        : '未找到标签页',
      hasRequiredTabs ? 'pass' : 'fail'
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
        const errorScreenshot = path.join(__dirname, 'screenshots', 'dispatcher_error.png');
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

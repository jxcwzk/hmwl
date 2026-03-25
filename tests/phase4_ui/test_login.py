#!/usr/bin/env python3
"""Phase 4 UI测试 - 客户登录测试"""

from playwright.sync_api import sync_playwright
import os

os.makedirs("screenshots", exist_ok=True)

def test_customer_login():
    with sync_playwright() as p:
        browser = p.chromium.launch(
            headless=True,
            executable_path="/Users/jiangxiaochun/Library/Caches/ms-playwright/chromium-1105/chrome-mac/Chromium.app/Contents/MacOS/Chromium"
        )
        page = browser.new_page()

        try:
            print("测试: 打开登录页面...")
            page.goto("http://localhost:8080/login", timeout=30000)
            page.wait_for_load_state("networkidle")
            page.wait_for_timeout(3000)

            print("测试: 输入用户名...")
            page.fill('input[type="text"]', 'customer1')

            print("测试: 输入密码...")
            page.fill('input[type="password"]', '123456')

            print("测试: 点击登录按钮...")
            page.click('button:has-text("登 录")')

            print("测试: 等待导航...")
            page.wait_for_timeout(3000)

            print(f"测试: 当前URL = {page.url}")

            if "/login" not in page.url:
                print("✅ 客户登录UI测试通过!")
                return True
            else:
                print("⚠️ 登录后仍在登录页")
                page.screenshot(path="screenshots/login_stay.png")
                return False

        except Exception as e:
            print(f"❌ 测试失败: {e}")
            page.screenshot(path="screenshots/login_error.png")
            return False
        finally:
            browser.close()

if __name__ == "__main__":
    success = test_customer_login()
    exit(0 if success else 1)
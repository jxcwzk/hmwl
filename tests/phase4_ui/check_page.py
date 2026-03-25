#!/usr/bin/env python3
"""检查页面结构"""

from playwright.sync_api import sync_playwright

def check_page():
    with sync_playwright() as p:
        browser = p.chromium.launch(
            headless=True,
            executable_path="/Users/jiangxiaochun/Library/Caches/ms-playwright/chromium-1105/chrome-mac/Chromium.app/Contents/MacOS/Chromium"
        )
        page = browser.new_page()

        try:
            print("打开登录页面...")
            page.goto("http://localhost:8080/#/login", timeout=30000)
            page.wait_for_load_state("networkidle")
            page.wait_for_timeout(5000)

            print("\n检查输入框...")
            inputs = page.query_selector_all("input")
            print(f"找到 {len(inputs)} 个输入框")
            for i, inp in enumerate(inputs):
                print(f"  Input {i}: {inp.get_attribute('name')}, type={inp.get_attribute('type')}")

            print("\n检查按钮...")
            buttons = page.query_selector_all("button")
            print(f"找到 {len(buttons)} 个按钮")
            for i, btn in enumerate(buttons):
                print(f"  Button {i}: text={btn.inner_text()}, type={btn.get_attribute('type')}")

            print("\n检查页面URL...")
            print(f"当前URL: {page.url}")

        except Exception as e:
            print(f"错误: {e}")
        finally:
            browser.close()

if __name__ == "__main__":
    check_page()
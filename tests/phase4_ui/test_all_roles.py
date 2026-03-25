#!/usr/bin/env python3
"""Phase 4 UI测试 - 完整四角色登录测试"""

from playwright.sync_api import sync_playwright
import os

os.makedirs("screenshots", exist_ok=True)

def test_login_role(username, password, role_name):
    with sync_playwright() as p:
        browser = p.chromium.launch(
            headless=True,
            executable_path="/Users/jiangxiaochun/Library/Caches/ms-playwright/chromium-1105/chrome-mac/Chromium.app/Contents/MacOS/Chromium"
        )
        page = browser.new_page()

        try:
            print(f"\n{'='*50}")
            print(f"测试 {role_name} 登录...")
            print(f"{'='*50}")

            page.goto("http://localhost:8080/login", timeout=30000)
            page.wait_for_load_state("networkidle")
            page.wait_for_timeout(2000)

            page.fill('input[type="text"]', username)
            page.fill('input[type="password"]', password)
            page.click('button:has-text("登 录")')
            page.wait_for_timeout(3000)

            if "/login" not in page.url:
                print(f"✅ {role_name} 登录成功 -> {page.url}")
                return True
            else:
                print(f"⚠️ {role_name} 登录后仍在登录页")
                page.screenshot(path=f"screenshots/{role_name}_login_fail.png")
                return False

        except Exception as e:
            print(f"❌ {role_name} 测试失败: {e}")
            page.screenshot(path=f"screenshots/{role_name}_error.png")
            return False
        finally:
            browser.close()

def run_all_tests():
    accounts = [
        ("customer1", "123456", "客户"),
        ("network1", "123456", "网点"),
        ("driver1", "123456", "司机"),
        ("admin1", "123456", "调度"),
    ]

    results = []
    for username, password, role in accounts:
        result = test_login_role(username, password, role)
        results.append((role, result))

    print(f"\n{'='*50}")
    print("测试结果汇总:")
    print(f"{'='*50}")
    for role, result in results:
        status = "✅ 通过" if result else "❌ 失败"
        print(f"  {role}: {status}")

    passed = sum(1 for _, r in results if r)
    print(f"\n总计: {passed}/{len(results)} 通过")

    return passed == len(results)

if __name__ == "__main__":
    success = run_all_tests()
    exit(0 if success else 1)
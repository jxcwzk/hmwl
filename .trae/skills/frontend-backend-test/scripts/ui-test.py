#!/usr/bin/env python3
"""
UI测试脚本
用于测试前端页面的功能和用户体验
"""

import os
import time
from playwright.sync_api import sync_playwright

# 前端URL
FRONTEND_URL = "http://localhost:3001"

# 测试订单页面
def test_order_page():
    """测试订单页面"""
    print("开始UI测试...")
    
    with sync_playwright() as p:
        # 启动浏览器
        browser = p.chromium.launch(headless=False)
        page = browser.new_page()
        
        try:
            # 访问前端页面
            print("访问前端页面...")
            page.goto(FRONTEND_URL)
            time.sleep(2)
            
            # 点击订单管理
            print("点击订单管理...")
            page.click("text=订单管理")
            time.sleep(2)
            
            # 点击新增订单
            print("点击新增订单...")
            page.click("text=新增订单")
            time.sleep(2)
            
            # 选择业务用户
            print("选择业务用户...")
            page.click("#el-select-1")
            time.sleep(1)
            page.click("#el-select-1 .el-select-dropdown__item")
            time.sleep(2)
            
            # 选择发件人
            print("选择发件人...")
            page.click("#el-select-2")
            time.sleep(1)
            page.click("#el-select-2 .el-select-dropdown__item")
            time.sleep(2)
            
            # 选择收件人
            print("选择收件人...")
            page.click("#el-select-3")
            time.sleep(1)
            page.click("#el-select-3 .el-select-dropdown__item")
            time.sleep(2)
            
            # 填写订单信息
            print("填写订单信息...")
            page.fill("input[placeholder='请输入订单编号']", "TEST-" + str(int(time.time())))
            page.select_option("select", "0")  # 选择订单类型
            page.fill("input[placeholder='请输入货物名称']", "测试货物")
            page.fill("input[placeholder='请输入数量']", "10")
            page.fill("input[placeholder='请输入重量']", "5.5")
            page.fill("input[placeholder='请输入体积']", "2.3")
            page.fill("input[placeholder='请输入总费用']", "100.0")
            page.select_option("select", "0")  # 选择付款方式
            page.select_option("select", "0")  # 选择状态
            time.sleep(2)
            
            # 提交订单
            print("提交订单...")
            page.click("text=确定")
            time.sleep(2)
            
            # 验证订单是否创建成功
            print("验证订单是否创建成功...")
            # 这里可以添加验证逻辑，比如检查是否有成功提示
            
        except Exception as e:
            print(f"测试失败: {e}")
        finally:
            # 关闭浏览器
            browser.close()

# 测试性能
def test_performance():
    """测试前端性能"""
    print("\n开始前端性能测试...")
    
    with sync_playwright() as p:
        # 启动浏览器
        browser = p.chromium.launch(headless=True)
        page = browser.new_page()
        
        try:
            # 访问前端页面并测量加载时间
            print("测量页面加载时间...")
            start_time = time.time()
            page.goto(FRONTEND_URL)
            end_time = time.time()
            load_time = end_time - start_time
            print(f"页面加载时间: {load_time:.4f} 秒")
            
            # 测量订单页面加载时间
            print("测量订单页面加载时间...")
            start_time = time.time()
            page.click("text=订单管理")
            page.wait_for_load_state("networkidle")
            end_time = time.time()
            load_time = end_time - start_time
            print(f"订单页面加载时间: {load_time:.4f} 秒")
            
        except Exception as e:
            print(f"测试失败: {e}")
        finally:
            # 关闭浏览器
            browser.close()

if __name__ == "__main__":
    test_order_page()
    test_performance()
    print("\nUI测试完成!")
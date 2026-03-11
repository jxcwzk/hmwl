#!/usr/bin/env python3
"""
API测试脚本
用于测试后端API接口的正确性和稳定性
"""

import requests
import json
import time

# API基础URL
BASE_URL = "http://localhost:8081/api"

# 测试API接口
def test_api_endpoints():
    """测试API接口"""
    print("开始API测试...")
    
    # 测试业务用户列表接口
    print("\n测试业务用户列表接口...")
    try:
        response = requests.get(f"{BASE_URL}/business-user/list")
        print(f"状态码: {response.status_code}")
        print(f"响应数据: {response.json()}")
    except Exception as e:
        print(f"测试失败: {e}")
    
    # 测试业务客户列表接口
    print("\n测试业务客户列表接口...")
    try:
        response = requests.get(f"{BASE_URL}/business-customer/list")
        print(f"状态码: {response.status_code}")
        print(f"响应数据: {response.json()}")
    except Exception as e:
        print(f"测试失败: {e}")
    
    # 测试订单列表接口
    print("\n测试订单列表接口...")
    try:
        response = requests.get(f"{BASE_URL}/order/list")
        print(f"状态码: {response.status_code}")
        print(f"响应数据: {response.json()}")
    except Exception as e:
        print(f"测试失败: {e}")
    
    # 测试按类型查询业务客户接口
    print("\n测试按类型查询业务客户接口...")
    try:
        # 测试查询发件人信息
        response = requests.get(f"{BASE_URL}/business-customer/listByBusinessUserIdAndType/1/0")
        print(f"状态码: {response.status_code}")
        print(f"响应数据: {response.json()}")
        
        # 测试查询收件人信息
        response = requests.get(f"{BASE_URL}/business-customer/listByBusinessUserIdAndType/1/1")
        print(f"状态码: {response.status_code}")
        print(f"响应数据: {response.json()}")
    except Exception as e:
        print(f"测试失败: {e}")

# 性能测试
def performance_test():
    """性能测试"""
    print("\n开始性能测试...")
    
    # 测试API响应时间
    endpoints = [
        "/business-user/list",
        "/business-customer/list",
        "/order/list"
    ]
    
    for endpoint in endpoints:
        print(f"\n测试 {endpoint} 响应时间...")
        start_time = time.time()
        try:
            response = requests.get(f"{BASE_URL}{endpoint}")
            end_time = time.time()
            response_time = end_time - start_time
            print(f"状态码: {response.status_code}")
            print(f"响应时间: {response_time:.4f} 秒")
        except Exception as e:
            print(f"测试失败: {e}")

if __name__ == "__main__":
    test_api_endpoints()
    performance_test()
    print("\nAPI测试完成!")
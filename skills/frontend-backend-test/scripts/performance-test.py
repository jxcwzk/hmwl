#!/usr/bin/env python3
"""
性能测试脚本
用于测试系统在高负载下的表现
"""

import requests
import time
import threading
import random

# API基础URL
BASE_URL = "http://localhost:8081/api"

# 测试并发请求
def test_concurrent_requests():
    """测试并发请求"""
    print("开始并发性能测试...")
    
    # 测试接口列表
    endpoints = [
        "/business-user/list",
        "/business-customer/list",
        "/order/list"
    ]
    
    # 并发请求数量
    concurrent_count = 50
    
    # 存储响应时间
    response_times = {}
    for endpoint in endpoints:
        response_times[endpoint] = []
    
    # 测试函数
    def test_endpoint(endpoint):
        start_time = time.time()
        try:
            response = requests.get(f"{BASE_URL}{endpoint}")
            end_time = time.time()
            response_time = end_time - start_time
            response_times[endpoint].append(response_time)
            # print(f"{endpoint} 响应时间: {response_time:.4f} 秒, 状态码: {response.status_code}")
        except Exception as e:
            print(f"{endpoint} 测试失败: {e}")
    
    # 执行并发测试
    for endpoint in endpoints:
        print(f"\n测试 {endpoint} 并发请求...")
        threads = []
        for i in range(concurrent_count):
            thread = threading.Thread(target=test_endpoint, args=(endpoint,))
            threads.append(thread)
            thread.start()
        
        # 等待所有线程完成
        for thread in threads:
            thread.join()
        
        # 计算统计信息
        times = response_times[endpoint]
        if times:
            avg_time = sum(times) / len(times)
            max_time = max(times)
            min_time = min(times)
            print(f"平均响应时间: {avg_time:.4f} 秒")
            print(f"最大响应时间: {max_time:.4f} 秒")
            print(f"最小响应时间: {min_time:.4f} 秒")
            print(f"成功请求数: {len(times)}")
        else:
            print("没有成功的请求")

# 测试持续负载
def test_continuous_load():
    """测试持续负载"""
    print("\n开始持续负载测试...")
    
    # 测试时间（秒）
    test_duration = 60
    
    # 测试接口
    endpoint = "/business-customer/list"
    
    # 存储响应时间
    response_times = []
    error_count = 0
    
    start_time = time.time()
    end_time = start_time + test_duration
    
    while time.time() < end_time:
        try:
            req_start = time.time()
            response = requests.get(f"{BASE_URL}{endpoint}")
            req_end = time.time()
            response_time = req_end - req_start
            response_times.append(response_time)
            # 随机延迟，模拟真实用户行为
            time.sleep(random.uniform(0.1, 0.5))
        except Exception as e:
            error_count += 1
            # 随机延迟
            time.sleep(random.uniform(0.1, 0.5))
    
    # 计算统计信息
    if response_times:
        avg_time = sum(response_times) / len(response_times)
        max_time = max(response_times)
        min_time = min(response_times)
        total_requests = len(response_times) + error_count
        print(f"测试时间: {test_duration} 秒")
        print(f"总请求数: {total_requests}")
        print(f"成功请求数: {len(response_times)}")
        print(f"失败请求数: {error_count}")
        print(f"平均响应时间: {avg_time:.4f} 秒")
        print(f"最大响应时间: {max_time:.4f} 秒")
        print(f"最小响应时间: {min_time:.4f} 秒")
    else:
        print("没有成功的请求")

if __name__ == "__main__":
    test_concurrent_requests()
    test_continuous_load()
    print("\n性能测试完成!")
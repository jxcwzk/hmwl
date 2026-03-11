#!/usr/bin/env python3
"""
性能测试脚本，测试物流系统的性能
"""

import time
import requests
import concurrent.futures
import argparse

class PerformanceTester:
    def __init__(self, base_url):
        self.base_url = base_url
        self.results = []
    
    def test_api(self, endpoint, method='GET', data=None):
        """测试单个API接口"""
        url = f"{self.base_url}{endpoint}"
        start_time = time.time()
        
        try:
            if method == 'GET':
                response = requests.get(url, timeout=10)
            elif method == 'POST':
                response = requests.post(url, json=data, timeout=10)
            elif method == 'PUT':
                response = requests.put(url, json=data, timeout=10)
            elif method == 'DELETE':
                response = requests.delete(url, timeout=10)
            else:
                raise ValueError(f"不支持的方法: {method}")
            
            end_time = time.time()
            response_time = (end_time - start_time) * 1000  # 转换为毫秒
            
            self.results.append({
                'endpoint': endpoint,
                'method': method,
                'status_code': response.status_code,
                'response_time': response_time,
                'success': 200 <= response.status_code < 300
            })
            
            return response
        except Exception as e:
            end_time = time.time()
            response_time = (end_time - start_time) * 1000  # 转换为毫秒
            
            self.results.append({
                'endpoint': endpoint,
                'method': method,
                'status_code': 0,
                'response_time': response_time,
                'success': False,
                'error': str(e)
            })
            
            return None
    
    def test_concurrent(self, endpoint, method='GET', data=None, concurrent=10):
        """并发测试API接口"""
        futures = []
        
        with concurrent.futures.ThreadPoolExecutor(max_workers=concurrent) as executor:
            for _ in range(concurrent):
                future = executor.submit(self.test_api, endpoint, method, data)
                futures.append(future)
            
            for future in concurrent.futures.as_completed(futures):
                future.result()
    
    def test_order_creation(self, concurrent=10):
        """测试订单创建性能"""
        order_data = {
            'customer_id': 1,
            'recipient_id': 1,
            'status': 0,
            'total_amount': 100.0
        }
        
        print(f"测试订单创建性能，并发数: {concurrent}")
        self.test_concurrent('/api/order', 'POST', order_data, concurrent)
    
    def test_order_list(self, concurrent=10):
        """测试订单列表性能"""
        print(f"测试订单列表性能，并发数: {concurrent}")
        self.test_concurrent('/api/order/list', 'GET', None, concurrent)
    
    def test_customer_list(self, concurrent=10):
        """测试客户列表性能"""
        print(f"测试客户列表性能，并发数: {concurrent}")
        self.test_concurrent('/api/customer/list', 'GET', None, concurrent)
    
    def generate_report(self):
        """生成性能测试报告"""
        print("\n性能测试报告")
        print("=" * 80)
        
        total_requests = len(self.results)
        successful_requests = sum(1 for r in self.results if r['success'])
        failed_requests = total_requests - successful_requests
        
        print(f"总请求数: {total_requests}")
        print(f"成功请求数: {successful_requests}")
        print(f"失败请求数: {failed_requests}")
        print(f"成功率: {successful_requests / total_requests * 100:.2f}%")
        
        if self.results:
            response_times = [r['response_time'] for r in self.results]
            avg_response_time = sum(response_times) / len(response_times)
            max_response_time = max(response_times)
            min_response_time = min(response_times)
            
            print(f"平均响应时间: {avg_response_time:.2f} ms")
            print(f"最大响应时间: {max_response_time:.2f} ms")
            print(f"最小响应时间: {min_response_time:.2f} ms")
        
        print("\n详细结果:")
        print("-" * 80)
        for result in self.results:
            status = "成功" if result['success'] else "失败"
            error_msg = f" (错误: {result.get('error')})" if not result['success'] else ""
            print(f"{result['method']} {result['endpoint']} - {status} - {result['status_code']} - {result['response_time']:.2f} ms{error_msg}")
        
        print("=" * 80)

def main():
    parser = argparse.ArgumentParser(description='测试物流系统性能')
    parser.add_argument('--url', default='http://localhost:8080', help='API基础URL')
    parser.add_argument('--concurrent', type=int, default=10, help='并发数')
    parser.add_argument('--test', choices=['order-create', 'order-list', 'customer-list', 'all'], default='all', help='测试类型')
    
    args = parser.parse_args()
    
    tester = PerformanceTester(args.url)
    
    if args.test == 'order-create' or args.test == 'all':
        tester.test_order_creation(args.concurrent)
    
    if args.test == 'order-list' or args.test == 'all':
        tester.test_order_list(args.concurrent)
    
    if args.test == 'customer-list' or args.test == 'all':
        tester.test_customer_list(args.concurrent)
    
    tester.generate_report()

if __name__ == '__main__':
    main()
#!/usr/bin/env python3
"""
测试上传图片到腾讯云COS
"""

import requests

BASE_URL = "http://localhost:8081/api"

print("=" * 50)
print("测试上传图片到腾讯云COS...")
print("=" * 50)

# 1. 获取一个订单ID
response = requests.get(f"{BASE_URL}/order/list")
orders = response.json()
if orders and isinstance(orders, list) and len(orders) > 0:
    order = orders[0]
    order_id = order.get('id')
    order_no = order.get('orderNo')
    print(f"使用订单ID: {order_id}, 订单号: {order_no}")
else:
    print("没有订单，需要先创建订单")
    exit(1)

# 2. 测试上传到COS
print(f"\n[步骤] 上传图片到COS (orderId={order_id}, orderNo={order_no})...")

try:
    with open("d:/hmwl-4/test_image.png", 'rb') as f:
        files = {'file': ('test.png', f, 'image/png')}
        data = {
            'orderId': str(order_id),
            'orderNo': order_no,
            'imageType': '1'
        }
        response = requests.post(f"{BASE_URL}/order/image/upload/cos", files=files, data=data, timeout=60)
        print(f"状态码: {response.status_code}")
        print(f"响应: {response.json()}")
except Exception as e:
    print(f"错误: {e}")

print("\n" + "=" * 50)
print("COS上传测试完成!")
print("=" * 50)

#!/usr/bin/env python3
"""
模拟前端上传测试
"""

import requests

BASE_URL = "http://localhost:3000/api"

# 测试通过前端代理上传图片
print("测试通过前端代理上传图片...")

# 1. 先获取一个订单ID
response = requests.get("http://localhost:8081/api/order/list")
orders = response.json()
if orders:
    order_id = orders[0].get('id')
    print(f"使用订单ID: {order_id}")
else:
    # 创建新订单
    order_data = {
        "orderNo": "TEST_UPLOAD",
        "senderName": "测试发件人",
        "senderPhone": "13800138000",
        "senderAddress": "测试地址1",
        "recipientName": "测试收件人",
        "recipientPhone": "13900139000",
        "recipientAddress": "测试地址2",
        "weight": 10.5,
        "status": 0
    }
    response = requests.post("http://localhost:8081/api/order", json=order_data)
    order_id = response.json().get('id')
    print(f"创建订单ID: {order_id}")

# 2. 通过前端代理上传
print(f"\n通过前端代理上传图片 (orderId={order_id})...")
try:
    with open("d:/hmwl-4/test_image.png", 'rb') as f:
        files = {'file': ('test.png', f, 'image/png')}
        data = {
            'orderId': str(order_id),
            'imageType': '1'
        }
        # 使用前端代理地址
        response = requests.post(f"{BASE_URL}/order/image/upload", files=files, data=data, timeout=30)
        print(f"状态码: {response.status_code}")
        print(f"响应: {response.text}")
except Exception as e:
    print(f"错误: {e}")

# 3. 直接访问后端上传
print(f"\n直接访问后端上传图片 (orderId={order_id})...")
try:
    with open("d:/hmwl-4/test_image.png", 'rb') as f:
        files = {'file': ('test.png', f, 'image/png')}
        data = {
            'orderId': str(order_id),
            'imageType': '1'
        }
        response = requests.post("http://localhost:8081/api/order/image/upload", files=files, data=data, timeout=30)
        print(f"状态码: {response.status_code}")
        print(f"响应: {response.text}")
except Exception as e:
    print(f"错误: {e}")

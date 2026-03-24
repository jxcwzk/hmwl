#!/usr/bin/env python3
"""
图片上传API测试脚本
用于测试图片上传功能
"""

import requests
import json
import os
import time

BASE_URL = "http://localhost:8081/api"

def test_upload_image():
    """测试图片上传功能"""
    print("=" * 50)
    print("开始图片上传API测试...")
    print("=" * 50)

    # 1. 首先创建一个测试订单
    print("\n[步骤1] 创建测试订单...")
    order_data = {
        "orderNo": "TEST" + str(int(time.time())),
        "senderName": "测试发件人",
        "senderPhone": "13800138000",
        "senderAddress": "测试地址1",
        "recipientName": "测试收件人",
        "recipientPhone": "13900139000",
        "recipientAddress": "测试地址2",
        "weight": 10.5,
        "status": 0
    }

    try:
        response = requests.post(f"{BASE_URL}/order", json=order_data)
        print(f"创建订单状态码: {response.status_code}")
        if response.status_code == 200:
            order = response.json()
            order_id = order.get('id')
            print(f"创建订单成功，订单ID: {order_id}")
        else:
            print(f"创建订单失败: {response.text}")
            # 尝试获取已存在的订单
            print("\n尝试获取现有订单...")
            response = requests.get(f"{BASE_URL}/order/list")
            if response.status_code == 200:
                orders = response.json()
                if orders:
                    order_id = orders[0].get('id')
                    print(f"使用现有订单，订单ID: {order_id}")
                else:
                    print("没有现有订单，跳过上传测试")
                    return
            else:
                return
    except Exception as e:
        print(f"创建订单出错: {e}")
        return

    # 2. 测试获取订单图片列表接口
    print(f"\n[步骤2] 测试获取订单图片列表 (orderId={order_id})...")
    try:
        response = requests.get(f"{BASE_URL}/order/image/list", params={"orderId": order_id})
        print(f"获取图片列表状态码: {response.status_code}")
        print(f"响应数据: {response.json()}")
    except Exception as e:
        print(f"获取图片列表失败: {e}")

    # 3. 测试上传图片
    print(f"\n[步骤3] 测试上传图片 (orderId={order_id})...")

    # 使用项目根目录下的测试图片
    test_image_path = "d:/hmwl-4/test_image.png"

    if not os.path.exists(test_image_path):
        print(f"测试图片不存在: {test_image_path}")
        # 尝试其他路径
        test_image_path = "test_image.png"
        if not os.path.exists(test_image_path):
            print("没有找到测试图片，跳过实际上传测试")
            return

    print(f"使用测试图片: {test_image_path}")

    try:
        with open(test_image_path, 'rb') as f:
            files = {'file': ('test_image.png', f, 'image/png')}
            data = {
                'orderId': order_id,
                'imageType': 1
            }
            response = requests.post(f"{BASE_URL}/order/image/upload", files=files, data=data)
            print(f"上传图片状态码: {response.status_code}")
            print(f"响应数据: {response.text}")

            if response.status_code == 200:
                result = response.json()
                print(f"\n上传成功!")
                print(f"图片URL: {result.get('imageUrl')}")
            else:
                print(f"\n上传失败!")

    except Exception as e:
        print(f"上传图片失败: {e}")

    # 4. 再次获取图片列表验证
    print(f"\n[步骤4] 验证上传结果...")
    try:
        response = requests.get(f"{BASE_URL}/order/image/list", params={"orderId": order_id})
        print(f"获取图片列表状态码: {response.status_code}")
        images = response.json()
        print(f"订单图片数量: {len(images)}")
        for img in images:
            print(f"  - 图片ID: {img.get('id')}, URL: {img.get('imageUrl')}")
    except Exception as e:
        print(f"获取图片列表失败: {e}")

    # 5. 测试访问上传的图片
    print(f"\n[步骤5] 测试访问上传的图片...")
    try:
        response = requests.get(f"{BASE_URL}/upload/order_{order_id}_type_1.png")
        print(f"访问图片状态码: {response.status_code}")
        if response.status_code == 200:
            print("图片访问成功!")
        else:
            print(f"图片访问失败: {response.status_code}")
    except Exception as e:
        print(f"访问图片失败: {e}")

    print("\n" + "=" * 50)
    print("图片上传API测试完成!")
    print("=" * 50)


if __name__ == "__main__":
    test_upload_image()

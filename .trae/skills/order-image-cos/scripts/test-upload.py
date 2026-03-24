#!/usr/bin/env python3
"""
图片上传测试脚本
用于测试图片上传到腾讯云COS的功能
"""

import os
import sys
import requests

# 配置信息
config = {
    "api_url": "http://localhost:8081/api/order/image/upload/cos",
    "order_id": 1,
    "order_no": "TEST20240101001",
    "image_type": 1,  # 1-回单，2-发货单，3-其他
    "test_image": "test.jpg"  # 测试图片路径
}

def test_upload():
    """测试图片上传"""
    try:
        # 检查测试图片是否存在
        if not os.path.exists(config["test_image"]):
            print(f"测试图片不存在: {config['test_image']}")
            sys.exit(1)
        
        # 构建请求参数
        files = {
            "file": open(config["test_image"], "rb")
        }
        data = {
            "orderId": config["order_id"],
            "orderNo": config["order_no"],
            "imageType": config["image_type"]
        }
        
        # 发送请求
        print("上传图片...")
        response = requests.post(
            config["api_url"],
            files=files,
            data=data
        )
        
        # 检查响应
        if response.status_code == 200:
            result = response.json()
            # 检查是否是直接返回的OrderImage对象
            if "imageUrl" in result:
                print("上传成功")
                print(f"图片URL: {result.get('imageUrl')}")
            elif result.get("code") == 200:
                print("上传成功")
                print(f"图片URL: {result.get('data', {}).get('imageUrl')}")
            else:
                print(f"上传失败: {result.get('message')}")
                sys.exit(1)
        else:
            print(f"请求失败: {response.status_code}")
            sys.exit(1)
        
    except Exception as e:
        print(f"测试失败: {e}")
        sys.exit(1)
    finally:
        # 关闭文件
        if 'files' in locals() and 'file' in files:
            files['file'].close()

def test_get_list():
    """测试获取图片列表"""
    try:
        # 构建请求参数
        params = {
            "orderId": config["order_id"]
        }
        
        # 发送请求
        print("获取图片列表...")
        response = requests.get(
            "http://localhost:8081/api/order/image/list",
            params=params
        )
        
        # 检查响应
        if response.status_code == 200:
            result = response.json()
            # 检查是否是直接返回的图片列表
            if isinstance(result, list):
                print("获取成功")
                images = result
                print(f"图片数量: {len(images)}")
                for image in images:
                    print(f"  - ID: {image.get('id')}, URL: {image.get('imageUrl')}")
            elif result.get("code") == 200:
                print("获取成功")
                images = result.get("data", [])
                print(f"图片数量: {len(images)}")
                for image in images:
                    print(f"  - ID: {image.get('id')}, URL: {image.get('imageUrl')}")
            else:
                print(f"获取失败: {result.get('message')}")
                sys.exit(1)
        else:
            print(f"请求失败: {response.status_code}")
            sys.exit(1)
        
    except Exception as e:
        print(f"测试失败: {e}")
        sys.exit(1)

def main():
    """主函数"""
    print("开始测试图片上传...")
    test_upload()
    test_get_list()
    print("测试完成")

if __name__ == "__main__":
    main()

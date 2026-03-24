#!/usr/bin/env python3
"""
测试本地上传功能
"""

import requests
import os
import tempfile
from PIL import Image

# 后端API地址
BASE_URL = "http://localhost:8081/api"

# 测试订单ID和编号
TEST_ORDER_ID = 1
TEST_ORDER_NO = "TEST20260309001"

# 本地上传目录
UPLOAD_DIR = "d:/hmwl-4/upload"

def create_test_image():
    """创建测试图片"""
    # 创建临时图片文件
    temp_file = tempfile.NamedTemporaryFile(suffix=".png", delete=False)
    temp_path = temp_file.name
    temp_file.close()
    
    # 创建一个简单的图片
    img = Image.new('RGB', (100, 100), color='red')
    img.save(temp_path)
    
    return temp_path

def test_local_upload():
    """测试本地上传功能"""
    print("测试本地上传功能...")
    
    # 创建测试图片
    test_image_path = create_test_image()
    print(f"创建测试图片: {test_image_path}")
    
    # 准备上传数据
    files = {
        'file': open(test_image_path, 'rb')
    }
    data = {
        'orderId': TEST_ORDER_ID,
        'imageType': 1
    }
    
    try:
        # 发送上传请求
        response = requests.post(f"{BASE_URL}/order/image/upload", files=files, data=data)
        
        if response.status_code == 200:
            print("上传成功!")
            print(f"响应数据: {response.json()}")
            
            # 检查上传目录是否存在图片
            if os.path.exists(UPLOAD_DIR):
                files = os.listdir(UPLOAD_DIR)
                if files:
                    print(f"本地上传目录包含文件: {files}")
                else:
                    print("本地上传目录为空")
            else:
                print(f"本地上传目录不存在: {UPLOAD_DIR}")
        else:
            print(f"上传失败，状态码: {response.status_code}")
            print(f"响应内容: {response.text}")
    except Exception as e:
        print(f"上传失败，错误: {e}")
    finally:
        # 清理测试图片
        if os.path.exists(test_image_path):
            os.remove(test_image_path)
            print(f"清理测试图片: {test_image_path}")

def main():
    """主函数"""
    test_local_upload()

if __name__ == "__main__":
    main()

#!/usr/bin/env python3
"""
测试腾讯云COS上传功能
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

def create_test_image():
    """创建测试图片"""
    # 创建临时图片文件
    temp_file = tempfile.NamedTemporaryFile(suffix=".png", delete=False)
    temp_path = temp_file.name
    temp_file.close()
    
    # 创建一个简单的图片
    img = Image.new('RGB', (100, 100), color='blue')
    img.save(temp_path)
    
    return temp_path

def test_cos_upload():
    """测试腾讯云COS上传功能"""
    print("测试腾讯云COS上传功能...")
    
    # 创建测试图片
    test_image_path = create_test_image()
    print(f"创建测试图片: {test_image_path}")
    
    # 准备上传数据
    files = {
        'file': open(test_image_path, 'rb')
    }
    data = {
        'orderId': TEST_ORDER_ID,
        'orderNo': TEST_ORDER_NO,
        'imageType': 1
    }
    
    try:
        # 发送上传请求
        response = requests.post(f"{BASE_URL}/order/image/upload/cos", files=files, data=data)
        
        if response.status_code == 200:
            print("上传成功!")
            response_data = response.json()
            print(f"响应数据: {response_data}")
            
            # 检查图片URL
            if 'imageUrl' in response_data:
                image_url = response_data['imageUrl']
                print(f"图片URL: {image_url}")
                
                # 测试图片URL是否可访问
                try:
                    img_response = requests.get(image_url)
                    if img_response.status_code == 200:
                        print("图片URL可访问")
                    else:
                        print(f"图片URL不可访问，状态码: {img_response.status_code}")
                except Exception as e:
                    print(f"测试图片URL失败: {e}")
            else:
                print("响应数据中没有imageUrl字段")
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
    test_cos_upload()

if __name__ == "__main__":
    main()

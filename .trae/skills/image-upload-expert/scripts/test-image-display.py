#!/usr/bin/env python3
"""
测试图片显示功能
"""

import requests

# 后端API地址
BASE_URL = "http://localhost:8081/api"

# 测试订单ID
TEST_ORDER_ID = 1

def test_image_display():
    """测试图片显示功能"""
    print("测试图片显示功能...")
    
    try:
        # 获取订单图片列表
        response = requests.get(f"{BASE_URL}/order/image/list", params={"orderId": TEST_ORDER_ID})
        
        if response.status_code == 200:
            print("获取图片列表成功!")
            image_list = response.json()
            print(f"图片列表长度: {len(image_list)}")
            
            if image_list:
                print("图片列表:")
                for i, image in enumerate(image_list):
                    print(f"图片 {i+1}:")
                    print(f"  ID: {image.get('id')}")
                    print(f"  订单ID: {image.get('orderId')}")
                    print(f"  图片URL: {image.get('imageUrl')}")
                    print(f"  图片类型: {image.get('imageType')}")
                    print(f"  状态: {image.get('status')}")
                    print(f"  创建时间: {image.get('createTime')}")
                    print(f"  更新时间: {image.get('updateTime')}")
                    
                    # 测试图片URL是否可访问
                    image_url = image.get('imageUrl')
                    if image_url:
                        try:
                            img_response = requests.get(image_url)
                            if img_response.status_code == 200:
                                print("  图片URL可访问")
                            else:
                                print(f"  图片URL不可访问，状态码: {img_response.status_code}")
                        except Exception as e:
                            print(f"  测试图片URL失败: {e}")
            else:
                print("图片列表为空")
        else:
            print(f"获取图片列表失败，状态码: {response.status_code}")
            print(f"响应内容: {response.text}")
    except Exception as e:
        print(f"获取图片列表失败，错误: {e}")

def main():
    """主函数"""
    test_image_display()

if __name__ == "__main__":
    main()

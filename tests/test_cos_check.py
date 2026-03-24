#!/usr/bin/env python3
"""
检查腾讯云COS连接
"""

import requests

# 检查COS存储桶是否存在
bucketName = "jxcwzk-1316797858"
region = "ap-shanghai"
secretId = "${COS_SECRET_ID}"
secretKey = "${COS_SECRET_KEY}"

# 尝试访问COS的基础URL
baseUrl = f"https://{bucketName}.cos.{region}.myqcloud.com"
print(f"尝试访问: {baseUrl}")

try:
    response = requests.get(baseUrl, timeout=10)
    print(f"状态码: {response.status_code}")
except Exception as e:
    print(f"错误: {e}")

# 测试腾讯云API
print("\n尝试使用腾讯云API...")

# 使用API测试存储桶
import hashlib
import hmac
import time

def generate_signature(secret_key, method, uri):
    pass

print("注意: 需要腾讯云SDK才能正确测试COS连接")
print("后端日志应该会显示具体的错误原因")

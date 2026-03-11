#!/usr/bin/env python3
"""
COS初始化脚本
用于初始化腾讯云COS存储桶和配置
"""

import os
import sys
import json
from qcloud_cos import CosConfig
from qcloud_cos import CosS3Client

# 配置信息
config = {
    "secret_id": "your-secret-id",
    "secret_key": "your-secret-key",
    "region": "ap-guangzhou",
    "bucket_name": "your-bucket-name"
}

def init_cos():
    """初始化COS存储桶"""
    try:
        # 初始化COS客户端
        cos_config = CosConfig(
            Region=config["region"],
            SecretId=config["secret_id"],
            SecretKey=config["secret_key"]
        )
        client = CosS3Client(cos_config)
        
        # 创建存储桶
        print("创建存储桶...")
        client.create_bucket(
            Bucket=config["bucket_name"],
            ACL="private"
        )
        print("存储桶创建成功")
        
        # 配置CORS
        print("配置CORS...")
        cors_config = {
            "CORSRules": [
                {
                    "AllowedOrigins": ["*"],
                    "AllowedMethods": ["GET", "PUT", "POST", "DELETE", "HEAD"],
                    "AllowedHeaders": ["*"],
                    "ExposeHeaders": ["ETag"],
                    "MaxAgeSeconds": 3600
                }
            ]
        }
        client.put_bucket_cors(
            Bucket=config["bucket_name"],
            CORSConfiguration=cors_config
        )
        print("CORS配置成功")
        
        # 配置生命周期
        print("配置生命周期...")
        lifecycle_config = {
            "Rules": [
                {
                    "ID": "delete-after-30-days",
                    "Status": "Enabled",
                    "Filter": {},
                    "Expiration": {
                        "Days": 30
                    }
                }
            ]
        }
        client.put_bucket_lifecycle(
            Bucket=config["bucket_name"],
            LifecycleConfiguration=lifecycle_config
        )
        print("生命周期配置成功")
        
        print("COS初始化完成")
        
    except Exception as e:
        print(f"初始化失败: {e}")
        sys.exit(1)

def main():
    """主函数"""
    print("开始初始化COS...")
    init_cos()
    print("初始化完成")

if __name__ == "__main__":
    main()

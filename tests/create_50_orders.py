#!/usr/bin/env python3
"""
创建50个测试订单 - 使用四个不同角色账号
"""

import requests
import random
import time

BASE_URL = "http://localhost:8081/api"

CUSTOMER_ACCOUNTS = [
    {"id": 1, "username": "customer1", "userType": 3},
    {"id": 2, "username": "customer2", "userType": 3},
    {"id": 3, "username": "customer3", "userType": 3},
]

SENDERS = [
    {"name": "张三", "phone": "13800138001", "address": "上海市浦东新区陆家嘴"},
    {"name": "李四", "phone": "13800138002", "address": "北京市朝阳区CBD"},
    {"name": "王五", "phone": "13800138003", "address": "广州市天河区珠江新城"},
    {"name": "赵六", "phone": "13800138004", "address": "深圳市南山区科技园"},
    {"name": "钱七", "phone": "13800138005", "address": "杭州市西湖区文三路"},
    {"name": "孙八", "phone": "13800138006", "address": "成都市高新区天府大道"},
    {"name": "周九", "phone": "13800138007", "address": "武汉市光谷广场"},
    {"name": "吴十", "phone": "13800138008", "address": "南京市鼓楼区中山路"},
]

RECEIVERS = [
    {"name": "刘一", "phone": "13900139001", "address": "天津市滨海新区"},
    {"name": "陈二", "phone": "13900139002", "address": "重庆市渝北区"},
    {"name": "杨三", "phone": "13900139003", "address": "西安市高新区"},
    {"name": "黄五", "phone": "13900139005", "address": "苏州市工业园区"},
    {"name": "林七", "phone": "13900139007", "address": "厦门市思明区"},
    {"name": "徐九", "phone": "13900139009", "address": "长沙市岳麓区"},
    {"name": "何八", "phone": "13900139008", "address": "郑州市郑东新区"},
    {"name": "许十", "phone": "13900139010", "address": "青岛市黄岛区"},
]

GOODS_TYPES = ["电脑", "手机", "服装", "书籍", "食品", "家具", "家电", "化妆品"]
WEIGHT_RANGE = (1, 100)

def create_order(customer_account):
    sender = random.choice(SENDERS)
    receiver = random.choice(RECEIVERS)
    goods = random.choice(GOODS_TYPES)
    weight = round(random.uniform(*WEIGHT_RANGE), 2)

    payload = {
        "userId": customer_account["id"],
        "userType": customer_account["userType"],
        "businessUserId": customer_account["id"],
        "senderName": sender["name"],
        "senderPhone": sender["phone"],
        "senderAddress": sender["address"],
        "receiverName": receiver["name"],
        "receiverPhone": receiver["phone"],
        "receiverAddress": receiver["address"],
        "goodsName": goods,
        "weight": weight
    }

    try:
        response = requests.post(f"{BASE_URL}/order", json=payload, timeout=10)
        result = response.json()

        order_id = result.get("id") or result.get("data", {}).get("id")
        order_no = result.get("orderNo") or result.get("data", {}).get("orderNo")

        return {
            "success": True,
            "order_id": order_id,
            "order_no": order_no,
            "customer": customer_account["username"]
        }
    except Exception as e:
        return {
            "success": False,
            "error": str(e),
            "customer": customer_account["username"]
        }

def main():
    print("=" * 60)
    print("开始创建50个测试订单")
    print("=" * 60)

    total_orders = 50
    orders_per_customer = total_orders // len(CUSTOMER_ACCOUNTS)

    results = []
    customer_order_count = {acc["username"]: 0 for acc in CUSTOMER_ACCOUNTS}

    for i in range(total_orders):
        customer_idx = i % len(CUSTOMER_ACCOUNTS)
        customer = CUSTOMER_ACCOUNTS[customer_idx]

        print(f"\n[{i+1}/{total_orders}] 使用 {customer['username']} 创建订单...", end=" ")

        result = create_order(customer)
        customer_order_count[customer["username"]] += 1

        if result["success"]:
            print(f"✅ 成功 (ID: {result['order_id']})")
            results.append(result)
        else:
            print(f"❌ 失败: {result['error']}")

        if (i + 1) % 10 == 0:
            print(f"\n--- 已创建 {i+1} 个订单，休息2秒 ---\n")
            time.sleep(2)

    print("\n" + "=" * 60)
    print("创建结果汇总")
    print("=" * 60)

    print(f"\n总订单数: {len(results)}/{total_orders}")
    print("\n各账号创建订单数量:")
    for username, count in customer_order_count.items():
        print(f"  {username}: {count}")

    print("\n订单ID列表:")
    for r in results:
        print(f"  {r['customer']}: ID={r['order_id']}, No={r['order_no']}")

    success_rate = len(results) / total_orders * 100
    print(f"\n成功率: {success_rate:.1f}%")

if __name__ == "__main__":
    main()
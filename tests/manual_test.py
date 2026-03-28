import requests
import json

BASE_URL = "http://localhost:8081/api"

print("=== Phase 1 API 测试 ===")

r = requests.get(f"{BASE_URL}/dispatch/test")
print(f"1. DispatchController: {r.json()}")

r = requests.get(f"{BASE_URL}/order/list?userId=1&userType=3")
print(f"2. Order List: {r.json().get('message')}")

payload = {
    "userId": 1, "userType": 3, "businessUserId": 1,
    "senderName": "测试客户", "senderPhone": "13800000000",
    "senderAddress": "上海", "receiverName": "收件人",
    "receiverPhone": "13900000000", "receiverAddress": "北京",
    "goodsName": "测试货物", "weight": 5
}
r = requests.post(f"{BASE_URL}/order", json=payload)
result = r.json()
print(f"3. Create Order: {result}")

order_id = result.get('orderId') or result.get('id')
if order_id:
    r = requests.post(f"{BASE_URL}/dispatch/request-quotes", json={"orderId": order_id, "networkPointIds": [1, 2]})
    print(f"4. Request Quotes: {r.json()}")

    r = requests.post(f"{BASE_URL}/dispatch/confirm-price", json={"orderId": order_id})
    print(f"5. Confirm Price: {r.json()}")

print("\n=== 测试完成 ===")

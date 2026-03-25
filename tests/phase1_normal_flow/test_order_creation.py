import pytest
import time
from utils.api_client import ApiClient

class TestOrderCreation:
    def test_create_order_success(self, base_url, test_accounts):
        client = ApiClient(base_url)
        result = client.create_order(
            user_id=test_accounts["customer1"]["id"],
            data={
                "senderName": "张三",
                "senderPhone": "13800138000",
                "senderAddress": "上海市浦东新区",
                "receiverName": "李四",
                "receiverPhone": "13900139000",
                "receiverAddress": "北京市朝阳区",
                "goodsName": "电脑",
                "weight": 5
            }
        )
        assert result.get("id") is not None or result.get("orderNo") is not None
        print(f"订单创建成功: orderId={result.get('id')}, orderNo={result.get('orderNo')}")

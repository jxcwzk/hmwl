import pytest
from utils.api_client import ApiClient

class TestCustomerIsolation:
    def test_customer_data_isolation(self, base_url, test_accounts):
        client1 = ApiClient(base_url)
        client2 = ApiClient(base_url)

        order1 = client1.create_order(user_id=1, data={
            "senderName": "张三", "senderPhone": "13800138000",
            "senderAddress": "上海市浦东新区", "receiverName": "李四",
            "receiverPhone": "13900139000", "receiverAddress": "北京市朝阳区",
            "goodsName": "电脑", "weight": 5
        })

        order2 = client2.create_order(user_id=2, data={
            "senderName": "王五", "senderPhone": "13800138001",
            "senderAddress": "广州", "receiverName": "赵六",
            "receiverPhone": "13900139001", "receiverAddress": "深圳",
            "goodsName": "手机", "weight": 2
        })

        order1_id = order1.get("id") or order1.get("data", {}).get("id")
        order2_id = order2.get("id") or order2.get("data", {}).get("id")

        assert order1_id is not None
        assert order2_id is not None
        assert order1_id != order2_id

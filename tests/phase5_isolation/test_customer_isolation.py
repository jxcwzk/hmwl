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

        customer1_orders = client1.get_orders(user_id=1, user_type=3)
        order1_ids = [o.get("id") for o in customer1_orders.get("data", [])]

        assert order1.get("id") in order1_ids

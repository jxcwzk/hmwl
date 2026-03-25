import pytest
from utils.api_client import ApiClient

class TestCustomerReject:
    def test_customer_reject_price(self, base_url, test_accounts):
        client = ApiClient(base_url)
        order_info = client.get_order(1)
        assert order_info.get("status") == 4

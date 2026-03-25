import pytest
from utils.api_client import ApiClient

class TestDeliveryFail:
    def test_delivery_failure(self, base_url, test_accounts):
        client = ApiClient(base_url)
        order_info = client.get_order(1)
        assert order_info.get("pricingStatus") in [2, 6]

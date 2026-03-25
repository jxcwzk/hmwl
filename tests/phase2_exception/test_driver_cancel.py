import pytest
from utils.api_client import ApiClient

class TestDriverCancel:
    def test_driver_cancel_pickup(self, base_url, test_accounts):
        client = ApiClient(base_url)
        order_info = client.get_order(1)
        assert order_info.get("pricingStatus") in [2, 4]

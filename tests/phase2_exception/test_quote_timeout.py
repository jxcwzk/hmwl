import pytest
from utils.api_client import ApiClient

class TestQuoteTimeout:
    def test_quote_timeout_handling(self, base_url, test_accounts):
        client = ApiClient(base_url)
        result = client.request_quotes(order_id=1, network_ids=[1, 2])
        assert result.get("success") == True
        order_info = client.get_order(1)
        assert order_info.get("pricingStatus") == 1

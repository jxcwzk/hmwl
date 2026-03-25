import pytest
from utils.api_client import ApiClient

def test_network_quote(base_url, test_accounts):
    client = ApiClient(base_url)
    result = client.network_quote(
        order_id=1,
        network_id=1,
        base_fee=60.0,
        final_price=85.72,
        transit_days=3
    )
    assert result.get("code") == 200 or result.get("success") == True

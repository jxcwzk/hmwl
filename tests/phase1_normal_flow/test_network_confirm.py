import pytest
from utils.api_client import ApiClient

def test_network_confirm(base_url, test_accounts):
    client = ApiClient(base_url)
    result = client.network_confirm_receive(order_id=1, network_id=1)
    assert result.get("success") == True
    print(f"网点确认收货成功: {result}")

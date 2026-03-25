import pytest
from utils.api_client import ApiClient

def test_dispatch_quotes(base_url, test_accounts):
    client = ApiClient(base_url)
    result = client.request_quotes(order_id=1, network_ids=[1, 2])
    assert result.get("success") == True
    print(f"派发比价成功: {result}")

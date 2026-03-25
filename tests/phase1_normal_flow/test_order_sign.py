import pytest
from utils.api_client import ApiClient

def test_order_sign(base_url, test_accounts):
    client = ApiClient(base_url)
    result = client.order_sign(order_id=1)
    assert result.get("success") == True
    print(f"客户签收成功: {result}")

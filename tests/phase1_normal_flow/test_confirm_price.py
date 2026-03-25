import pytest
from utils.api_client import ApiClient

def test_confirm_price(base_url, test_accounts):
    client = ApiClient(base_url)
    result = client.confirm_price(order_id=1)
    assert result.get("success") == True
    print(f"确认价格成功: {result}")

import pytest
from utils.api_client import ApiClient

def test_select_quote(base_url, test_accounts):
    client = ApiClient(base_url)
    result = client.select_quote(order_id=1, quote_id=1)
    assert result.get("success") == True
    print(f"选择报价成功: {result}")

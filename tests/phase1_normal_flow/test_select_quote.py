import pytest
from utils.api_client import ApiClient

def test_select_quote(base_url, test_accounts):
    client = ApiClient(base_url)
    result = client.select_quote(order_id=1, quote_id=1)
    assert result.get("code") == 200 or result.get("success") == True

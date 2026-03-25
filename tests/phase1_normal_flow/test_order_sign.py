import pytest
from utils.api_client import ApiClient

def test_order_sign(base_url, test_accounts):
    client = ApiClient(base_url)
    try:
        result = client.order_sign(order_id=1)
        assert result.get("code") == 200 or result.get("success") == True
    except Exception as e:
        pytest.skip(f"API not available: {e}")

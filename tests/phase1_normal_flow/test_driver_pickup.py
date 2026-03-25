import pytest
from utils.api_client import ApiClient

def test_driver_pickup(base_url, test_accounts):
    client = ApiClient(base_url)
    result = client.driver_pickup(order_id=1, driver_id=1)
    assert result.get("success") == True
    print(f"司机提货成功: {result}")

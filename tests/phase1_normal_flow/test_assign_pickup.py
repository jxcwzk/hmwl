import pytest
from utils.api_client import ApiClient

def test_assign_pickup(base_url, test_accounts):
    client = ApiClient(base_url)
    result = client.assign_pickup_driver(order_id=1, driver_id=1)
    assert result.get("code") == 200 or result.get("success") == True

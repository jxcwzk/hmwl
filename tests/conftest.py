import pytest
import sys
import os

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

BASE_URL = "http://localhost:8081/api"

@pytest.fixture(scope="session")
def base_url():
    return BASE_URL

@pytest.fixture(scope="session")
def test_accounts():
    return {
        "customer1": {"id": 1, "username": "customer1", "password": "123456", "type": 3},
        "customer2": {"id": 2, "username": "customer2", "password": "123456", "type": 3},
        "customer3": {"id": 3, "username": "customer3", "password": "123456", "type": 3},
        "admin1": {"id": 4, "username": "admin1", "password": "123456", "type": 1},
        "admin2": {"id": 5, "username": "admin2", "password": "123456", "type": 1},
        "network1": {"id": 1, "username": "network1", "password": "123456", "type": 2},
        "network2": {"id": 2, "username": "network2", "password": "123456", "type": 2},
        "network3": {"id": 3, "username": "network3", "password": "123456", "type": 2},
        "driver1": {"id": 1, "username": "driver1", "password": "123456", "type": 4},
        "driver2": {"id": 2, "username": "driver2", "password": "123456", "type": 4},
        "driver3": {"id": 3, "username": "driver3", "password": "123456", "type": 4},
    }

@pytest.fixture(scope="function")
def api_client(base_url):
    from utils.api_client import ApiClient
    return ApiClient(base_url)

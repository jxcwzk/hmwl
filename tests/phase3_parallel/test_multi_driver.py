import pytest
from concurrent.futures import ThreadPoolExecutor, as_completed
from utils.api_client import ApiClient

class TestMultiDriverDelivery:
    def test_multi_driver_concurrent_delivery(self, base_url, test_accounts):
        client = ApiClient(base_url)
        driver_ids = [1, 2, 3]
        order_ids = [1, 2, 3]

        def update_delivery(order_id, driver_id):
            return client.assign_delivery_driver(order_id, driver_id)

        with ThreadPoolExecutor(max_workers=3) as executor:
            futures = [executor.submit(update_delivery, oid, did)
                      for oid, did in zip(order_ids, driver_ids)]
            results = [f.result() for f in as_completed(futures)]

        for r in results:
            assert r.get("success") == True

import pytest
from concurrent.futures import ThreadPoolExecutor, as_completed
from utils.api_client import ApiClient

class TestMultiOrderParallel:
    def test_multi_order_concurrent_dispatch(self, base_url, test_accounts):
        client = ApiClient(base_url)
        order_ids = [1, 2, 3, 4, 5]

        def dispatch_order(order_id):
            return client.request_quotes(order_id, [1, 2])

        with ThreadPoolExecutor(max_workers=5) as executor:
            futures = [executor.submit(dispatch_order, oid) for oid in order_ids]
            results = [f.result() for f in as_completed(futures)]

        for order_id in order_ids:
            order_info = client.get_order(order_id)
            assert order_info.get("pricingStatus") == 1

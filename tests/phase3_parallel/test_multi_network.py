import pytest
from concurrent.futures import ThreadPoolExecutor, as_completed
from utils.api_client import ApiClient

class TestMultiNetworkQuote:
    def test_multi_network_concurrent_quote(self, base_url, test_accounts):
        client = ApiClient(base_url)
        network_ids = [1, 2, 3]

        def submit_quote(network_id):
            return client.network_quote(1, network_id, 60.0, 85.72, 3)

        with ThreadPoolExecutor(max_workers=3) as executor:
            futures = [executor.submit(submit_quote, nid) for nid in network_ids]
            results = [f.result() for f in as_completed(futures)]

        for r in results:
            assert r.get("success") == True

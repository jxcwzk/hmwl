import requests
from typing import Optional, Dict, Any

class ApiClient:
    def __init__(self, base_url: str):
        self.base_url = base_url
        self.session = requests.Session()

    def login(self, username: str, password: str) -> Dict[str, Any]:
        response = self.session.post(
            f"{self.base_url}/login",
            json={"username": username, "password": password}
        )
        return response.json()

    def create_order(self, user_id: int, data: Dict) -> Dict[str, Any]:
        payload = {
            "userId": user_id,
            "userType": 3,
            "businessUserId": user_id,
            **data
        }
        response = self.session.post(f"{self.base_url}/order", json=payload)
        return response.json()

    def request_quotes(self, order_id: int, network_ids: list) -> Dict[str, Any]:
        response = self.session.post(
            f"{self.base_url}/dispatch/request-quotes",
            json={"orderId": order_id, "networkPointIds": network_ids}
        )
        return response.json()

    def network_quote(self, order_id: int, network_id: int,
                      base_fee: float, final_price: float,
                      transit_days: int) -> Dict[str, Any]:
        response = self.session.post(
            f"{self.base_url}/network/quote",
            json={
                "orderId": order_id,
                "networkId": network_id,
                "baseFee": base_fee,
                "finalPrice": final_price,
                "transitDays": transit_days
            }
        )
        return response.json()

    def select_quote(self, order_id: int, quote_id: int) -> Dict[str, Any]:
        response = self.session.post(
            f"{self.base_url}/dispatch/select-quote",
            json={"orderId": order_id, "quoteId": quote_id}
        )
        return response.json()

    def push_quote(self, order_id: int) -> Dict[str, Any]:
        response = self.session.post(
            f"{self.base_url}/dispatch/push-quote",
            json={"orderId": order_id}
        )
        return response.json()

    def confirm_price(self, order_id: int) -> Dict[str, Any]:
        response = self.session.post(
            f"{self.base_url}/dispatch/confirm-price",
            json={"orderId": order_id}
        )
        return response.json()

    def assign_pickup_driver(self, order_id: int, driver_id: int) -> Dict[str, Any]:
        response = self.session.post(
            f"{self.base_url}/dispatch/assign-pickup-driver",
            json={"orderId": order_id, "driverId": driver_id}
        )
        return response.json()

    def driver_pickup(self, order_id: int, driver_id: int) -> Dict[str, Any]:
        response = self.session.post(
            f"{self.base_url}/driver/pickup",
            json={"orderId": order_id, "driverId": driver_id}
        )
        return response.json()

    def network_confirm_receive(self, order_id: int, network_id: int) -> Dict[str, Any]:
        response = self.session.post(
            f"{self.base_url}/network/confirm-receive",
            json={"orderId": order_id, "networkId": network_id}
        )
        return response.json()

    def assign_delivery_driver(self, order_id: int, driver_id: int) -> Dict[str, Any]:
        response = self.session.post(
            f"{self.base_url}/dispatch/assign-delivery-driver",
            json={"orderId": order_id, "driverId": driver_id}
        )
        return response.json()

    def order_sign(self, order_id: int) -> Dict[str, Any]:
        response = self.session.post(
            f"{self.base_url}/order/sign",
            json={"orderId": order_id}
        )
        return response.json()

    def get_order(self, order_id: int) -> Dict[str, Any]:
        response = self.session.get(f"{self.base_url}/order/{order_id}")
        return response.json()

    def get_orders(self, user_id: int = None, user_type: int = None) -> Dict[str, Any]:
        params = {}
        if user_id:
            params["userId"] = user_id
        if user_type:
            params["userType"] = user_type
        response = self.session.get(f"{self.base_url}/order/list", params=params)
        return response.json()

    def get_driver_orders(self, driver_id: int) -> Dict[str, Any]:
        response = self.session.get(
            f"{self.base_url}/driver/orders",
            params={"driverId": driver_id}
        )
        return response.json()

    def get_network_quotes(self, network_id: int) -> Dict[str, Any]:
        response = self.session.get(
            f"{self.base_url}/network/quotes",
            params={"networkId": network_id}
        )
        return response.json()

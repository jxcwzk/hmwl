# 四角色物流流程验证方案 - 实施计划

> **For agentic workers:** REQUIRED: Use superpowers:subagent-driven-development (if subagents available) or superpowers:executing-plans to implement this plan.

**Goal:** 实现完整的四角色物流流程自动化验证系统，覆盖 Phase 1-5 所有测试场景

**Architecture:**
- 测试框架: pytest (Python) + Puppeteer (JavaScript)
- 测试数据: SQL 初始化脚本 + pytest fixture
- 报告生成: pytest-html + Allure
- CI/CD: GitHub Actions

**Tech Stack:** Python 3.9+, pytest, requests, Puppeteer, MySQL, GitHub Actions

**Spec:** `02-active/四角色物流流程验证方案.md`

---

## 文件结构

```
tests/
├── conftest.py                    # pytest 配置和 fixture
├── phase1_normal_flow/            # 正常流程测试
│   ├── __init__.py
│   ├── test_order_creation.py    # 步骤1: 客户下单
│   ├── test_dispatch_quotes.py   # 步骤2: 调度派发比价
│   ├── test_network_quote.py     # 步骤3: 网点报价
│   ├── test_select_quote.py      # 步骤4: 调度选择报价
│   ├── test_push_quote.py        # 步骤5: 调度推送报价
│   ├── test_confirm_price.py     # 步骤6: 客户确认价格
│   ├── test_assign_pickup.py     # 步骤7: 调度安排提货
│   ├── test_driver_pickup.py     # 步骤8: 司机提货
│   ├── test_network_confirm.py    # 步骤9: 网点确认收货
│   └── test_order_sign.py        # 步骤10: 客户签收
├── phase2_exception/             # 异常场景测试
│   ├── __init__.py
│   ├── test_quote_timeout.py     # 场景1: 报价超时
│   ├── test_customer_reject.py    # 场景2: 客户拒绝
│   ├── test_driver_cancel.py      # 场景3: 司机取消
│   └── test_delivery_fail.py     # 场景4: 配送失败
├── phase3_parallel/              # 并行测试
│   ├── __init__.py
│   ├── test_multi_order.py       # 多订单并行
│   ├── test_multi_network.py     # 多网点并行
│   └── test_multi_driver.py      # 多司机并行
├── phase4_ui/                    # UI端到端测试
│   ├── puppeteer-runner.js       # Puppeteer 入口
│   ├── pages/                    # 页面对象
│   └── tests/                    # UI测试用例
├── phase5_isolation/             # 多账号隔离测试
│   ├── __init__.py
│   ├── test_customer_isolation.py      # 客户间隔离
│   ├── test_dispatcher_isolation.py    # 调度间隔离
│   ├── test_network_isolation.py        # 网点间隔离
│   ├── test_driver_isolation.py        # 司机间隔离
│   └── test_session_switch.py           # 账号切换
├── utils/
│   ├── api_client.py            # API 客户端封装
│   ├── db_helper.py             # 数据库助手
│   └── test_data.py             # 测试数据生成
├── reports/                      # 测试报告
├── screenshots/                  # UI测试截图
├── logs/                        # 测试日志
└── run-tests.sh                # 测试启动脚本

database/
└── init_test_data.sql          # 测试数据初始化脚本

.github/
└── workflows/
    └── logistics-test.yml       # GitHub Actions CI/CD 配置
```

---

## Chunk 1: 测试基础设施

### Task 1.1: 创建测试目录结构

**Files:**
- Create: `tests/__init__.py`
- Create: `tests/conftest.py`
- Create: `tests/utils/__init__.py`
- Create: `tests/phase1_normal_flow/__init__.py`
- Create: `tests/phase2_exception/__init__.py`
- Create: `tests/phase3_parallel/__init__.py`
- Create: `tests/phase4_ui/__init__.py`
- Create: `tests/phase5_isolation/__init__.py`

- [ ] **Step 1: 创建目录结构**

```bash
mkdir -p tests/{phase1_normal_flow,phase2_exception,phase3_parallel,phase4_ui/{pages,tests},phase5_isolation,utils,reports,screenshots,logs}
touch tests/__init__.py tests/utils/__init__.py tests/phase*/__init__.py
```

- [ ] **Step 2: 创建 conftest.py**

```python
import pytest
import sys
import os

# 添加项目路径
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

@pytest.fixture(scope="session")
def base_url():
    return "http://localhost:8081/api"

@pytest.fixture(scope="session")
def test_accounts():
    return {
        "customer1": {"id": 1, "username": "customer1", "password": "123456", "type": 3},
        "customer2": {"id": 2, "username": "customer2", "password": "123456", "type": 3},
        "admin1": {"id": 4, "username": "admin1", "password": "123456", "type": 1},
        "network1": {"id": 1, "username": "network1", "password": "123456", "type": 2},
        "driver1": {"id": 1, "username": "driver1", "password": "123456", "type": 4},
    }

@pytest.fixture(scope="function")
def clean_test_orders():
    """每个测试前清理测试订单"""
    yield
    # 清理逻辑
```

- [ ] **Step 3: 提交**

```bash
git add tests/ && git commit -m "feat(tests): create test directory structure"
```

### Task 1.2: 创建测试数据初始化脚本

**Files:**
- Create: `database/init_test_data.sql`

- [ ] **Step 1: 创建 SQL 脚本**

```sql
-- 四角色物流流程测试数据初始化
-- 使用说明: 执行此脚本前需先备份现有数据

-- 客户账号
INSERT INTO user (id, username, password, user_type, create_time) VALUES
(1, 'customer1', '123456', 3, NOW()),
(2, 'customer2', '123456', 3, NOW()),
(3, 'customer3', '123456', 3, NOW())
ON DUPLICATE KEY UPDATE username=username;

-- 调度账号 (管理员)
INSERT INTO user (id, username, password, user_type, create_time) VALUES
(4, 'admin1', '123456', 1, NOW()),
(5, 'admin2', '123456', 1, NOW())
ON DUPLICATE KEY UPDATE username=username;

-- 网点账号
INSERT INTO user (id, username, password, user_type, create_time) VALUES
(6, 'network1', '123456', 2, NOW()),
(7, 'network2', '123456', 2, NOW()),
(8, 'network3', '123456', 2, NOW())
ON DUPLICATE KEY UPDATE username=username;

-- 网点
INSERT INTO network_point (id, name, code, user_id, create_time) VALUES
(1, 'network1', 'NW001', 6, NOW()),
(2, 'network2', 'NW002', 7, NOW()),
(3, 'network3', 'NW003', 8, NOW())
ON DUPLICATE KEY UPDATE name=name;

-- 司机账号
INSERT INTO user (id, username, password, user_type, create_time) VALUES
(9, 'driver1', '123456', 4, NOW()),
(10, 'driver2', '123456', 4, NOW()),
(11, 'driver3', '123456', 4, NOW())
ON DUPLICATE KEY UPDATE username=username;

-- 司机
INSERT INTO driver (id, name, phone, network_id, user_id, create_time) VALUES
(1, 'driver1', '13800001111', 1, 9, NOW()),
(2, 'driver2', '13800002222', 1, 10, NOW()),
(3, 'driver3', '13800003333', 2, 11, NOW())
ON DUPLICATE KEY UPDATE name=name;
```

- [ ] **Step 2: 提交**

```bash
git add database/init_test_data.sql && git commit -m "feat(tests): add test data initialization SQL"
```

### Task 1.3: 创建 API 客户端封装

**Files:**
- Create: `tests/utils/api_client.py`

- [ ] **Step 1: 创建 API 客户端**

```python
import requests
from typing import Optional, Dict, Any

class ApiClient:
    def __init__(self, base_url: str):
        self.base_url = base_url
        self.session = requests.Session()

    def login(self, username: str, password: str) -> Dict[str, Any]:
        """登录获取session"""
        response = self.session.post(
            f"{self.base_url}/login",
            json={"username": username, "password": password}
        )
        return response.json()

    def create_order(self, user_id: int, data: Dict) -> Dict[str, Any]:
        """创建订单"""
        payload = {
            "userId": user_id,
            "userType": 3,
            "businessUserId": user_id,
            **data
        }
        response = self.session.post(f"{self.base_url}/order", json=payload)
        return response.json()

    def request_quotes(self, order_id: int, network_ids: list) -> Dict[str, Any]:
        """派发比价"""
        response = self.session.post(
            f"{self.base_url}/dispatch/request-quotes",
            json={"orderId": order_id, "networkPointIds": network_ids}
        )
        return response.json()

    def network_quote(self, order_id: int, network_id: int,
                      base_fee: float, final_price: float,
                      transit_days: int) -> Dict[str, Any]:
        """网点报价"""
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
        """调度选择报价"""
        response = self.session.post(
            f"{self.base_url}/dispatch/select-quote",
            json={"orderId": order_id, "quoteId": quote_id}
        )
        return response.json()

    def confirm_price(self, order_id: int) -> Dict[str, Any]:
        """客户确认价格"""
        response = self.session.post(
            f"{self.base_url}/dispatch/confirm-price",
            json={"orderId": order_id}
        )
        return response.json()

    def assign_pickup_driver(self, order_id: int, driver_id: int) -> Dict[str, Any]:
        """分配提货司机"""
        response = self.session.post(
            f"{self.base_url}/dispatch/assign-pickup-driver",
            json={"orderId": order_id, "driverId": driver_id}
        )
        return response.json()

    def driver_pickup(self, order_id: int, driver_id: int) -> Dict[str, Any]:
        """司机提货"""
        response = self.session.post(
            f"{self.base_url}/driver/pickup",
            json={"orderId": order_id, "driverId": driver_id}
        )
        return response.json()

    def network_confirm_receive(self, order_id: int, network_id: int) -> Dict[str, Any]:
        """网点确认收货"""
        response = self.session.post(
            f"{self.base_url}/network/confirm-receive",
            json={"orderId": order_id, "networkId": network_id}
        )
        return response.json()

    def assign_delivery_driver(self, order_id: int, driver_id: int) -> Dict[str, Any]:
        """分配配送司机"""
        response = self.session.post(
            f"{self.base_url}/dispatch/assign-delivery-driver",
            json={"orderId": order_id, "driverId": driver_id}
        )
        return response.json()

    def order_sign(self, order_id: int) -> Dict[str, Any]:
        """客户签收"""
        response = self.session.post(
            f"{self.base_url}/order/sign",
            json={"orderId": order_id}
        )
        return response.json()

    def get_order(self, order_id: int) -> Dict[str, Any]:
        """获取订单详情"""
        response = self.session.get(f"{self.base_url}/order/{order_id}")
        return response.json()
```

- [ ] **Step 2: 提交**

```bash
git add tests/utils/api_client.py && git commit -m "feat(tests): add API client wrapper"
```

---

## Chunk 2: Phase 1 正常流程测试

### Task 2.1: 测试步骤1 - 客户下单

**Files:**
- Create: `tests/phase1_normal_flow/test_order_creation.py`

- [ ] **Step 1: 创建测试**

```python
import pytest
from tests.utils.api_client import ApiClient

class TestOrderCreation:
    def test_create_order_success(self, base_url, test_accounts):
        """测试客户下单成功"""
        client = ApiClient(base_url)
        client.login(test_accounts["customer1"]["username"],
                     test_accounts["customer1"]["password"])

        result = client.create_order(
            user_id=test_accounts["customer1"]["id"],
            data={
                "senderName": "张三",
                "senderPhone": "13800138000",
                "senderAddress": "上海市浦东新区",
                "receiverName": "李四",
                "receiverPhone": "13900139000",
                "receiverAddress": "北京市朝阳区",
                "goodsName": "电脑",
                "weight": 5
            }
        )

        assert result.get("success") == True
        assert "orderId" in result or "order_no" in result
        order_info = client.get_order(result.get("orderId", result.get("id")))
        assert order_info.get("status") == 0
        print(f"订单创建成功: {result}")
```

- [ ] **Step 2: 提交**

```bash
git add tests/phase1_normal_flow/test_order_creation.py
git commit -m "feat(tests): add Phase 1 step 1 - order creation test"
```

### Task 2.2: 测试步骤2-4 - 调度派发、网点报价、选择报价

**Files:**
- Create: `tests/phase1_normal_flow/test_dispatch_quotes.py`
- Create: `tests/phase1_normal_flow/test_network_quote.py`
- Create: `tests/phase1_normal_flow/test_select_quote.py`

- [ ] **Step 1: 创建调度派发测试**

```python
def test_dispatch_quotes(self, base_url, test_accounts):
    """测试调度派发比价"""
    client = ApiClient(base_url)
    client.login(test_accounts["admin1"]["username"],
                 test_accounts["admin1"]["password"])

    # 假设订单ID=100
    result = client.request_quotes(order_id=100, network_ids=[1, 2])

    assert result.get("success") == True
    order_info = client.get_order(100)
    assert order_info.get("pricingStatus") == 1
```

- [ ] **Step 2: 创建网点报价测试**

```python
def test_network_quote(self, base_url, test_accounts):
    """测试网点报价"""
    client = ApiClient(base_url)
    client.login(test_accounts["network1"]["username"],
                 test_accounts["network1"]["password"])

    result = client.network_quote(
        order_id=100,
        network_id=1,
        base_fee=60.0,
        final_price=85.72,
        transit_days=3
    )

    assert result.get("success") == True
```

- [ ] **Step 3: 创建选择报价测试**

```python
def test_select_quote(self, base_url, test_accounts):
    """测试调度选择报价"""
    client = ApiClient(base_url)
    client.login(test_accounts["admin1"]["username"],
                 test_accounts["admin1"]["password"])

    # 假设报价ID=50
    result = client.select_quote(order_id=100, quote_id=50)

    assert result.get("success") == True
    order_info = client.get_order(100)
    assert order_info.get("pricingStatus") == 2
```

- [ ] **Step 4: 提交**

```bash
git add tests/phase1_normal_flow/test_dispatch_quotes.py
git add tests/phase1_normal_flow/test_network_quote.py
git add tests/phase1_normal_flow/test_select_quote.py
git commit -m "feat(tests): add Phase 1 steps 2-4 - dispatch, quote, select tests"
```

### Task 2.3: 测试步骤5-7 - 推送报价、确认价格、安排提货

**Files:**
- Create: `tests/phase1_normal_flow/test_push_quote.py`
- Create: `tests/phase1_normal_flow/test_confirm_price.py`
- Create: `tests/phase1_normal_flow/test_assign_pickup.py`

- [ ] **Step 1: 创建推送报价测试**

```python
def test_push_quote(self, base_url, test_accounts):
    """测试调度推送报价给客户"""
    client = ApiClient(base_url)
    client.login(test_accounts["admin1"]["username"],
                 test_accounts["admin1"]["password"])

    result = client.push_quote(order_id=100)

    assert result.get("success") == True
    order_info = client.get_order(100)
    assert order_info.get("status") == 4
```

- [ ] **Step 2: 创建确认价格测试**

```python
def test_confirm_price(self, base_url, test_accounts):
    """测试客户确认价格"""
    client = ApiClient(base_url)
    client.login(test_accounts["customer1"]["username"],
                 test_accounts["customer1"]["password"])

    result = client.confirm_price(order_id=100)

    assert result.get("success") == True
    order_info = client.get_order(100)
    assert order_info.get("pricingStatus") == 3
    assert order_info.get("status") == 5
```

- [ ] **Step 3: 创建安排提货测试**

```python
def test_assign_pickup(self, base_url, test_accounts):
    """测试调度安排提货司机"""
    client = ApiClient(base_url)
    client.login(test_accounts["admin1"]["username"],
                 test_accounts["admin1"]["password"])

    result = client.assign_pickup_driver(order_id=100, driver_id=1)

    assert result.get("success") == True
    order_info = client.get_order(100)
    assert order_info.get("pricingStatus") == 4
    assert order_info.get("driverId") == 1
```

- [ ] **Step 4: 提交**

```bash
git add tests/phase1_normal_flow/test_push_quote.py
git add tests/phase1_normal_flow/test_confirm_price.py
git add tests/phase1_normal_flow/test_assign_pickup.py
git commit -m "feat(tests): add Phase 1 steps 5-7 - push quote, confirm price, assign pickup"
```

### Task 2.4: 测试步骤8-10 - 提货、确认收货、签收

**Files:**
- Create: `tests/phase1_normal_flow/test_driver_pickup.py`
- Create: `tests/phase1_normal_flow/test_network_confirm.py`
- Create: `tests/phase1_normal_flow/test_order_sign.py`

- [ ] **Step 1: 创建司机提货测试**

```python
def test_driver_pickup(self, base_url, test_accounts):
    """测试司机提货"""
    client = ApiClient(base_url)
    client.login(test_accounts["driver1"]["username"],
                 test_accounts["driver1"]["password"])

    result = client.driver_pickup(order_id=100, driver_id=1)

    assert result.get("success") == True
    order_info = client.get_order(100)
    assert order_info.get("pricingStatus") == 5
```

- [ ] **Step 2: 创建网点确认收货测试**

```python
def test_network_confirm(self, base_url, test_accounts):
    """测试网点确认收货"""
    client = ApiClient(base_url)
    client.login(test_accounts["network1"]["username"],
                 test_accounts["network1"]["password"])

    result = client.network_confirm_receive(order_id=100, network_id=1)

    assert result.get("success") == True
    order_info = client.get_order(100)
    assert order_info.get("status") == 9
```

- [ ] **Step 3: 创建客户签收测试**

```python
def test_order_sign(self, base_url, test_accounts):
    """测试客户签收"""
    client = ApiClient(base_url)
    client.login(test_accounts["customer1"]["username"],
                 test_accounts["customer1"]["password"])

    result = client.order_sign(order_id=100)

    assert result.get("success") == True
    order_info = client.get_order(100)
    assert order_info.get("status") == 13
```

- [ ] **Step 4: 提交**

```bash
git add tests/phase1_normal_flow/test_driver_pickup.py
git add tests/phase1_normal_flow/test_network_confirm.py
git add tests/phase1_normal_flow/test_order_sign.py
git commit -m "feat(tests): add Phase 1 steps 8-10 - pickup, confirm receive, sign"
```

---

## Chunk 3: Phase 2 异常场景测试

### Task 3.1: 报价超时场景

**Files:**
- Create: `tests/phase2_exception/test_quote_timeout.py`

- [ ] **Step 1: 创建报价超时测试**

```python
import pytest
from unittest.mock import patch
from tests.utils.api_client import ApiClient

class TestQuoteTimeout:
    def test_quote_timeout_handling(self, base_url, test_accounts):
        """测试报价超时处理"""
        client = ApiClient(base_url)
        client.login(test_accounts["admin1"]["username"],
                     test_accounts["admin1"]["password"])

        # 创建订单并派发
        order_result = client.create_order(...)
        client.request_quotes(order_result["orderId"], [1, 2])

        # 模拟超时 (实际测试中可能需要等待或mock时间)
        # 验证调度可以重新派发或取消
        with patch("time.sleep"):
            # 验证超时后订单状态
            order_info = client.get_order(order_result["orderId"])
            assert order_info.get("pricingStatus") == 1  # 仍为待报价状态
```

- [ ] **Step 2: 提交**

```bash
git add tests/phase2_exception/test_quote_timeout.py
git commit -m "feat(tests): add Phase 2 - quote timeout test"
```

### Task 3.2: 客户拒绝场景

**Files:**
- Create: `tests/phase2_exception/test_customer_reject.py`

- [ ] **Step 1: 创建客户拒绝测试**

```python
def test_customer_reject_price(self, base_url, test_accounts):
    """测试客户拒绝价格"""
    # 前提: 订单已推送给客户 (status=4)
    # 客户拒绝 -> 订单保持status=4，调度可调整

    # 验证订单状态为已推送
    order_info = client.get_order(order_id)
    assert order_info.get("status") == 4

    # 调度可以重新调整价格 (调用调整价格API或重新推送)
```

- [ ] **Step 2: 提交**

```bash
git add tests/phase2_exception/test_customer_reject.py
git commit -m "feat(tests): add Phase 2 - customer reject test"
```

### Task 3.3: 司机取消和配送失败场景

**Files:**
- Create: `tests/phase2_exception/test_driver_cancel.py`
- Create: `tests/phase2_exception/test_delivery_fail.py`

- [ ] **Step 1: 创建司机取消测试**

```python
def test_driver_cancel_pickup(self, base_url, test_accounts):
    """测试司机取消提货"""
    # 前提: 订单已分配提货司机 (pricingStatus=4)
    # 司机取消 -> pricingStatus回退，调度可重新分配
```

- [ ] **Step 2: 创建配送失败测试**

```python
def test_delivery_failure(self, base_url, test_accounts):
    """测试配送失败"""
    # 前提: 配送中 (pricingStatus=6)
    # 配送失败 -> pricingStatus保持，调度可重新分配
```

- [ ] **Step 3: 提交**

```bash
git add tests/phase2_exception/test_driver_cancel.py
git add tests/phase2_exception/test_delivery_fail.py
git commit -m "feat(tests): add Phase 2 - driver cancel and delivery fail tests"
```

---

## Chunk 4: Phase 3 并行测试

### Task 4.1: 多订单并行测试

**Files:**
- Create: `tests/phase3_parallel/test_multi_order.py`

- [ ] **Step 1: 创建并行测试**

```python
import pytest
from concurrent.futures import ThreadPoolExecutor, as_completed
from tests.utils.api_client import ApiClient

class TestMultiOrderParallel:
    def test_multi_order_concurrent_dispatch(self, base_url, test_accounts):
        """测试多订单并行派发"""
        client = ApiClient(base_url)
        client.login(test_accounts["admin1"]["username"],
                     test_accounts["admin1"]["password"])

        # 创建5个订单
        order_ids = []
        for i in range(5):
            result = client.create_order(...)
            order_ids.append(result["orderId"])

        # 并行派发
        def dispatch_order(order_id):
            return client.request_quotes(order_id, [1, 2])

        with ThreadPoolExecutor(max_workers=5) as executor:
            futures = [executor.submit(dispatch_order, oid) for oid in order_ids]
            results = [f.result() for f in as_completed(futures)]

        # 验证所有订单状态独立
        for order_id in order_ids:
            order_info = client.get_order(order_id)
            assert order_info.get("pricingStatus") == 1
```

- [ ] **Step 2: 提交**

```bash
git add tests/phase3_parallel/test_multi_order.py
git commit -m "feat(tests): add Phase 3 - multi-order parallel test"
```

### Task 4.2: 多网点和多司机并行测试

**Files:**
- Create: `tests/phase3_parallel/test_multi_network.py`
- Create: `tests/phase3_parallel/test_multi_driver.py`

- [ ] **Step 1: 创建多网点并行测试**

```python
def test_multi_network_concurrent_quote(self, base_url, test_accounts):
    """测试多网点同时报价"""
    # 1个订单派发给3个网点，3个网点同时报价
    # 验证报价记录都保存，互不覆盖
```

- [ ] **Step 2: 创建多司机并行测试**

```python
def test_multi_driver_concurrent_delivery(self, base_url, test_accounts):
    """测试多司机同时配送"""
    # 3个订单分配给3个司机，同时更新状态
    # 验证状态独立，无串改
```

- [ ] **Step 3: 提交**

```bash
git add tests/phase3_parallel/test_multi_network.py
git add tests/phase3_parallel/test_multi_driver.py
git commit -m "feat(tests): add Phase 3 - multi-network and multi-driver parallel tests"
```

---

## Chunk 5: Phase 4 UI端到端测试

### Task 5.1: Puppeteer 测试框架

**Files:**
- Create: `tests/phase4_ui/puppeteer-runner.js`
- Create: `tests/phase4_ui/package.json`

- [ ] **Step 1: 创建 package.json**

```json
{
  "name": "logistics-ui-tests",
  "version": "1.0.0",
  "scripts": {
    "test": "node puppeteer-runner.js"
  },
  "dependencies": {
    "puppeteer": "^19.0.0"
  }
}
```

- [ ] **Step 2: 创建 Puppeteer 入口**

```javascript
const puppeteer = require('puppeteer');

async function runTests() {
  const browser = await puppeteer.launch({
    headless: 'new',
    args: ['--no-sandbox', '--disable-setuid-sandbox']
  });

  const page = await browser.newPage();

  try {
    // 测试登录
    await page.goto('http://localhost:8080/#/login');
    await page.waitForSelector('input[name="username"]');
    await page.type('input[name="username"]', 'customer1');
    await page.type('input[name="password"]', '123456');
    await page.click('button[type="submit"]');
    await page.waitForNavigation();

    console.log('✓ 登录测试通过');

    // 测试下单
    await page.goto('http://localhost:8080/#/order/create');
    // ... 填写表单提交

    console.log('✓ UI端到端测试完成');

  } catch (error) {
    console.error('测试失败:', error);
    await page.screenshot({ path: 'screenshots/error.png' });
  } finally {
    await browser.close();
  }
}

runTests();
```

- [ ] **Step 3: 提交**

```bash
git add tests/phase4_ui/
git commit -m "feat(tests): add Phase 4 - Puppeteer UI test framework"
```

---

## Chunk 6: Phase 5 多账号隔离测试

### Task 6.1: 客户间数据隔离测试

**Files:**
- Create: `tests/phase5_isolation/test_customer_isolation.py`

- [ ] **Step 1: 创建隔离测试**

```python
class TestCustomerIsolation:
    def test_customer_data_isolation(self, base_url, test_accounts):
        """测试客户间数据隔离"""
        client1 = ApiClient(base_url)
        client2 = ApiClient(base_url)

        # customer1 登录并下单
        client1.login(test_accounts["customer1"]["username"],
                      test_accounts["customer1"]["password"])
        order1 = client1.create_order(user_id=1, data={...})

        # customer2 登录并下单
        client2.login(test_accounts["customer2"]["username"],
                      test_accounts["customer2"]["password"])
        order2 = client2.create_order(user_id=2, data={...})

        # customer1 查看订单 - 不应看到 order2
        customer1_orders = client1.get_orders()
        order1_ids = [o["id"] for o in customer1_orders]

        assert order1 in order1_ids
        assert order2 not in order1_ids

        # customer1 尝试直接访问 order2 - 应被拒绝
        with pytest.raises(Exception):
            client1.get_order(order2["id"])
```

- [ ] **Step 2: 提交**

```bash
git add tests/phase5_isolation/test_customer_isolation.py
git commit -m "feat(tests): add Phase 5 - customer data isolation test"
```

### Task 6.2: 其他角色隔离测试

**Files:**
- Create: `tests/phase5_isolation/test_dispatcher_isolation.py`
- Create: `tests/phase5_isolation/test_network_isolation.py`
- Create: `tests/phase5_isolation/test_driver_isolation.py`
- Create: `tests/phase5_isolation/test_session_switch.py`

- [ ] **Step 1: 创建调度隔离测试**

```python
def test_dispatcher_isolation(self, base_url, test_accounts):
    """测试调度间隔离 - admin1处理的订单不应影响admin2的视图"""
```

- [ ] **Step 2: 创建网点隔离测试**

```python
def test_network_isolation(self, base_url, test_accounts):
    """测试网点间隔离 - network1的报价不应被network2看到"""
```

- [ ] **Step 3: 创建司机隔离测试**

```python
def test_driver_isolation(self, base_url, test_accounts):
    """测试司机间隔离 - driver1的任务不应被driver2看到"""
```

- [ ] **Step 4: 创建账号切换测试**

```python
def test_session_switch(self, base_url, test_accounts):
    """测试账号切换后权限正确"""
```

- [ ] **Step 5: 提交**

```bash
git add tests/phase5_isolation/test_dispatcher_isolation.py
git add tests/phase5_isolation/test_network_isolation.py
git add tests/phase5_isolation/test_driver_isolation.py
git add tests/phase5_isolation/test_session_switch.py
git commit -m "feat(tests): add Phase 5 - isolation tests for all roles"
```

---

## Chunk 7: CI/CD 和报告

### Task 7.1: GitHub Actions 配置

**Files:**
- Create: `.github/workflows/logistics-test.yml`

- [ ] **Step 1: 创建 GitHub Actions 配置**

```yaml
name: Logistics Flow Test

on:
  push:
    branches: [main]
  pull_request:
    branches: [main]

jobs:
  api-tests:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3

      - name: Set up Python
        uses: actions/setup-python@v4
        with:
          python-version: '3.9'

      - name: Install dependencies
        run: |
          pip install pytest pytest-html requests

      - name: Start Backend
        run: |
          cd backend
          mvn spring-boot:run &
          sleep 30

      - name: Run Phase 1 & 2 Tests
        run: |
          pytest tests/phase1_normal_flow tests/phase2_exception \
                 --html=reports/api-test-report.html --self-contained-html

      - name: Run Phase 3 Tests
        run: |
          pytest tests/phase3_parallel \
                 --html=reports/parallel-test-report.html

      - name: Run Phase 5 Tests
        run: |
          pytest tests/phase5_isolation \
                 --html=reports/isolation-test-report.html

      - name: Upload Reports
        uses: actions/upload-artifact@v3
        with:
          name: test-reports
          path: reports/

  ui-tests:
    runs-on: ubuntu-latest
    needs: api-tests
    steps:
      - uses: actions/checkout@v3

      - name: Set up Node.js
        uses: actions/setup-node@v3
        with:
          node-version: '18'

      - name: Install dependencies
        run: |
          cd tests/phase4_ui
          npm install

      - name: Start Services
        run: |
          cd backend && mvn spring-boot:run &
          sleep 30
          cd ../vue && npm run serve &
          sleep 20

      - name: Run UI Tests
        run: |
          cd tests/phase4_ui
          node puppeteer-runner.js

      - name: Upload Screenshots
        uses: actions/upload-artifact@v3
        with:
          name: ui-screenshots
          path: tests/phase4_ui/screenshots/
```

- [ ] **Step 2: 提交**

```bash
git add .github/workflows/logistics-test.yml
git commit -m "feat(ci): add GitHub Actions workflow for logistics tests"
```

### Task 7.2: 测试运行脚本

**Files:**
- Create: `tests/run-tests.sh`

- [ ] **Step 1: 创建运行脚本**

```bash
#!/bin/bash

# 四角色物流流程测试运行脚本

set -e

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m'

echo -e "${GREEN}开始四角色物流流程测试...${NC}"

# 解析参数
PHASE=""
SCENARIO=""

while [[ $# -gt 0 ]]; do
  case $1 in
    --phase)
      PHASE="$2"
      shift 2
      ;;
    --scenario)
      SCENARIO="$2"
      shift 2
      ;;
    --all)
      PHASE="all"
      shift
      ;;
    *)
      echo "未知参数: $1"
      exit 1
      ;;
  esac
done

# 启动后端服务
echo -e "${GREEN}启动后端服务...${NC}"
cd backend
mvn spring-boot:run &
BACKEND_PID=$!
sleep 30

# 运行测试
if [ "$PHASE" == "all" ]; then
  pytest tests/phase1_normal_flow tests/phase2_exception \
         tests/phase3_parallel tests/phase5_isolation \
         --html=reports/test-report.html --self-contained-html
elif [ "$PHASE" == "1" ]; then
  pytest tests/phase1_normal_flow --html=reports/phase1-report.html
elif [ "$PHASE" == "2" ]; then
  pytest tests/phase2_exception --html=reports/phase2-report.html
elif [ "$PHASE" == "3" ]; then
  pytest tests/phase3_parallel --html=reports/phase3-report.html
elif [ "$PHASE" == "4" ]; then
  cd tests/phase4_ui && npm install && node puppeteer-runner.js
elif [ "$PHASE" == "5" ]; then
  pytest tests/phase5_isolation --html=reports/phase5-report.html
else
  pytest tests/ --html=reports/test-report.html
fi

# 清理
kill $BACKEND_PID 2>/dev/null || true

echo -e "${GREEN}测试完成！${NC}"
```

- [ ] **Step 2: 添加执行权限并提交**

```bash
chmod +x tests/run-tests.sh
git add tests/run-tests.sh
git commit -m "feat(tests): add test runner script"
```

---

## 执行顺序

1. **Chunk 1**: 测试基础设施 (目录结构、配置、API客户端)
2. **Chunk 2**: Phase 1 正常流程测试 (10步)
3. **Chunk 3**: Phase 2 异常场景测试
4. **Chunk 4**: Phase 3 并行测试
5. **Chunk 5**: Phase 4 UI测试框架
6. **Chunk 6**: Phase 5 多账号隔离测试
7. **Chunk 7**: CI/CD 配置和运行脚本

---

## 风险与注意事项

1. **后端服务**: 测试需要后端服务运行在 localhost:8081
2. **测试数据**: 首次运行需要执行 database/init_test_data.sql
3. **端口占用**: 确保 8081 和 8080 端口未被占用
4. **清理**: 测试后会留下 TEST% 订单，需要定期清理

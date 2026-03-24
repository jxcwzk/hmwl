---
name: logistics-price-estimator
description: 物流运输估价专家。基于货物重量、体积和运输距离计算物流费用。支持按重量计费、按体积计费、按距离计费等多种计价方式，并根据订单类型(零担/整车)调整价格。使用此skill进行：物流报价计算、运费估算、运输成本分析
---

# 物流运输估价专家

根据货物信息(重量、体积)和运输信息(距离)计算物流运输费用。

## 输入参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| weight | number | 是 | 货物重量(kg) |
| volume | number | 是 | 货物体积(m³) |
| distance | number | 否 | 运输距离(公里) |
| orderType | number | 否 | 订单类型: 0-零担, 1-整车，默认0 |
| startNetworkId | number | 否 | 起始网点ID |
| endNetworkId | number | 否 | 目的网点ID |

## 默认价格配置

如果数据库中没有价格配置，使用以下默认值：

```javascript
{
  basePrice: 10,           // 起步价
  pricePerKg: 1.5,        // 每公斤单价
  pricePerVolume: 80,      // 每立方米单价
  pricePerKm: 0.5,        // 每公里单价
  minPrice: 20,            // 最低收费
  trunkRouteDiscount: 0.1  // 干线折扣
}
```

## 计价规则

### 1. 重量计价阶梯
- 轻货(≤5kg): 单价 × 0.8
- 中货(5-20kg): 单价 × 1.0
- 重货(20-100kg): 单价 × 1.2
- 超重(>100kg): 单价 × 1.5

### 2. 体积计价阶梯
- 小件(≤0.1m³): 单价 × 0.8
- 中件(0.1-1m³): 单价 × 1.0
- 大件(1-5m³): 单价 × 1.2
- 超大(>5m³): 单价 × 1.5

### 3. 距离计价阶梯
- 短途(≤100km): 单价 × 1.2
- 中途(100-500km): 单价 × 1.0
- 长途(500-1000km): 单价 × 0.8
-  超长途(>1000km): 单价 × 0.6

### 4. 整车加成
- 整车订单费用 = 计算费用 × 1.5

### 5. 保底价格
- 最低收费: 20元

## 使用方式

### 前端集成
在前端订单表单中添加"运费估算"按钮，调用后端API计算价格：

```javascript
// 获取价格估算
const price = await request.get('/price-calculate', {
  params: {
    weight: form.weight,
    volume: form.volume,
    distance: estimatedDistance,
    orderType: form.orderType
  }
});
```

### 计算公式

```
总费用 = 起步价 + 重量费用 + 体积费用 + 距离费用
```

其中：
- 重量费用 = 重量 × 单价 × 重量系数
- 体积费用 = 体积 × 单价 × 体积系数
- 距离费用 = 距离 × 单价 × 距离系数

## 输出示例

```json
{
  "totalFee": 125.50,
  "breakdown": {
    "baseFee": 10,
    "weightFee": 22.5,
    "volumeFee": 40,
    "distanceFee": 53
  },
  "details": {
    "weight": 15,
    "volume": 0.5,
    "distance": 100,
    "orderType": "零担",
    "pricingLevel": "standard"
  }
}
```

## 后续扩展

- 数据库完善后可从 `price_config` 表读取价格配置
- 距离可从 `route_distance` 表根据网点ID查询
- 可添加会员折扣、促销活动等影响因素

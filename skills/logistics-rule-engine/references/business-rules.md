# 业务规则场景

## 1. 订单处理规则

### 1.1 订单验证规则

**功能**：验证订单的合法性，确保订单数据完整、准确。

**规则示例**：

```
rule "订单必填字段验证"
    when
        order: Order(
            customerName == null || customerName.isEmpty(),
            customerPhone == null || customerPhone.isEmpty(),
            deliveryAddress == null || deliveryAddress.isEmpty()
        )
    then
        order.setStatus("invalid");
        order.setErrorMessage("订单信息不完整，缺少必填字段");
end

rule "订单金额验证"
    when
        order: Order(amount <= 0)
    then
        order.setStatus("invalid");
        order.setErrorMessage("订单金额必须大于0");
end

rule "库存验证"
    when
        order: Order()
        $items: List(size > 0) from order.items
        forall ($item : $items)
            $item.quantity > $item.product.stock
    then
        order.setStatus("invalid");
        order.setErrorMessage("部分商品库存不足");
end
```

### 1.2 订单优先级规则

**功能**：根据订单属性设置订单优先级，影响订单处理顺序。

**规则示例**：

```
rule "高优先级订单"
    when
        order: Order(
            priority == "high",
            amount > 1000
        )
    then
        order.setProcessingTime("immediate");
        order.setAssignedTo("priority_team");
end

rule "加急订单"
    when
        order: Order(urgent == true)
    then
        order.setPriority("high");
        order.setProcessingTime("immediate");
end

rule "普通订单"
    when
        order: Order(
            priority == "normal",
            amount <= 1000
        )
    then
        order.setProcessingTime("standard");
        order.setAssignedTo("standard_team");
end
```

### 1.3 订单分配规则

**功能**：根据订单属性和系统状态，将订单分配给合适的处理团队或人员。

**规则示例**：

```
rule "VIP客户订单分配"
    when
        order: Order(customer.vipLevel >= 3)
    then
        order.setAssignedTo("vip_team");
        order.setPriority("high");
end

rule "大额订单分配"
    when
        order: Order(amount > 5000)
    then
        order.setAssignedTo("enterprise_team");
        order.setPriority("high");
end

rule "区域订单分配"
    when
        order: Order(
            deliveryAddress.region == "north"
        )
    then
        order.setAssignedTo("north_team");
end
```

### 1.4 订单状态转换规则

**功能**：根据业务流程和事件，自动转换订单状态。

**规则示例**：

```
rule "订单支付成功"
    when
        order: Order(
            status == "pending_payment",
            paymentStatus == "paid"
        )
    then
        order.setStatus("processing");
        order.setProcessingStartTime(new Date());
end

rule "订单处理完成"
    when
        order: Order(
            status == "processing",
            items.allSatisfy(item -> item.processed == true)
        )
    then
        order.setStatus("shipped");
        order.setProcessingEndTime(new Date());
end

rule "订单配送完成"
    when
        order: Order(
            status == "shipped",
            deliveryStatus == "delivered"
        )
    then
        order.setStatus("completed");
        order.setDeliveryEndTime(new Date());
end
```

## 2. 路由规划规则

### 2.1 路径选择规则

**功能**：根据订单属性和地理信息，选择最优配送路径。

**规则示例**：

```
rule "最优路径选择"
    when
        route: Route(
            distance < 50,
            traffic == "light"
        )
    then
        route.setPriority("high");
        route.setEstimatedTime(route.distance / 60);
end

rule "备选路径选择"
    when
        route: Route(
            distance >= 50,
            distance < 100,
            traffic == "medium"
        )
    then
        route.setPriority("medium");
        route.setEstimatedTime(route.distance / 40);
end

rule "绕行路径选择"
    when
        route: Route(
            distance >= 100,
            traffic == "heavy"
        )
    then
        route.setPriority("low");
        route.setEstimatedTime(route.distance / 30);
end
```

### 2.2 车辆分配规则

**功能**：根据订单属性和车辆状态，分配合适的配送车辆。

**规则示例**：

```
rule "重型车辆分配"
    when
        order: Order(
            weight > 500,
            volume > 10
        )
        $vehicle: Vehicle(
            type == "truck",
            status == "available"
        )
    then
        order.setAssignedVehicle($vehicle);
        $vehicle.setStatus("occupied");
end

rule "中型车辆分配"
    when
        order: Order(
            weight > 100,
            weight <= 500,
            volume > 2,
            volume <= 10
        )
        $vehicle: Vehicle(
            type == "van",
            status == "available"
        )
    then
        order.setAssignedVehicle($vehicle);
        $vehicle.setStatus("occupied");
end

rule "轻型车辆分配"
    when
        order: Order(
            weight <= 100,
            volume <= 2
        )
        $vehicle: Vehicle(
            type == "car",
            status == "available"
        )
    then
        order.setAssignedVehicle($vehicle);
        $vehicle.setStatus("occupied");
end
```

### 2.3 时间窗口规则

**功能**：根据客户要求和系统能力，确定配送时间窗口。

**规则示例**：

```
rule "工作日时间窗口"
    when
        order: Order(
            deliveryTimePreference == "working_hours",
            dayOfWeek(now) >= 1,
            dayOfWeek(now) <= 5
        )
    then
        order.setDeliveryWindow("09:00-18:00");
end

rule "周末时间窗口"
    when
        order: Order(
            deliveryTimePreference == "weekend",
            dayOfWeek(now) >= 6,
            dayOfWeek(now) <= 7
        )
    then
        order.setDeliveryWindow("10:00-16:00");
end

rule "加急时间窗口"
    when
        order: Order(
            urgent == true
        )
    then
        order.setDeliveryWindow("within 2 hours");
end
```

### 2.4 成本计算规则

**功能**：根据路径和车辆信息，计算配送成本。

**规则示例**：

```
rule "基础配送成本"
    when
        order: Order()
        route: Route() from order.route
    then
        double baseCost = route.distance * 0.5;
        order.setDeliveryCost(baseCost);
end

rule "车辆成本加成"
    when
        order: Order(
            assignedVehicle.type == "truck"
        )
    then
        double vehicleCost = order.deliveryCost * 1.5;
        order.setDeliveryCost(vehicleCost);
end

rule "加急成本加成"
    when
        order: Order(
            urgent == true
        )
    then
        double urgentCost = order.deliveryCost * 2.0;
        order.setDeliveryCost(urgentCost);
end
```

## 3. 配送规则

### 3.1 配送时间规则

**功能**：根据订单属性和系统状态，计算预计配送时间。

**规则示例**：

```
rule "标准配送时间"
    when
        order: Order(
            priority == "normal",
            distance <= 50
        )
    then
        order.setEstimatedDeliveryTime(24); // 24小时
end

rule "加急配送时间"
    when
        order: Order(
            priority == "high",
            distance <= 30
        )
    then
        order.setEstimatedDeliveryTime(2); // 2小时
end

rule "远距离配送时间"
    when
        order: Order(
            distance > 100
        )
    then
        order.setEstimatedDeliveryTime(48); // 48小时
end
```

### 3.2 配送费用规则

**功能**：根据订单属性和配送要求，计算配送费用。

**规则示例**：

```
rule "标准配送费用"
    when
        order: Order(
            weight <= 5,
            distance <= 10
        )
    then
        order.setDeliveryFee(10);
end

rule "超重配送费用"
    when
        order: Order(
            weight > 5,
            weight <= 20
        )
    then
        order.setDeliveryFee(10 + (order.weight - 5) * 2);
end

rule "远距离配送费用"
    when
        order: Order(
            distance > 10,
            distance <= 50
        )
    then
        order.setDeliveryFee(10 + (order.distance - 10) * 0.5);
end

rule "加急配送费用"
    when
        order: Order(
            urgent == true
        )
    then
        order.setDeliveryFee(order.deliveryFee * 1.5);
end
```

### 3.3 配送方式规则

**功能**：根据订单属性和客户要求，选择合适的配送方式。

**规则示例**：

```
rule "快递配送"
    when
        order: Order(
            weight <= 5,
            distance <= 50,
            deliveryTimePreference == "standard"
        )
    then
        order.setDeliveryMethod("express");
end

rule "物流配送"
    when
        order: Order(
            weight > 50,
            distance > 100
        )
    then
        order.setDeliveryMethod("logistics");
end

rule "同城配送"
    when
        order: Order(
            distance <= 20,
            deliveryTimePreference == "same_day"
        )
    then
        order.setDeliveryMethod("same_city");
end
```

### 3.4 配送状态规则

**功能**：根据配送进展，更新配送状态。

**规则示例**：

```
rule "配送开始"
    when
        delivery: Delivery(
            status == "pending",
            startTime == null
        )
    then
        delivery.setStatus("in_transit");
        delivery.setStartTime(new Date());
end

rule "配送中"
    when
        delivery: Delivery(
            status == "in_transit",
            currentLocation != null,
            distanceToDestination > 0
        )
    then
        delivery.setStatus("delivering");
end

rule "配送完成"
    when
        delivery: Delivery(
            status == "delivering",
            distanceToDestination == 0,
            endTime == null
        )
    then
        delivery.setStatus("completed");
        delivery.setEndTime(new Date());
end
```

## 4. 库存规则

### 4.1 库存预警规则

**功能**：监控库存水平，当库存低于阈值时触发预警。

**规则示例**：

```
rule "库存不足预警"
    when
        product: Product(
            stock < minStockLevel
        )
    then
        product.setStockStatus("low");
        notificationService.sendAlert("库存不足", "产品 " + product.name + " 库存不足，当前库存: " + product.stock);
end

rule "库存严重不足预警"
    when
        product: Product(
            stock < minStockLevel * 0.5
        )
    then
        product.setStockStatus("critical");
        notificationService.sendAlert("库存严重不足", "产品 " + product.name + " 库存严重不足，当前库存: " + product.stock);
end

rule "库存过剩预警"
    when
        product: Product(
            stock > maxStockLevel
        )
    then
        product.setStockStatus("excess");
        notificationService.sendAlert("库存过剩", "产品 " + product.name + " 库存过剩，当前库存: " + product.stock);
end
```

### 4.2 库存分配规则

**功能**：根据订单需求和库存状态，分配库存。

**规则示例**：

```
rule "优先分配主仓库库存"
    when
        order: Order()
        warehouse: Warehouse(
            type == "main",
            stock >= order.totalQuantity
        )
    then
        order.setAssignedWarehouse(warehouse);
        warehouse.setStock(warehouse.stock - order.totalQuantity);
end

rule "分配备用仓库库存"
    when
        order: Order(
            assignedWarehouse == null
        )
        warehouse: Warehouse(
            type == "backup",
            stock >= order.totalQuantity
        )
    then
        order.setAssignedWarehouse(warehouse);
        warehouse.setStock(warehouse.stock - order.totalQuantity);
end

rule "多仓库分配"
    when
        order: Order(
            assignedWarehouse == null,
            totalQuantity > 0
        )
        $warehouses: List(size > 0) from warehouseService.getAvailableWarehouses()
    then
        int remainingQuantity = order.totalQuantity;
        for (Warehouse warehouse : $warehouses) {
            if (remainingQuantity <= 0) break;
            int allocateQuantity = Math.min(remainingQuantity, warehouse.stock);
            order.addWarehouseAllocation(warehouse, allocateQuantity);
            warehouse.setStock(warehouse.stock - allocateQuantity);
            remainingQuantity -= allocateQuantity;
        }
        if (remainingQuantity > 0) {
            order.setStatus("partial_fulfillment");
        }
end
```

### 4.3 库存盘点规则

**功能**：根据库存变化和时间周期，触发库存盘点。

**规则示例**：

```
rule "定期盘点"
    when
        $now: Date()
        $lastInventory: Inventory(
            productId == product.id,
            date < $now.minusDays(30)
        )
    then
        inventoryService.scheduleInventory(product.id);
end

rule "库存异常盘点"
    when
        product: Product(
            stockVariance > 0.1 // 库存差异超过10%
        )
    then
        inventoryService.scheduleInventory(product.id);
end

rule "高价值商品盘点"
    when
        product: Product(
            value > 1000,
            lastInventoryDate < now.minusDays(15)
        )
    then
        inventoryService.scheduleInventory(product.id);
end
```

### 4.4 库存成本规则

**功能**：根据库存水平和成本参数，计算库存成本。

**规则示例**：

```
rule "基础库存成本"
    when
        product: Product()
    then
        double holdingCost = product.stock * product.unitCost * 0.1 / 365; // 年持有成本10%
        product.setDailyHoldingCost(holdingCost);
end

rule "过期风险成本"
    when
        product: Product(
            expiryDate != null,
            daysUntilExpiry < 30
        )
    then
        double expiryRiskCost = product.stock * product.unitCost * 0.5;
        product.setExpiryRiskCost(expiryRiskCost);
end

rule "库存总成本"
    when
        product: Product()
    then
        double totalCost = product.dailyHoldingCost + product.expiryRiskCost;
        product.setTotalInventoryCost(totalCost);
end
```

## 5. 计费规则

### 5.1 基础费用规则

**功能**：计算订单的基础费用。

**规则示例**：

```
rule "基础订单费用"
    when
        order: Order()
    then
        double baseFee = order.amount * 0.05; // 基础费用为订单金额的5%
        order.setBaseFee(baseFee);
end

rule "最小订单费用"
    when
        order: Order(
            baseFee < 5
        )
    then
        order.setBaseFee(5); // 最小订单费用为5元
end

rule "大额订单折扣"
    when
        order: Order(
            amount > 10000
        )
    then
        double discountedFee = order.baseFee * 0.8; // 大额订单8折
        order.setBaseFee(discountedFee);
end
```

### 5.2 附加费用规则

**功能**：根据订单属性和服务要求，计算附加费用。

**规则示例**：

```
rule "超重附加费"
    when
        order: Order(
            weight > 20
        )
    then
        double overweightFee = (order.weight - 20) * 2;
        order.addAdditionalFee("超重附加费", overweightFee);
end

rule "超长附加费"
    when
        order: Order(
            length > 150
        )
    then
        double overlengthFee = (order.length - 150) * 0.5;
        order.addAdditionalFee("超长附加费", overlengthFee);
end

rule "特殊处理附加费"
    when
        order: Order(
            specialHandling == true
        )
    then
        order.addAdditionalFee("特殊处理费", 50);
end
```

### 5.3 折扣规则

**功能**：根据订单属性和促销策略，计算折扣。

**规则示例**：

```
rule "新客户折扣"
    when
        order: Order(
            customer.isNew == true
        )
    then
        order.setDiscount(0.1); // 新客户10%折扣
end

rule "VIP客户折扣"
    when
        order: Order(
            customer.vipLevel >= 3
        )
    then
        order.setDiscount(0.15); // VIP客户15%折扣
end

rule "促销活动折扣"
    when
        order: Order(
            createdAt >= promotion.startDate,
            createdAt <= promotion.endDate
        )
    then
        order.setDiscount(promotion.discountRate);
end
```

### 5.4 税费计算规则

**功能**：根据订单属性和税法规定，计算税费。

**规则示例**：

```
rule "增值税计算"
    when
        order: Order()
    then
        double vat = order.subtotal * 0.13; // 增值税13%
        order.setVat(vat);
end

rule "消费税计算"
    when
        order: Order(
            containsLuxuryItems == true
        )
    then
        double consumptionTax = order.subtotal * 0.05; // 消费税5%
        order.setConsumptionTax(consumptionTax);
end

rule "关税计算"
    when
        order: Order(
            isImported == true
        )
    then
        double customsDuty = order.subtotal * 0.1; // 关税10%
        order.setCustomsDuty(customsDuty);
end

rule "总税费计算"
    when
        order: Order()
    then
        double totalTax = order.vat + order.consumptionTax + order.customsDuty;
        order.setTotalTax(totalTax);
end
```

## 6. 总结

业务规则场景是物流业务规则引擎的核心内容，涵盖了物流业务的各个环节。通过合理的规则设计，可以实现物流业务流程的自动化和智能化，提高业务处理效率和准确性。在实际应用中，需要根据具体的业务需求和系统环境，设计和实现适合的业务规则，确保规则的正确性和可维护性。
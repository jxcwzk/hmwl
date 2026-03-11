# 规则定义语法

## 1. 规则结构

规则是规则引擎的基本组成单位，每个规则由规则名称、条件和动作三部分组成。规则的基本结构如下：

```
rule "规则名称"
    when
        条件
    then
        动作
end
```

### 1.1 规则名称

规则名称是规则的唯一标识符，用于区分不同的规则。规则名称应该简洁明了，能够准确描述规则的功能。

**示例**：
```
rule "高优先级订单处理"
    when
        order.priority == "high"
    then
        order.setProcessingTime("immediate");
end
```

### 1.2 条件部分

条件部分（when）定义了规则触发的条件，当条件满足时，规则会被触发执行。条件可以是简单的属性比较，也可以是复杂的逻辑组合。

**示例**：
```
rule "超重订单"
    when
        order.weight > 50
        order.distance > 100
    then
        order.setDeliveryFee(order.getDeliveryFee() * 1.5);
end
```

### 1.3 动作部分

动作部分（then）定义了规则触发后执行的操作，动作可以是赋值、计算、调用方法或服务等。

**示例**：
```
rule "加急订单"
    when
        order.urgent == true
    then
        order.setPriority("high");
        order.setProcessingTime("immediate");
end
```

## 2. 规则条件

规则条件是规则触发的判断依据，支持多种类型的条件表达式。

### 2.1 简单条件

简单条件是最基本的条件类型，用于比较属性值。

**语法**：
```
对象.属性 操作符 值
```

**操作符**：
- `==`：等于
- `!=`：不等于
- `>`：大于
- `>=`：大于等于
- `<`：小于
- `<=`：小于等于
- `contains`：包含
- `not contains`：不包含
- `matches`：匹配正则表达式

**示例**：
```
order.amount > 1000
order.status == "pending"
order.tags contains "express"
```

### 2.2 复合条件

复合条件是由多个简单条件通过逻辑运算符组合而成的条件。

**逻辑运算符**：
- `&&` 或 `and`：逻辑与
- `||` 或 `or`：逻辑或
- `!` 或 `not`：逻辑非

**示例**：
```
order.amount > 1000 && order.priority == "high"
order.status == "pending" || order.status == "processing"
!order.urgent
```

### 2.3 嵌套条件

嵌套条件是将条件组合成更复杂的逻辑结构。

**示例**：
```
(order.amount > 1000 && order.priority == "high") || (order.urgent == true && order.distance < 50)
```

### 2.4 表达式条件

表达式条件是使用表达式语言编写的条件，可以包含更复杂的逻辑和计算。

**示例**：
```
order.weight * order.distance > 1000
order.items.size() > 5
```

## 3. 规则动作

规则动作是规则触发后执行的操作，支持多种类型的动作。

### 3.1 赋值动作

赋值动作用于设置对象的属性值。

**语法**：
```
对象.属性 = 值;
```

**示例**：
```
order.priority = "high";
order.deliveryFee = 20;
```

### 3.2 计算动作

计算动作用于执行计算操作并将结果赋值给属性。

**示例**：
```
order.deliveryFee = order.weight * 2 + order.distance * 0.5;
order.totalAmount = order.subtotal + order.tax + order.deliveryFee;
```

### 3.3 调用动作

调用动作用于调用对象的方法或外部服务。

**示例**：
```
order.calculateTotal();
deliveryService.assignDriver(order);
```

### 3.4 通知动作

通知动作用于发送通知或消息。

**示例**：
```
notificationService.sendEmail(order.customer.email, "订单已处理");
messageQueue.send("order-processed", order.id);
```

### 3.5 流程动作

流程动作用于触发业务流程。

**示例**：
```
workflowService.startProcess("order-fulfillment", order.id);
```

## 4. 规则变量

规则变量用于在规则中存储和使用临时数据。

### 4.1 局部变量

局部变量只在规则内部有效，用于存储规则执行过程中的临时数据。

**语法**：
```
$变量名 : 表达式
```

**示例**：
```
rule "计算配送费用"
    when
        $order : Order()
        $weight : $order.weight
        $distance : $order.distance
    then
        $order.setDeliveryFee($weight * 2 + $distance * 0.5);
end
```

### 4.2 全局变量

全局变量在整个规则引擎中有效，用于存储全局配置或共享数据。

**语法**：
```
global 类型 变量名;
```

**示例**：
```
global NotificationService notificationService;

then
    notificationService.sendEmail(order.customer.email, "订单已处理");
end
```

## 5. 规则属性

规则属性用于控制规则的行为和执行顺序。

### 5.1 优先级

优先级用于控制规则的执行顺序，优先级高的规则先执行。

**语法**：
```
rule "规则名称"
    salience 优先级
    when
        条件
    then
        动作
end
```

**示例**：
```
rule "高优先级订单"
    salience 100
    when
        order.priority == "high"
    then
        order.setProcessingTime("immediate");
end

rule "普通订单"
    salience 50
    when
        order.priority == "normal"
    then
        order.setProcessingTime("standard");
end
```

### 5.2 激活组

激活组用于控制规则的互斥执行，同一激活组中的规则只能有一个被执行。

**语法**：
```
rule "规则名称"
    activation-group "组名"
    when
        条件
    then
        动作
end
```

**示例**：
```
rule "加急处理"
    activation-group "processing"
    when
        order.urgent == true
    then
        order.setProcessingTime("immediate");
end

rule "标准处理"
    activation-group "processing"
    when
        order.urgent == false
    then
        order.setProcessingTime("standard");
end
```

### 5.3 日期生效

日期生效用于控制规则的生效和失效时间。

**语法**：
```
rule "规则名称"
    date-effective "生效日期"
    date-expires "失效日期"
    when
        条件
    then
        动作
end
```

**示例**：
```
rule "节假日促销"
    date-effective "2023-12-01"
    date-expires "2023-12-31"
    when
        order.amount > 500
    then
        order.setDiscount(0.1);
end
```

## 6. 规则模板

规则模板是预定义的规则结构，用于快速创建相似的规则。

### 6.1 模板定义

**语法**：
```
template "模板名称"
    parameters: 参数1, 参数2, ...
    rule "@name"
        when
            @condition
        then
            @action
    end
end
```

**示例**：
```
template "配送费用规则"
    parameters: weightThreshold, distanceThreshold, feeMultiplier
    rule "配送费用规则_@weightThreshold_@distanceThreshold"
        when
            order.weight > @weightThreshold
            order.distance > @distanceThreshold
        then
            order.setDeliveryFee(order.getDeliveryFee() * @feeMultiplier);
    end
end
```

### 6.2 模板实例化

**语法**：
```
apply template "模板名称" with: 参数1=值1, 参数2=值2, ...
```

**示例**：
```
apply template "配送费用规则" with: weightThreshold=50, distanceThreshold=100, feeMultiplier=1.5
apply template "配送费用规则" with: weightThreshold=100, distanceThreshold=200, feeMultiplier=2.0
```

## 7. 规则 DSL

规则 DSL（Domain Specific Language）是专门为规则定义设计的语言，提供了更简洁、直观的规则定义方式。

### 7.1 MVEL DSL

MVEL（MVFLEX Expression Language）是一种轻量级的表达式语言，常用于规则引擎中。

**示例**：
```
rule "高优先级订单"
    when
        order: Order(priority == "high", amount > 1000)
    then
        order.setProcessingTime("immediate");
        order.setAssignedTo("priority_team");
end
```

### 7.2 Drools DSL

Drools DSL是Drools规则引擎提供的领域特定语言，允许使用更自然的语言描述规则。

**示例**：
```
rule "高优先级订单"
    when
        an order with priority is "high"
        and the order amount is greater than 1000
    then
        set the processing time of the order to "immediate"
        assign the order to "priority_team"
end
```

## 8. 规则测试

规则测试是确保规则正确执行的重要手段，通过测试可以验证规则的逻辑是否正确。

### 8.1 单元测试

单元测试用于测试单个规则的执行逻辑。

**示例**：
```java
@Test
public void testHighPriorityOrderRule() {
    // 创建订单对象
    Order order = new Order();
    order.setPriority("high");
    order.setAmount(1500);
    
    // 执行规则
    KieSession session = kieContainer.newKieSession();
    session.insert(order);
    session.fireAllRules();
    session.dispose();
    
    // 验证结果
    assertEquals("immediate", order.getProcessingTime());
    assertEquals("priority_team", order.getAssignedTo());
}
```

### 8.2 集成测试

集成测试用于测试规则与其他系统的集成。

**示例**：
```java
@Test
public void testOrderProcessing() {
    // 创建订单对象
    Order order = new Order();
    order.setPriority("high");
    order.setAmount(1500);
    order.setCustomer(new Customer("john@example.com"));
    
    // 执行规则
    RuleEngineService ruleEngineService = new RuleEngineService();
    ruleEngineService.executeRules(order);
    
    // 验证结果
    assertEquals("immediate", order.getProcessingTime());
    assertEquals("priority_team", order.getAssignedTo());
    // 验证通知是否发送
    verify(notificationService).sendEmail("john@example.com", "订单已处理");
}
```

## 9. 规则版本控制

规则版本控制是管理规则变更的重要手段，通过版本控制可以追踪规则的变更历史。

### 9.1 版本号管理

规则版本号通常采用语义化版本号，格式为 `major.minor.patch`。

- **major**：重大变更，不兼容的API变更
- **minor**：新功能，向后兼容
- **patch**：bug修复，向后兼容

### 9.2 变更记录

变更记录用于记录规则的变更历史，包括变更内容、变更原因、变更时间等。

**示例**：
```
版本 1.0.0
- 初始版本
- 包含订单处理规则

版本 1.1.0
- 添加路由规划规则
- 优化配送费用计算规则

版本 1.1.1
- 修复订单优先级规则的bug
```

## 10. 规则文档

规则文档是规则管理的重要组成部分，用于记录规则的设计意图、使用方法和维护说明。

### 10.1 规则说明

规则说明用于描述规则的功能、适用场景和执行效果。

**示例**：
```
# 高优先级订单处理规则

## 功能
处理高优先级订单，设置立即处理时间和优先级团队。

## 适用场景
- 订单优先级为high
- 订单金额大于1000

## 执行效果
- 设置处理时间为immediate
- 分配给priority_team

## 依赖
- Order对象必须有priority、amount、processingTime、assignedTo属性
```

### 10.2 规则流程图

规则流程图用于可视化规则的执行逻辑。

**示例**：
```
┌─────────────────────┐
│ 订单优先级为high？ │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│ 订单金额大于1000？ │
└──────────┬──────────┘
           │
           ▼
┌────────────────────────┐
│ 设置处理时间为immediate │
└──────────┬─────────────┘
           │
           ▼
┌────────────────────────┐
│ 分配给priority_team    │
└────────────────────────┘
```

## 11. 总结

规则定义语法是规则引擎的核心组成部分，通过合理的规则定义语法，可以创建清晰、可维护的业务规则。在实际应用中，需要根据具体的规则引擎和业务需求，选择合适的规则定义语法，确保规则的正确性和可维护性。
# 订单编号生成规范

## 1. 需求分析

### 1.1 业务需求
- 订单创建时自动生成唯一的订单编号
- 订单编号应包含时间信息，便于识别和排序
- 订单编号应具有一定的业务含义
- 订单编号长度适中，便于存储和处理

### 1.2 技术需求
- 生成的订单编号必须唯一
- 生成过程应高效，不影响订单创建性能
- 应考虑并发情况下的唯一性保证
- 实现方案应与现有代码架构兼容

## 2. 编码规则

### 2.1 订单编号格式

```
HM + YYYYMMDD + XXXXXX
```

- **HM**：系统前缀，代表"hmwl"物流系统
- **YYYYMMDD**：日期部分，格式为年月日，如20260309
- **XXXXXX**：6位序列号，从000001开始，每日重置

### 2.2 编码规则说明

1. **前缀**：使用"HM"作为系统标识，便于与其他系统的订单编号区分
2. **日期部分**：包含完整的年月日信息，确保订单编号的时间可追溯性
3. **序列号**：6位数字，每日从000001开始递增，确保同一天内的订单编号唯一
4. **长度**：总长度为16位，格式固定，便于系统处理和人工识别

### 2.3 示例

- 2026年3月9日的第一个订单：HM20260309000001
- 2026年3月9日的第二个订单：HM20260309000002
- 2026年3月10日的第一个订单：HM20260310000001

## 3. 实现方案

### 3.1 技术实现

1. **实现位置**：在`OrderServiceImpl`的`save`方法中重写，在保存订单前生成订单编号

2. **核心逻辑**：
   - 获取当前日期，格式化为YYYYMMDD
   - 查询当天已存在的订单数量，确定下一个序列号
   - 生成完整的订单编号
   - 设置到订单对象中

3. **并发处理**：
   - 使用数据库事务确保查询和保存的原子性
   - 或使用分布式锁保证序列号生成的唯一性

### 3.2 代码实现

```java
package com.hmwl.service.impl;

import com.hmwl.entity.Order;
import com.hmwl.mapper.OrderMapper;
import com.hmwl.service.OrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Override
    @Transactional
    public boolean save(Order order) {
        // 生成订单编号
        if (order.getOrderNo() == null || order.getOrderNo().isEmpty()) {
            String orderNo = generateOrderNo();
            order.setOrderNo(orderNo);
        }
        return super.save(order);
    }

    /**
     * 生成订单编号
     * 格式：HM + YYYYMMDD + 6位序列号
     */
    private String generateOrderNo() {
        // 1. 生成日期部分
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String datePart = sdf.format(new Date());
        
        // 2. 生成序列号部分
        // 构建查询条件：当天的订单
        String prefix = "HM" + datePart;
        
        // 查询当天已存在的订单数量
        List<Order> todayOrders = baseMapper.selectList(
            new QueryWrapper<Order>()
                .likeRight("order_no", prefix)
        );
        
        int sequence = todayOrders.size() + 1;
        String sequencePart = String.format("%06d", sequence);
        
        // 3. 组合订单编号
        return prefix + sequencePart;
    }
}
```

### 3.3 依赖和配置

- 无需额外依赖，使用现有Spring Boot和MyBatis-Plus框架
- 确保数据库表中`order_no`字段设置为唯一索引，防止重复

## 4. 测试计划

### 4.1 单元测试

1. **测试订单编号生成**：
   - 测试正常情况下的订单编号生成
   - 测试同一天内多个订单的编号生成
   - 测试跨天的订单编号生成

2. **测试并发场景**：
   - 使用多线程同时创建订单，验证订单编号的唯一性

### 4.2 集成测试

1. **测试订单创建API**：
   - 调用订单创建接口，验证返回的订单包含正确的订单编号
   - 验证订单编号格式符合规范

2. **测试数据库唯一性**：
   - 尝试插入重复订单编号，验证数据库约束是否生效

## 5. 验收标准

1. **功能验收**：
   - 订单创建时自动生成订单编号
   - 订单编号格式符合规范
   - 订单编号唯一，无重复

2. **性能验收**：
   - 订单创建性能不受订单编号生成影响
   - 并发情况下订单编号生成正常

3. **可靠性验收**：
   - 系统重启后订单编号生成正常
   - 日期变更后序列号正确重置

## 6. 风险评估

### 6.1 潜在风险

1. **并发冲突**：高并发情况下可能出现序列号重复
2. **性能问题**：频繁查询当天订单数量可能影响性能
3. **数据库压力**：每次创建订单都需要查询数据库

### 6.2 风险缓解措施

1. **并发控制**：使用数据库事务或分布式锁
2. **性能优化**：考虑使用Redis缓存当天序列号
3. **数据库优化**：为`order_no`字段创建索引，提高查询性能

## 7. 总结

本规范定义了订单编号的生成规则和实现方案，确保订单编号的唯一性、可读性和可排序性。通过在订单创建时自动生成符合规范的订单编号，提高了系统的自动化程度和用户体验。

实现方案采用了简单可靠的方式，与现有代码架构完全兼容，无需额外依赖。同时，通过测试计划和风险评估，确保了方案的可靠性和性能。
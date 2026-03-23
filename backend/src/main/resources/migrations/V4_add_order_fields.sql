-- 物流系统V4流程优化 - 数据库迁移脚本
-- 执行时间: 2026-03-17
-- 说明: 为支持V4流程新增字段

-- 1. 添加提货司机字段
ALTER TABLE orders ADD COLUMN pickup_driver_id BIGINT COMMENT '提货司机ID';

-- 2. 添加配送司机字段
ALTER TABLE orders ADD COLUMN delivery_driver_id BIGINT COMMENT '配送司机ID';

-- 3. 添加选中网点字段
ALTER TABLE orders ADD COLUMN selected_network_id BIGINT COMMENT '选中的网点ID';

-- 4. 添加价格状态字段
ALTER TABLE orders ADD COLUMN pricing_status INT DEFAULT 0 COMMENT '价格状态: 0-待报价 1-已报价 2-已选择';

-- 5. 添加仓储状态字段
ALTER TABLE orders ADD COLUMN warehouse_status INT DEFAULT 0 COMMENT '仓储状态: 0-待入库 1-已入库';

-- 6. 添加价格确认时间
ALTER TABLE orders ADD COLUMN price_confirmed_time DATETIME COMMENT '价格确认时间';

-- 7. 添加提货时间
ALTER TABLE orders ADD COLUMN picked_up_time DATETIME COMMENT '提货时间';

-- 8. 添加送达网点时间
ALTER TABLE orders ADD COLUMN delivered_to_network_time DATETIME COMMENT '送达网点时间';

-- 9. 添加网点确认时间
ALTER TABLE orders ADD COLUMN warehouse_confirm_time DATETIME COMMENT '网点确认时间';

-- 10. 添加索引
CREATE INDEX idx_pickup_driver ON orders(pickup_driver_id);
CREATE INDEX idx_delivery_driver ON orders(delivery_driver_id);
CREATE INDEX idx_selected_network ON orders(selected_network_id);
CREATE INDEX idx_pricing_status ON orders(pricing_status);
CREATE INDEX idx_warehouse_status ON orders(warehouse_status);

-- 回滚脚本（如果需要回滚）
-- ALTER TABLE orders DROP COLUMN pickup_driver_id;
-- ALTER TABLE orders DROP COLUMN delivery_driver_id;
-- ALTER TABLE orders DROP COLUMN selected_network_id;
-- ALTER TABLE orders DROP COLUMN pricing_status;
-- ALTER TABLE orders DROP COLUMN warehouse_status;
-- ALTER TABLE orders DROP COLUMN price_confirmed_time;
-- ALTER TABLE orders DROP COLUMN picked_up_time;
-- ALTER TABLE orders DROP COLUMN delivered_to_network_time;
-- ALTER TABLE orders DROP COLUMN warehouse_confirm_time;
-- DROP INDEX idx_pickup_driver ON orders;
-- DROP INDEX idx_delivery_driver ON orders;
-- DROP INDEX idx_selected_network ON orders;
-- DROP INDEX idx_pricing_status ON orders;
-- DROP INDEX idx_warehouse_status ON orders;
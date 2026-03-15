-- 添加 orders 表缺失的字段
ALTER TABLE orders
    ADD COLUMN driver_id BIGINT DEFAULT NULL AFTER recipient_id,
    ADD COLUMN network_point_id BIGINT DEFAULT NULL AFTER driver_id,
    ADD COLUMN base_fee DOUBLE DEFAULT NULL AFTER volume,
    ADD COLUMN coefficient DOUBLE DEFAULT NULL AFTER base_fee,
    ADD COLUMN logistics_status VARCHAR(50) DEFAULT NULL AFTER status,
    ADD COLUMN logistics_progress TEXT DEFAULT NULL AFTER logistics_status;

-- 添加 user 表缺失的字段
ALTER TABLE user
    ADD COLUMN driver_id BIGINT DEFAULT NULL AFTER wechat,
    ADD COLUMN network_point_id BIGINT DEFAULT NULL AFTER driver_id,
    ADD COLUMN openid VARCHAR(100) DEFAULT NULL,
    ADD COLUMN status INT DEFAULT NULL,
    ADD COLUMN business_user_id BIGINT DEFAULT NULL;

-- 添加 business_customer 表缺失的字段
ALTER TABLE business_customer ADD COLUMN phone VARCHAR(20) DEFAULT NULL;

-- 添加 settlement 表缺失的字段
ALTER TABLE settlement
    ADD COLUMN order_no VARCHAR(50) DEFAULT NULL,
    ADD COLUMN customer_name VARCHAR(100) DEFAULT NULL,
    ADD COLUMN order_amount DOUBLE DEFAULT NULL,
    ADD COLUMN recommended_price DOUBLE DEFAULT NULL,
    ADD COLUMN final_amount DOUBLE DEFAULT NULL,
    ADD COLUMN invoice_no VARCHAR(50) DEFAULT NULL;

-- 添加索引
ALTER TABLE orders ADD INDEX idx_driver_id (driver_id);
ALTER TABLE orders ADD INDEX idx_network_point_id (network_point_id);
ALTER TABLE orders ADD INDEX idx_logistics_status (logistics_status);

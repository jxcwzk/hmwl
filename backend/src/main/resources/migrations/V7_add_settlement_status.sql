-- V7 添加结算状态字段
-- 执行时间: 2026-04-01

-- 添加结算状态字段 (0-未结算, 1-已结算)
ALTER TABLE orders ADD COLUMN settlement_status INT DEFAULT 0 COMMENT '结算状态 0-未结算 1-已结算';

-- 回滚脚本
-- ALTER TABLE orders DROP COLUMN settlement_status;

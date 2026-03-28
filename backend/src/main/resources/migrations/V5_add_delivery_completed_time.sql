-- V5 添加配送完成时间字段
-- 执行时间: 2026-03-26

-- 添加配送完成时间字段
ALTER TABLE orders ADD COLUMN delivery_completed_time DATETIME COMMENT '配送完成时间';

-- 回滚脚本
-- ALTER TABLE orders DROP COLUMN delivery_completed_time;

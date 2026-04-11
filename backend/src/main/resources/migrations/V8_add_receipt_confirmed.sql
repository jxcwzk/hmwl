-- V8 添加回单确认状态字段
-- 执行时间: 2026-04-01

-- 添加回单确认状态字段 (0-未确认, 1-已确认)
ALTER TABLE orders ADD COLUMN receipt_confirmed INT DEFAULT 0 COMMENT '回单确认状态 0-未确认 1-已确认';

-- 回滚脚本
-- ALTER TABLE orders DROP COLUMN receipt_confirmed;

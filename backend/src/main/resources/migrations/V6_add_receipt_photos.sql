-- V6 添加回单照片字段
-- 执行时间: 2026-04-01

-- 添加回单照片字段 (JSON格式存储多张照片的base64编码)
ALTER TABLE orders ADD COLUMN receipt_photos TEXT COMMENT '回单照片(base64 JSON数组)';

-- 回滚脚本
-- ALTER TABLE orders DROP COLUMN receipt_photos;

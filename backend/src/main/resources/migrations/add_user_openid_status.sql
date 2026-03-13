-- 为user表添加openid和status字段
-- 用于小程序微信登录

ALTER TABLE `user` ADD COLUMN `openid` varchar(100) DEFAULT NULL COMMENT '微信openid';
ALTER TABLE `user` ADD COLUMN `status` int DEFAULT 0 COMMENT '用户状态: 0-未分配身份, 1-已激活';

-- 添加索引
ALTER TABLE `user` ADD INDEX `idx_openid` (`openid`);

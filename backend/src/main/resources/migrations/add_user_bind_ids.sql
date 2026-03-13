-- 为user表添加关联ID，用于绑定业务用户和司机
ALTER TABLE `user` ADD COLUMN `business_user_id` bigint DEFAULT NULL COMMENT '关联的业务用户ID';
ALTER TABLE `user` ADD COLUMN `driver_id` bigint DEFAULT NULL COMMENT '关联的司机ID';

-- 添加索引
ALTER TABLE `user` ADD INDEX `idx_business_user_id` (`business_user_id`);
ALTER TABLE `user` ADD INDEX `idx_driver_id` (`driver_id`);

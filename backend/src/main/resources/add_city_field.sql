-- 使用数据库
USE hmwl;

-- 为network_point表添加city字段
ALTER TABLE `network_point` ADD COLUMN `city` varchar(50) DEFAULT NULL AFTER `name`;

-- 更新测试数据，为city字段添加值
UPDATE `network_point` SET `city` = '北京市' WHERE `code` = 'NP001';
UPDATE `network_point` SET `city` = '上海市' WHERE `code` = 'NP002';
UPDATE `network_point` SET `city` = '北京市' WHERE `code` = 'NP003';

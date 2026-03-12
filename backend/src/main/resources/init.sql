-- 创建数据库
CREATE DATABASE IF NOT EXISTS hmwl DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE hmwl;

-- 创建系统用户表 (无依赖)
CREATE TABLE IF NOT EXISTS `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) DEFAULT NULL,
  `user_type` int DEFAULT NULL,
  `remark` varchar(200) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `wechat` varchar(50) DEFAULT NULL,
  `password` varchar(100) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_username` (`username`),
  KEY `idx_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建业务用户表 (无依赖)
CREATE TABLE IF NOT EXISTS `business_user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `wechat` varchar(50) DEFAULT NULL,
  `remark` varchar(200) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_username` (`username`),
  KEY `idx_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建车辆表 (无依赖)
CREATE TABLE IF NOT EXISTS `vehicle` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `license_plate` varchar(20) DEFAULT NULL,
  `vehicle_type` varchar(50) DEFAULT NULL,
  `load_capacity` double DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_license_plate` (`license_plate`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建网点表 (无依赖)
CREATE TABLE IF NOT EXISTS `network_point` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(20) DEFAULT NULL,
  `name` varchar(100) DEFAULT NULL,
  `contact_person` varchar(50) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `address` varchar(200) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_code` (`code`),
  KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建业务客户表 (依赖 business_user)
CREATE TABLE IF NOT EXISTS `business_customer` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `customer_name` varchar(100) DEFAULT NULL,
  `contact` varchar(50) DEFAULT NULL,
  `address` varchar(200) DEFAULT NULL,
  `remark` varchar(200) DEFAULT NULL,
  `business_user_id` bigint DEFAULT NULL,
  `type` int DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_business_user_id` (`business_user_id`),
  CONSTRAINT `fk_business_customer_user` FOREIGN KEY (`business_user_id`) REFERENCES `business_user` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建业务收件人表 (依赖 business_user)
CREATE TABLE IF NOT EXISTS `business_recipient` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `business_user_id` bigint DEFAULT NULL,
  `name` varchar(50) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `address` varchar(200) DEFAULT NULL,
  `remark` varchar(200) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_business_user_id` (`business_user_id`),
  CONSTRAINT `fk_business_recipient_user` FOREIGN KEY (`business_user_id`) REFERENCES `business_user` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建司机表 (依赖 vehicle)
CREATE TABLE IF NOT EXISTS `driver` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `id_card` varchar(20) DEFAULT NULL,
  `vehicle_id` bigint DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_vehicle_id` (`vehicle_id`),
  CONSTRAINT `fk_driver_vehicle` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicle` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建线路表 (依赖 network_point)
CREATE TABLE IF NOT EXISTS `route` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `start_point_id` bigint DEFAULT NULL,
  `end_point_id` bigint DEFAULT NULL,
  `is_trunk` boolean DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_start_point_id` (`start_point_id`),
  KEY `idx_end_point_id` (`end_point_id`),
  CONSTRAINT `fk_route_start_point` FOREIGN KEY (`start_point_id`) REFERENCES `network_point` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_route_end_point` FOREIGN KEY (`end_point_id`) REFERENCES `network_point` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建订单表 (依赖 network_point, business_user, business_customer)
CREATE TABLE IF NOT EXISTS `orders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_no` varchar(50) DEFAULT NULL,
  `order_type` int DEFAULT NULL,
  `start_network_id` bigint DEFAULT NULL,
  `end_network_id` bigint DEFAULT NULL,
  `business_user_id` bigint DEFAULT NULL,
  `sender_id` bigint DEFAULT NULL,
  `recipient_id` bigint DEFAULT NULL,
  `sender_name` varchar(50) DEFAULT NULL,
  `sender_phone` varchar(20) DEFAULT NULL,
  `sender_address` varchar(200) DEFAULT NULL,
  `receiver_name` varchar(50) DEFAULT NULL,
  `receiver_phone` varchar(20) DEFAULT NULL,
  `receiver_address` varchar(200) DEFAULT NULL,
  `goods_name` varchar(100) DEFAULT NULL,
  `quantity` int DEFAULT NULL,
  `weight` double DEFAULT NULL,
  `volume` double DEFAULT NULL,
  `distance` double DEFAULT NULL,
  `total_fee` double DEFAULT NULL,
  `payment_method` int DEFAULT NULL,
  `network_payment_method` int DEFAULT NULL,
  `qr_code_url` varchar(500) DEFAULT NULL,
  `status` int DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_order_no` (`order_no`),
  KEY `idx_start_network_id` (`start_network_id`),
  KEY `idx_end_network_id` (`end_network_id`),
  KEY `idx_business_user_id` (`business_user_id`),
  KEY `idx_sender_id` (`sender_id`),
  KEY `idx_recipient_id` (`recipient_id`),
  KEY `idx_status` (`status`),
  CONSTRAINT `fk_order_start_network` FOREIGN KEY (`start_network_id`) REFERENCES `network_point` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_order_end_network` FOREIGN KEY (`end_network_id`) REFERENCES `network_point` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_order_business_user` FOREIGN KEY (`business_user_id`) REFERENCES `business_user` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_order_sender` FOREIGN KEY (`sender_id`) REFERENCES `business_customer` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_order_recipient` FOREIGN KEY (`recipient_id`) REFERENCES `business_customer` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建结算表 (依赖 orders, driver, network_point)
CREATE TABLE IF NOT EXISTS `settlement` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `settlement_no` varchar(50) DEFAULT NULL,
  `type` int DEFAULT NULL,
  `order_id` bigint DEFAULT NULL,
  `customer_id` bigint DEFAULT NULL,
  `driver_id` bigint DEFAULT NULL,
  `start_network_id` bigint DEFAULT NULL,
  `end_network_id` bigint DEFAULT NULL,
  `amount` double DEFAULT NULL,
  `status` int DEFAULT NULL,
  `payment_method` int DEFAULT NULL,
  `commission` double DEFAULT NULL,
  `transfer_fee` double DEFAULT NULL,
  `trunk_fee` double DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_settlement_no` (`settlement_no`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_customer_id` (`customer_id`),
  KEY `idx_driver_id` (`driver_id`),
  KEY `idx_start_network_id` (`start_network_id`),
  KEY `idx_end_network_id` (`end_network_id`),
  KEY `idx_status` (`status`),
  CONSTRAINT `fk_settlement_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_settlement_driver` FOREIGN KEY (`driver_id`) REFERENCES `driver` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_settlement_start_network` FOREIGN KEY (`start_network_id`) REFERENCES `network_point` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_settlement_end_network` FOREIGN KEY (`end_network_id`) REFERENCES `network_point` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建发票表 (依赖 settlement, orders)
CREATE TABLE IF NOT EXISTS `invoice` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `invoice_no` varchar(50) DEFAULT NULL,
  `settlement_id` bigint DEFAULT NULL,
  `order_id` bigint DEFAULT NULL,
  `customer_id` bigint DEFAULT NULL,
  `customer_name` varchar(100) DEFAULT NULL,
  `amount` double DEFAULT NULL,
  `status` int DEFAULT NULL COMMENT '0-待开 1-已开',
  `invoice_date` datetime DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_invoice_no` (`invoice_no`),
  KEY `idx_settlement_id` (`settlement_id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_customer_id` (`customer_id`),
  KEY `idx_status` (`status`),
  CONSTRAINT `fk_invoice_settlement` FOREIGN KEY (`settlement_id`) REFERENCES `settlement` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_invoice_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建网点结算表 (依赖 network_point)
CREATE TABLE IF NOT EXISTS `branch_settle` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `settlement_no` varchar(50) DEFAULT NULL,
  `start_network_id` bigint DEFAULT NULL,
  `end_network_id` bigint DEFAULT NULL,
  `transfer_fee` double DEFAULT NULL,
  `trunk_fee` double DEFAULT NULL,
  `status` int DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_settlement_no` (`settlement_no`),
  KEY `idx_start_network_id` (`start_network_id`),
  KEY `idx_end_network_id` (`end_network_id`),
  KEY `idx_status` (`status`),
  CONSTRAINT `fk_branch_settle_start_network` FOREIGN KEY (`start_network_id`) REFERENCES `network_point` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_branch_settle_end_network` FOREIGN KEY (`end_network_id`) REFERENCES `network_point` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建回单图片表 (依赖 orders)
CREATE TABLE IF NOT EXISTS `receipt_image` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint DEFAULT NULL,
  `image_url` varchar(1000) DEFAULT NULL,
  `status` int DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  CONSTRAINT `fk_receipt_image_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建发货单图片表 (依赖 orders)
CREATE TABLE IF NOT EXISTS `sender_image` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint DEFAULT NULL,
  `image_url` varchar(1000) DEFAULT NULL,
  `status` int DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  CONSTRAINT `fk_sender_image_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

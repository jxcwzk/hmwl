-- 物流系统数据库初始化脚本

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `logistics` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `logistics`;

-- 创建客户表
CREATE TABLE IF NOT EXISTS `customer` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(50) NOT NULL,
    `phone` VARCHAR(20) NOT NULL,
    `address` VARCHAR(200) NOT NULL,
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_name` (`name`),
    INDEX `idx_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 创建收件人表
CREATE TABLE IF NOT EXISTS `recipient` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(50) NOT NULL,
    `phone` VARCHAR(20) NOT NULL,
    `address` VARCHAR(200) NOT NULL,
    `customer_id` BIGINT NOT NULL,
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_customer_id` (`customer_id`),
    INDEX `idx_name` (`name`),
    INDEX `idx_phone` (`phone`),
    FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 创建订单表
CREATE TABLE IF NOT EXISTS `order` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `order_no` VARCHAR(32) NOT NULL,
    `customer_id` BIGINT NOT NULL,
    `recipient_id` BIGINT NOT NULL,
    `status` INT NOT NULL COMMENT '0: 待处理, 1: 处理中, 2: 已完成, 3: 已取消',
    `total_amount` DECIMAL(10,2) NOT NULL,
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `idx_order_no` (`order_no`),
    INDEX `idx_customer_id` (`customer_id`),
    INDEX `idx_recipient_id` (`recipient_id`),
    INDEX `idx_status` (`status`),
    FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`recipient_id`) REFERENCES `recipient` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 创建订单商品表
CREATE TABLE IF NOT EXISTS `order_item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `order_id` BIGINT NOT NULL,
    `product_id` BIGINT NOT NULL,
    `quantity` INT NOT NULL,
    `price` DECIMAL(10,2) NOT NULL,
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_order_id` (`order_id`),
    INDEX `idx_product_id` (`product_id`),
    FOREIGN KEY (`order_id`) REFERENCES `order` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 创建仓库表
CREATE TABLE IF NOT EXISTS `warehouse` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(50) NOT NULL,
    `address` VARCHAR(200) NOT NULL,
    `capacity` INT NOT NULL,
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_name` (`name`),
    INDEX `idx_address` (`address`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 创建产品表
CREATE TABLE IF NOT EXISTS `product` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(100) NOT NULL,
    `sku` VARCHAR(50) NOT NULL,
    `price` DECIMAL(10,2) NOT NULL,
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `idx_sku` (`sku`),
    INDEX `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 创建库存表
CREATE TABLE IF NOT EXISTS `inventory` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `product_id` BIGINT NOT NULL,
    `warehouse_id` BIGINT NOT NULL,
    `quantity` INT NOT NULL,
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `idx_product_warehouse` (`product_id`, `warehouse_id`),
    INDEX `idx_warehouse_id` (`warehouse_id`),
    FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`warehouse_id`) REFERENCES `warehouse` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 创建车辆表
CREATE TABLE IF NOT EXISTS `vehicle` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `plate_number` VARCHAR(20) NOT NULL,
    `type` VARCHAR(50) NOT NULL,
    `capacity` INT NOT NULL,
    `status` INT NOT NULL COMMENT '0: 空闲, 1: 运输中, 2: 维修中',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `idx_plate_number` (`plate_number`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 创建司机表
CREATE TABLE IF NOT EXISTS `driver` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(50) NOT NULL,
    `phone` VARCHAR(20) NOT NULL,
    `license_number` VARCHAR(50) NOT NULL,
    `status` INT NOT NULL COMMENT '0: 空闲, 1: 工作中, 2: 休息中',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `idx_license_number` (`license_number`),
    INDEX `idx_name` (`name`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 创建运输表
CREATE TABLE IF NOT EXISTS `transport` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `order_id` BIGINT NOT NULL,
    `vehicle_id` BIGINT NOT NULL,
    `driver_id` BIGINT NOT NULL,
    `status` INT NOT NULL COMMENT '0: 待运输, 1: 运输中, 2: 已完成, 3: 已取消',
    `start_time` DATETIME NULL,
    `end_time` DATETIME NULL,
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_order_id` (`order_id`),
    INDEX `idx_vehicle_id` (`vehicle_id`),
    INDEX `idx_driver_id` (`driver_id`),
    INDEX `idx_status` (`status`),
    FOREIGN KEY (`order_id`) REFERENCES `order` (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`vehicle_id`) REFERENCES `vehicle` (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`driver_id`) REFERENCES `driver` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 创建结算表
CREATE TABLE IF NOT EXISTS `settlement` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `order_id` BIGINT NOT NULL,
    `amount` DECIMAL(10,2) NOT NULL,
    `status` INT NOT NULL COMMENT '0: 待结算, 1: 已结算, 2: 已取消',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_order_id` (`order_id`),
    INDEX `idx_status` (`status`),
    FOREIGN KEY (`order_id`) REFERENCES `order` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 插入测试数据

-- 插入客户数据
INSERT INTO `customer` (`name`, `phone`, `address`) VALUES
('张三', '13800138001', '北京市朝阳区'),
('李四', '13800138002', '上海市浦东新区'),
('王五', '13800138003', '广州市天河区');

-- 插入收件人数据
INSERT INTO `recipient` (`name`, `phone`, `address`, `customer_id`) VALUES
('张三', '13800138001', '北京市朝阳区', 1),
('李四', '13800138002', '上海市浦东新区', 2),
('王五', '13800138003', '广州市天河区', 3);

-- 插入仓库数据
INSERT INTO `warehouse` (`name`, `address`, `capacity`) VALUES
('北京仓库', '北京市朝阳区', 10000),
('上海仓库', '上海市浦东新区', 10000),
('广州仓库', '广州市天河区', 10000);

-- 插入产品数据
INSERT INTO `product` (`name`, `sku`, `price`) VALUES
('测试产品1', 'TEST001', 100.00),
('测试产品2', 'TEST002', 200.00),
('测试产品3', 'TEST003', 300.00);

-- 插入库存数据
INSERT INTO `inventory` (`product_id`, `warehouse_id`, `quantity`) VALUES
(1, 1, 100),
(2, 1, 200),
(3, 1, 300),
(1, 2, 100),
(2, 2, 200),
(3, 2, 300),
(1, 3, 100),
(2, 3, 200),
(3, 3, 300);

-- 插入车辆数据
INSERT INTO `vehicle` (`plate_number`, `type`, `capacity`, `status`) VALUES
('京A12345', '厢式货车', 5000, 0),
('沪B67890', '厢式货车', 5000, 0),
('粤C24680', '厢式货车', 5000, 0);

-- 插入司机数据
INSERT INTO `driver` (`name`, `phone`, `license_number`, `status`) VALUES
('赵六', '13800138004', '111111199001011234', 0),
('钱七', '13800138005', '222222199001011234', 0),
('孙八', '13800138006', '333333199001011234', 0);

-- 插入订单数据
INSERT INTO `order` (`order_no`, `customer_id`, `recipient_id`, `status`, `total_amount`) VALUES
('ORD20260308001', 1, 1, 0, 100.00),
('ORD20260308002', 2, 2, 0, 200.00),
('ORD20260308003', 3, 3, 0, 300.00);

-- 插入订单商品数据
INSERT INTO `order_item` (`order_id`, `product_id`, `quantity`, `price`) VALUES
(1, 1, 1, 100.00),
(2, 2, 1, 200.00),
(3, 3, 1, 300.00);

-- 插入运输数据
INSERT INTO `transport` (`order_id`, `vehicle_id`, `driver_id`, `status`) VALUES
(1, 1, 1, 0),
(2, 2, 2, 0),
(3, 3, 3, 0);

-- 插入结算数据
INSERT INTO `settlement` (`order_id`, `amount`, `status`) VALUES
(1, 100.00, 0),
(2, 200.00, 0),
(3, 300.00, 0);

-- 提交事务
COMMIT;

-- 完成数据库初始化
SELECT '数据库初始化完成' AS message;
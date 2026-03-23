-- 使用数据库
USE hmwl;

-- 创建网络路由表
CREATE TABLE IF NOT EXISTS `network_route` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `network_point_id` bigint DEFAULT NULL,
  `start_city` varchar(50) DEFAULT NULL,
  `destination_city` varchar(50) DEFAULT NULL,
  `base_price` double DEFAULT NULL,
  `price_per_kg` double DEFAULT NULL,
  `transit_days` int DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_network_point_id` (`network_point_id`),
  KEY `idx_start_city` (`start_city`),
  KEY `idx_destination_city` (`destination_city`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建网点报价表
CREATE TABLE IF NOT EXISTS `network_quote` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint DEFAULT NULL,
  `network_point_id` bigint DEFAULT NULL,
  `network_name` varchar(100) DEFAULT NULL,
  `base_fee` double DEFAULT NULL,
  `final_price` double DEFAULT NULL,
  `transit_days` int DEFAULT NULL,
  `status` int DEFAULT NULL,
  `remark` varchar(200) DEFAULT NULL,
  `quote_time` datetime DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_network_point_id` (`network_point_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 插入测试数据 - 网点
INSERT INTO `network_point` (`code`, `name`, `contact_person`, `phone`, `address`) VALUES
('NP001', '北京朝阳网点', '张三', '13800138001', '北京市朝阳区'),
('NP002', '上海浦东网点', '李四', '13900139001', '上海市浦东新区'),
('NP003', '北京海淀网点', '王五', '13700137001', '北京市海淀区');

-- 插入测试数据 - 网络路由
INSERT INTO `network_route` (`network_point_id`, `start_city`, `destination_city`, `base_price`, `price_per_kg`, `transit_days`) VALUES
(1, '北京市', '上海市', 100, 5, 2),
(2, '北京市', '上海市', 90, 4.5, 2),
(3, '北京市', '上海市', 95, 4.8, 2);

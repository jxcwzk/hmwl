-- 红美物流小规模优化功能数据库脚本
-- 创建时间: 2026-03-14

-- 1. 客户档案表
CREATE TABLE IF NOT EXISTS `customer_profile` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '档案ID',
  `business_user_id` BIGINT NOT NULL COMMENT '业务用户ID',
  `company_name` VARCHAR(100) DEFAULT NULL COMMENT '公司名称',
  `contact_person` VARCHAR(50) DEFAULT NULL COMMENT '联系人',
  `contact_phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
  `address` VARCHAR(200) DEFAULT NULL COMMENT '地址',
  `receiving_habits` TEXT COMMENT '收货习惯',
  `special_requirements` TEXT COMMENT '特殊要求',
  `cooperation_duration` INT DEFAULT NULL COMMENT '合作时长（月）',
  `credit_rating` INT DEFAULT 3 COMMENT '信用评级(1-5)',
  `total_orders` INT DEFAULT 0 COMMENT '历史订单总数',
  `total_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '历史总金额',
  `status` INT DEFAULT 1 COMMENT '状态: 1-有效, 0-无效',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_business_user` (`business_user_id`),
  KEY `idx_company_name` (`company_name`),
  KEY `idx_credit_rating` (`credit_rating`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户档案表';

-- 2. 运营统计表
CREATE TABLE IF NOT EXISTS `operation_statistics` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '统计ID',
  `stat_date` VARCHAR(10) NOT NULL COMMENT '统计日期(YYYY-MM-DD)',
  `stat_type` VARCHAR(20) DEFAULT 'daily' COMMENT '统计类型',
  `total_orders` INT DEFAULT 0 COMMENT '总订单数',
  `completed_orders` INT DEFAULT 0 COMMENT '已完成订单数',
  `pending_orders` INT DEFAULT 0 COMMENT '待处理订单数',
  `delivering_orders` INT DEFAULT 0 COMMENT '配送中订单数',
  `total_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '总金额',
  `avg_order_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '平均订单金额',
  `active_customers` INT DEFAULT 0 COMMENT '活跃客户数',
  `active_drivers` INT DEFAULT 0 COMMENT '活跃司机数',
  `day_over_day_growth` DECIMAL(5,2) DEFAULT 0.00 COMMENT '日环比增长(%)',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_stat_date` (`stat_date`),
  KEY `idx_stat_type` (`stat_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='运营统计表';

-- 3. 订单分配历史表
CREATE TABLE IF NOT EXISTS `order_assign_history` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `order_no` VARCHAR(50) DEFAULT NULL COMMENT '订单号',
  `driver_id` BIGINT NOT NULL COMMENT '司机ID',
  `driver_name` VARCHAR(50) DEFAULT NULL COMMENT '司机名称',
  `operator_id` BIGINT DEFAULT NULL COMMENT '操作人ID',
  `operator_name` VARCHAR(50) DEFAULT NULL COMMENT '操作人名称',
  `assign_type` INT DEFAULT 1 COMMENT '分配类型: 1-手动分配, 2-智能推荐',
  `assign_reason` VARCHAR(200) DEFAULT NULL COMMENT '分配原因',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_driver_id` (`driver_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单分配历史表';

-- 4. 司机统计表
CREATE TABLE IF NOT EXISTS `driver_statistics` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '统计ID',
  `driver_id` BIGINT NOT NULL COMMENT '司机ID',
  `stat_date` DATE NOT NULL COMMENT '统计日期',
  `order_count` INT DEFAULT 0 COMMENT '订单数量',
  `delivery_distance` DECIMAL(10,2) DEFAULT 0.00 COMMENT '配送里程',
  `delivery_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '配送金额',
  `online_time` INT DEFAULT 0 COMMENT '在线时长（分钟）',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_driver_date` (`driver_id`, `stat_date`),
  KEY `idx_driver_id` (`driver_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='司机统计表';

-- 5. 路线模板表
CREATE TABLE IF NOT EXISTS `route_template` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '模板ID',
  `template_name` VARCHAR(100) NOT NULL COMMENT '模板名称',
  `start_address` VARCHAR(200) NOT NULL COMMENT '起始地址',
  `end_address` VARCHAR(200) NOT NULL COMMENT '目的地址',
  `route_description` TEXT COMMENT '路线描述',
  `estimated_time` INT DEFAULT 60 COMMENT '预计时间（分钟）',
  `customer_id` BIGINT DEFAULT NULL COMMENT '关联客户ID',
  `driver_id` BIGINT DEFAULT NULL COMMENT '常用司机ID',
  `usage_count` INT DEFAULT 0 COMMENT '使用次数',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_customer_id` (`customer_id`),
  KEY `idx_driver_id` (`driver_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='路线模板表';

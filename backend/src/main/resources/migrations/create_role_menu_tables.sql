-- 角色表
CREATE TABLE IF NOT EXISTS `role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '角色名称',
  `code` varchar(50) NOT NULL COMMENT '角色代码',
  `description` varchar(200) DEFAULT NULL COMMENT '角色描述',
  `status` int DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 菜单权限表
CREATE TABLE IF NOT EXISTS `menu` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '菜单名称',
  `path` varchar(100) DEFAULT NULL COMMENT '菜单路径',
  `icon` varchar(50) DEFAULT NULL COMMENT '菜单图标',
  `parent_id` bigint DEFAULT 0 COMMENT '父菜单ID',
  `sort` int DEFAULT 0 COMMENT '排序',
  `status` int DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单权限表';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS `user_role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 初始化角色数据
INSERT INTO `role` (`name`, `code`, `description`, `status`) VALUES
('系统管理员', 'admin', '系统管理员，拥有所有权限', 1),
('客户', 'customer', '普通客户', 1),
('司机', 'driver', '司机角色', 1);

-- 初始化菜单数据
INSERT INTO `menu` (`name`, `path`, `icon`, `parent_id`, `sort`, `status`) VALUES
('订单管理', '/order', 'Document', 0, 1, 1),
('线路管理', '/route', 'TrendCharts', 0, 2, 1),
('司机管理', '/driver', 'Van', 0, 3, 1),
('车辆管理', '/vehicle', 'Truck', 0, 4, 1),
('网点管理', '/network', 'OfficeBuilding', 0, 5, 1),
('财务结算', '/settlement', 'Money', 0, 6, 1),
('系统管理', '/system', 'Setting', 0, 7, 1);

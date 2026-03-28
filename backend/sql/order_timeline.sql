-- 订单时间记录表
CREATE TABLE IF NOT EXISTS `order_timeline` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_no` VARCHAR(32) NOT NULL COMMENT '订单号',
  `operator_id` BIGINT DEFAULT 0 COMMENT '操作员ID，0表示系统',
  `operator_type` VARCHAR(16) DEFAULT 'SYSTEM' COMMENT '操作员类型：CUSTOMER/DISPATCHER/NETWORK/DRIVER/SYSTEM',
  `status_code` VARCHAR(32) NOT NULL COMMENT '状态码',
  `status_name` VARCHAR(64) NOT NULL COMMENT '状态中文名',
  `operate_time` DATETIME NOT NULL COMMENT '操作时间',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  INDEX `idx_order_no` (`order_no`),
  INDEX `idx_operate_time` (`operate_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单时间记录表';

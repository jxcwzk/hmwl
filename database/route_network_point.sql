-- 线路-网点关联表
CREATE TABLE IF NOT EXISTS route_network_point (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    route_id BIGINT NOT NULL COMMENT '线路ID',
    network_point_id BIGINT NOT NULL COMMENT '网点ID',
    sequence INT DEFAULT 0 COMMENT '在线路上的顺序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_route_network (route_id, network_point_id),
    KEY idx_route_id (route_id),
    KEY idx_network_point_id (network_point_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='线路-网点关联表';

-- 测试数据：线路1 (上海→北京) 关联 网点1,2,3
INSERT INTO route_network_point (route_id, network_point_id, sequence) VALUES
(1, 1, 1),
(1, 2, 2),
(1, 3, 3);

-- 测试数据：线路2 (上海→广州) 关联 网点1,4
INSERT INTO route_network_point (route_id, network_point_id, sequence) VALUES
(2, 1, 1),
(2, 4, 2);

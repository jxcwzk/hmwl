-- 四角色物流流程测试数据初始化
-- 使用说明: 执行此脚本前需先备份现有数据

-- 客户账号
INSERT INTO user (id, username, password, user_type, create_time) VALUES
(1, 'customer1', '123456', 3, NOW()),
(2, 'customer2', '123456', 3, NOW()),
(3, 'customer3', '123456', 3, NOW())
ON DUPLICATE KEY UPDATE username=username;

-- 调度账号 (管理员)
INSERT INTO user (id, username, password, user_type, create_time) VALUES
(4, 'admin1', '123456', 1, NOW()),
(5, 'admin2', '123456', 1, NOW())
ON DUPLICATE KEY UPDATE username=username;

-- 网点账号
INSERT INTO user (id, username, password, user_type, create_time) VALUES
(6, 'network1', '123456', 2, NOW()),
(7, 'network2', '123456', 2, NOW()),
(8, 'network3', '123456', 2, NOW())
ON DUPLICATE KEY UPDATE username=username;

-- 网点
INSERT INTO network_point (id, name, code, user_id, create_time) VALUES
(1, 'network1', 'NW001', 6, NOW()),
(2, 'network2', 'NW002', 7, NOW()),
(3, 'network3', 'NW003', 8, NOW())
ON DUPLICATE KEY UPDATE name=name;

-- 司机账号
INSERT INTO user (id, username, password, user_type, create_time) VALUES
(9, 'driver1', '123456', 4, NOW()),
(10, 'driver2', '123456', 4, NOW()),
(11, 'driver3', '123456', 4, NOW())
ON DUPLICATE KEY UPDATE username=username;

-- 司机
INSERT INTO driver (id, name, phone, network_id, user_id, create_time) VALUES
(1, 'driver1', '13800001111', 1, 9, NOW()),
(2, 'driver2', '13800002222', 1, 10, NOW()),
(3, 'driver3', '13800003333', 2, 11, NOW())
ON DUPLICATE KEY UPDATE name=name;

-- 验证数据
SELECT '客户账号' as type, COUNT(*) as count FROM user WHERE user_type = 3
UNION ALL
SELECT '调度账号', COUNT(*) FROM user WHERE user_type = 1
UNION ALL
SELECT '网点账号', COUNT(*) FROM user WHERE user_type = 2
UNION ALL
SELECT '司机账号', COUNT(*) FROM user WHERE user_type = 4;

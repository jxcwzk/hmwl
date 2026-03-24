-- 创建测试用户

-- 管理员角色 (user_type=1)
INSERT INTO user (username, user_type, remark, phone, wechat, password, status) VALUES
('admin1', 1, '管理员1', '13800138001', 'admin1_wechat', '123456', 1),
('admin2', 1, '管理员2', '13800138002', 'admin2_wechat', '123456', 1);

-- 客户角色 (user_type=2)
INSERT INTO user (username, user_type, remark, phone, wechat, password, status, business_user_id) VALUES
('customer1', 2, '客户1', '13900139001', 'customer1_wechat', '123456', 1, 1),
('customer2', 2, '客户2', '13900139002', 'customer2_wechat', '123456', 1, 2),
('customer3', 2, '客户3', '13900139003', 'customer3_wechat', '123456', 1, 3);

-- 司机角色 (user_type=3)
INSERT INTO user (username, user_type, remark, phone, wechat, password, status, driver_id) VALUES
('driver1', 3, '司机1', '13700137001', 'driver1_wechat', '123456', 1, 1),
('driver2', 3, '司机2', '13700137002', 'driver2_wechat', '123456', 1, 2),
('driver3', 3, '司机3', '13700137003', 'driver3_wechat', '123456', 1, 3);

-- 网点角色 (user_type=4)
INSERT INTO user (username, user_type, remark, phone, wechat, password, status, network_point_id) VALUES
('network1', 4, '网点1', '13600136001', 'network1_wechat', '123456', 1, 1),
('network2', 4, '网点2', '13600136002', 'network2_wechat', '123456', 1, 2),
('network3', 4, '网点3', '13600136003', 'network3_wechat', '123456', 1, 3);

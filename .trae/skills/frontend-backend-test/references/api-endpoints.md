# API接口文档

## 业务用户接口

### 1. 获取业务用户列表
- **URL**: `/api/business-user/list`
- **方法**: GET
- **描述**: 获取所有业务用户的列表
- **响应**: 业务用户列表

### 2. 分页获取业务用户列表
- **URL**: `/api/business-user/page`
- **方法**: GET
- **参数**: 
  - current: 当前页码，默认1
  - size: 每页大小，默认10
- **响应**: 分页后的业务用户列表

### 3. 根据ID获取业务用户详情
- **URL**: `/api/business-user/{id}`
- **方法**: GET
- **参数**: id: 业务用户ID
- **响应**: 业务用户详情

### 4. 保存业务用户信息
- **URL**: `/api/business-user`
- **方法**: POST
- **参数**: 业务用户信息
- **响应**: 保存是否成功

### 5. 更新业务用户信息
- **URL**: `/api/business-user`
- **方法**: PUT
- **参数**: 业务用户信息
- **响应**: 更新是否成功

### 6. 删除业务用户
- **URL**: `/api/business-user/{id}`
- **方法**: DELETE
- **参数**: id: 业务用户ID
- **响应**: 删除是否成功

## 业务客户接口

### 1. 获取业务客户列表
- **URL**: `/api/business-customer/list`
- **方法**: GET
- **描述**: 获取所有业务客户的列表
- **响应**: 业务客户列表

### 2. 分页获取业务客户列表
- **URL**: `/api/business-customer/page`
- **方法**: GET
- **参数**: 
  - current: 当前页码，默认1
  - size: 每页大小，默认10
- **响应**: 分页后的业务客户列表

### 3. 根据ID获取业务客户详情
- **URL**: `/api/business-customer/{id}`
- **方法**: GET
- **参数**: id: 业务客户ID
- **响应**: 业务客户详情

### 4. 保存业务客户信息
- **URL**: `/api/business-customer`
- **方法**: POST
- **参数**: 业务客户信息
- **响应**: 保存是否成功

### 5. 更新业务客户信息
- **URL**: `/api/business-customer`
- **方法**: PUT
- **参数**: 业务客户信息
- **响应**: 更新是否成功

### 6. 删除业务客户
- **URL**: `/api/business-customer/{id}`
- **方法**: DELETE
- **参数**: id: 业务客户ID
- **响应**: 删除是否成功

### 7. 根据业务用户ID获取业务客户列表
- **URL**: `/api/business-customer/listByBusinessUserId/{businessUserId}`
- **方法**: GET
- **参数**: businessUserId: 业务用户ID
- **响应**: 业务客户列表

### 8. 根据业务用户ID和类型获取业务客户列表
- **URL**: `/api/business-customer/listByBusinessUserIdAndType/{businessUserId}/{type}`
- **方法**: GET
- **参数**: 
  - businessUserId: 业务用户ID
  - type: 类型，0表示发件人信息，1表示收件人信息
- **响应**: 业务客户列表

## 订单接口

### 1. 获取订单列表
- **URL**: `/api/order/list`
- **方法**: GET
- **描述**: 获取所有订单的列表
- **响应**: 订单列表

### 2. 分页获取订单列表
- **URL**: `/api/order/page`
- **方法**: GET
- **参数**: 
  - current: 当前页码，默认1
  - size: 每页大小，默认10
- **响应**: 分页后的订单列表

### 3. 根据ID获取订单详情
- **URL**: `/api/order/{id}`
- **方法**: GET
- **参数**: id: 订单ID
- **响应**: 订单详情

### 4. 保存订单信息
- **URL**: `/api/order`
- **方法**: POST
- **参数**: 订单信息
- **响应**: 保存是否成功

### 5. 更新订单信息
- **URL**: `/api/order`
- **方法**: PUT
- **参数**: 订单信息
- **响应**: 更新是否成功

### 6. 删除订单
- **URL**: `/api/order/{id}`
- **方法**: DELETE
- **参数**: id: 订单ID
- **响应**: 删除是否成功
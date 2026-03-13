---
name: "business-process-tester"
description: "Tests business processes for multi-role order management system. Invoke when user wants to test end-to-end business flows, verify role-specific functionalities, or validate system integration."
---

# Business Process Tester

## Overview

This skill helps test the end-to-end business processes of the multi-role order management system. It provides structured testing steps for each role and key business flows to ensure system functionality and integration.

## When to Invoke

Invoke this skill when:
- User wants to test the complete order management business flow
- User needs to verify role-specific functionalities
- User wants to validate system integration between different roles
- User needs to test specific business scenarios

## Testable Business Processes

### 1. Order Creation and Management Flow

**Steps:**
1. **Customer Role**: Create a new order
2. **Admin Role**: Review order, assign driver and network point
3. **Network Point Role**: Provide base fee quote
4. **Admin Role**: Review and adjust customer quote
5. **Customer Role**: View order and estimated price
6. **Network Point Role**: Update logistics progress
7. **Customer Role**: Track logistics status

### 2. Role-Specific Functionality Test

**Admin Role Tests:**
- View all orders
- Assign orders to drivers
- Assign orders to network points
- Modify customer quotes
- Review network point quotes

**Driver Role Tests:**
- View assigned orders
- Update order status

**Customer Role Tests:**
- Create orders
- View own orders
- Track logistics progress
- View estimated prices

**Network Point Role Tests:**
- View assigned orders
- Provide base fee quotes
- Update logistics progress
- Confirm order prices

### 3. Integration Test Scenarios

**Scenario 1: Complete Order Lifecycle**
1. Customer creates order
2. Admin assigns driver and network point
3. Network point provides quote
4. Admin adjusts price
5. Network point updates logistics
6. Customer tracks delivery

**Scenario 2: Price Calculation Workflow**
1. Network point provides base fee
2. System automatically calculates customer price (base fee × 1.4286)
3. Admin reviews and modifies price if needed
4. Customer views final price

**Scenario 3: Logistics Tracking**
1. Network point updates logistics progress
2. Customer views updated progress
3. Admin monitors logistics status

## Testing Checklist

### Admin Role
- [ ] Can view all orders
- [ ] Can assign orders to drivers
- [ ] Can assign orders to network points
- [ ] Can modify customer quotes
- [ ] Can review network point quotes

### Driver Role
- [ ] Can view assigned orders
- [ ] Can update order status

### Customer Role
- [ ] Can create orders
- [ ] Can view own orders
- [ ] Can track logistics progress
- [ ] Can view estimated prices

### Network Point Role
- [ ] Can view assigned orders
- [ ] Can provide base fee quotes
- [ ] Can update logistics progress
- [ ] Can confirm order prices

## Test Data

Use the following test users for testing:

### Admin Users
- Username: admin1, Password: 123456
- Username: admin2, Password: 123456

### Customer Users
- Username: customer1, Password: 123456
- Username: customer2, Password: 123456
- Username: customer3, Password: 123456

### Driver Users
- Username: driver1, Password: 123456
- Username: driver2, Password: 123456
- Username: driver3, Password: 123456

### Network Point Users
- Username: network1, Password: 123456
- Username: network2, Password: 123456
- Username: network3, Password: 123456

## Test Environment

- Frontend: http://localhost:3000
- Backend: http://localhost:8081/api
- Database: MySQL (hmwl database)

## Troubleshooting

### Common Issues
1. **400 Bad Request**: Missing required parameters (userId, userType, businessUserId)
2. **500 Internal Server Error**: Database connection issues or missing table fields
3. **No Data Displayed**: Check user role and permissions
4. **API Connection Errors**: Verify backend service is running

### Resolution Steps
1. Ensure backend service is running: `mvn spring-boot:run`
2. Verify database connection and table structure
3. Check API parameters for required fields
4. Confirm user roles and permissions
5. Clear browser cache and try again

## Usage Example

To test the complete order lifecycle:

1. **Login as customer1** and create a new order
2. **Login as admin1** and assign driver1 and network1 to the order
3. **Login as network1** and provide a base fee quote
4. **Login as admin1** and review/modify the customer quote
5. **Login as customer1** and view the order with estimated price
6. **Login as network1** and update the logistics progress
7. **Login as customer1** and track the logistics status

This end-to-end test validates the complete business flow and ensures all roles can interact properly with the system.
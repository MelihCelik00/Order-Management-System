# Order Management System Documentation

## Project Overview
A Spring Boot application implementing an Order Management System with customer management, order processing, and automated discount capabilities.

## Technology Stack
- Framework: Spring Boot 3.4.1
- Build Tool: Gradle with Groovy DSL
- Java Version: 21
- Database: PostgreSQL
- Dependencies:
  - Spring Web
  - Spring Data JPA
  - Spring Validation
  - Lombok
  - PostgreSQL Driver

## Core Requirements

### 1. Customer Management
- Customer creation functionality
- Customer tier system with levels:
  - Regular (Default)
  - Gold (After 10 orders)
  - Platinum (After 20 orders)
- Automatic tier progression based on order count

### 2. Order Processing
- Order creation functionality
- Orders are final upon creation
- No order status workflow
- Payment is assumed to be completed during order creation

### 3. Discount System
- Automatic discount application:
  - Gold customers: 10% discount
  - Platinum customers: 20% discount
- Discount tracking:
  - Store discount details per customer/order
  - Enable future discount claims
  - Maintain discount history

## Technical Implementation

### 1. Database Schema
#### Customer Entity
- Fields:
  - id (Long)
  - name (String)
  - email (String, unique)
  - tier (CustomerTier enum)
  - totalOrders (Integer)

#### Order Entity
- Fields:
  - id (Long)
  - customer (Customer)
  - amount (Double)
  - discountAmount (Double)
  - finalAmount (Double)
  - orderDate (LocalDateTime)

### 2. API Endpoints

#### Customer API
```
POST   /api/customers          - Create new customer
GET    /api/customers          - Get all customers
GET    /api/customers/{id}     - Get customer by ID
GET    /api/customers/email/{email} - Get customer by email
PUT    /api/customers/{id}     - Update customer
DELETE /api/customers/{id}     - Delete customer
```

#### Order API
```
POST   /api/orders            - Create new order
GET    /api/orders            - Get all orders
GET    /api/orders/{id}       - Get order by ID
GET    /api/orders/customer/{customerId} - Get orders by customer ID
```

## Completed Tasks

### 1. Project Setup
- [x] Created Spring Boot project with required dependencies
- [x] Configured PostgreSQL database connection
- [x] Set up application properties

### 2. Entity Implementation
- [x] Created Customer entity with tier management
- [x] Created Order entity with discount calculation
- [x] Implemented entity relationships and validations

### 3. DTO Implementation
- [x] Created CustomerDTO for data transfer
- [x] Created OrderDTO for data transfer
- [x] Implemented validation constraints
- [x] Implemented builder pattern for all DTOs

### 4. Repository Layer
- [x] Created CustomerRepository with custom queries
- [x] Created OrderRepository with custom queries

### 5. Service Layer
- [x] Implemented CustomerService interface and implementation
- [x] Implemented OrderService interface and implementation
- [x] Added business logic for tier progression
- [x] Added automatic discount calculation
- [x] Implemented proper validation handling

### 6. Controller Layer
- [x] Implemented CustomerController with CRUD operations
- [x] Implemented OrderController with order management
- [x] Added proper error handling
- [x] Implemented response wrapping with ApiResponse
- [x] Added request validation using @Valid

### 7. Documentation
- [x] Created initial documentation
- [x] Documented API endpoints
- [x] Documented project structure
- [x] Listed completed tasks

### 8. Testing
- [x] Added test dependencies (JUnit, Mockito)
- [x] Implemented CustomerService unit tests
- [x] Implemented OrderService unit tests
- [x] Implemented builder pattern in test data creation
- [x] Added comprehensive test coverage for edge cases
- [x] Optimized test structure and removed redundant validations

### 9. API Documentation
- [x] Added SpringDoc OpenAPI dependency
- [x] Configured OpenAPI with project information
- [x] Added API documentation to CustomerController
- [x] Added API documentation to OrderController
- [x] Implemented Swagger UI for API testing and documentation

## API Documentation Access
The API documentation can be accessed through Swagger UI at:
```
http://localhost:8080/swagger-ui.html
```

The OpenAPI specification is available at:
```
http://localhost:8080/v3/api-docs
```

## Future Tasks
- [ ] Add logging
- [ ] Implement security
- [ ] Add monitoring and metrics
- [ ] Add integration tests
- [ ] Implement caching
- [ ] Add rate limiting

## Test Coverage

### CustomerService Tests
- Create customer (success and failure scenarios)
- Get customer by ID
- Get customer by email
- Get all customers
- Update customer
- Delete customer
- Email uniqueness validation
- Input validation using @Valid
- Builder pattern implementation

### OrderService Tests
- Create order (success and failure scenarios)
- Get order by ID
- Get orders by customer ID
- Get all orders
- Discount calculation for different customer tiers
- Customer tier progression after order creation
- Edge cases (maximum/minimum amounts)
- Tier upgrade notifications
- Tier progression alerts
- Input validation using @Valid
- Builder pattern implementation 
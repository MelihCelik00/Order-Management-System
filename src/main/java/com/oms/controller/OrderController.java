package com.oms.controller;

import com.oms.config.ApiEndpoints;
import com.oms.dto.CreateOrderRequest;
import com.oms.dto.OrderDTO;
import com.oms.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiEndpoints.ORDERS)
@Tag(name = "Order Management", description = "APIs for managing orders")
public class OrderController {
    
    @Autowired
    private OrderService orderService;

    @Operation(
        summary = "Create a new order",
        description = "Creates a new order for a customer with automatic discount application based on customer tier"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Order created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or customer not found")
    })
    @PostMapping(ApiEndpoints.Order.CREATE)
    public ResponseEntity<OrderDTO> createOrder(
            @Parameter(description = "Order details") @Valid @RequestBody CreateOrderRequest request) {
        OrderDTO createdOrder = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    @Operation(
        summary = "Get order by ID",
        description = "Retrieves an order by its unique identifier"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Order found"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping(ApiEndpoints.Order.GET_BY_ID)
    public ResponseEntity<OrderDTO> getOrderById(
            @Parameter(description = "Order ID") @PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @Operation(
        summary = "Get orders by customer ID",
        description = "Retrieves all orders for a specific customer"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Orders found"),
        @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @GetMapping(ApiEndpoints.Order.GET_BY_CUSTOMER)
    public ResponseEntity<List<OrderDTO>> getOrdersByCustomerId(
            @Parameter(description = "Customer ID") @PathVariable Long customerId) {
        return ResponseEntity.ok(orderService.getOrdersByCustomerId(customerId));
    }

    @Operation(
        summary = "Get all orders",
        description = "Retrieves a list of all orders in the system"
    )
    @ApiResponse(responseCode = "200", description = "List of orders retrieved successfully")
    @GetMapping(ApiEndpoints.Order.GET_ALL)
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }
} 
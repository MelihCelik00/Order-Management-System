package com.oms.controller;

import com.oms.dto.OrderDTO;
import com.oms.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Order Management", description = "APIs for managing orders")
public class OrderController {
    
    @Autowired
    private OrderService orderService;

    @Operation(
        summary = "Create a new order",
        description = "Creates a new order for a customer with automatic discount application based on customer tier"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Order created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @PostMapping
    public ResponseEntity<com.oms.response.ApiResponse<OrderDTO>> createOrder(
            @Valid @RequestBody OrderDTO orderDTO) {
        try {
            OrderDTO createdOrder = orderService.createOrder(orderDTO);
            return ResponseEntity.ok(new com.oms.response.ApiResponse<>("SUCCESS", "Order created successfully", createdOrder));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(new com.oms.response.ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    @Operation(
        summary = "Get order by ID",
        description = "Retrieves an order by its unique identifier"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Order found"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderDTO>> getOrderById(
            @Parameter(description = "Order ID") @PathVariable Long id) {
        try {
            OrderDTO order = orderService.getOrderById(id);
            return ResponseEntity.ok(new com.oms.response.ApiResponse<>("SUCCESS", "Order retrieved successfully", order));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new com.oms.response.ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    @Operation(
        summary = "Get orders by customer ID",
        description = "Retrieves all orders for a specific customer"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Orders found"),
        @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<com.oms.response.ApiResponse<List<OrderDTO>>> getOrdersByCustomerId(
            @Parameter(description = "Customer ID") @PathVariable Long customerId) {
        try {
            List<OrderDTO> orders = orderService.getOrdersByCustomerId(customerId);
            return ResponseEntity.ok(new com.oms.response.ApiResponse<>("SUCCESS", "Orders retrieved successfully", orders));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new com.oms.response.ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    @Operation(
        summary = "Get all orders",
        description = "Retrieves a list of all orders in the system"
    )
    @ApiResponse(responseCode = "200", description = "List of orders retrieved successfully")
    @GetMapping
    public ResponseEntity<com.oms.response.ApiResponse<List<OrderDTO>>> getAllOrders() {
        List<OrderDTO> orders = orderService.getAllOrders();
        return ResponseEntity.ok(new com.oms.response.ApiResponse<>("SUCCESS", "Orders retrieved successfully", orders));
    }
} 
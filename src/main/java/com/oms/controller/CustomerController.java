package com.oms.controller;

import com.oms.dto.CustomerDTO;
import com.oms.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
@RequestMapping("/api/customers")
@Tag(name = "Customer Management", description = "APIs for managing customers")
public class CustomerController {
    
    @Autowired
    private CustomerService customerService;

    @Operation(
        summary = "Create a new customer",
        description = "Creates a new customer with the provided details. Email must be unique."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Customer created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or email already exists")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<CustomerDTO>> createCustomer(
            @Valid @RequestBody CustomerDTO customerDTO) {
        try {
            CustomerDTO createdCustomer = customerService.createCustomer(customerDTO);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "Customer created successfully", createdCustomer));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    @Operation(
        summary = "Get customer by ID",
        description = "Retrieves a customer by their unique identifier"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Customer found"),
        @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerDTO>> getCustomerById(
            @Parameter(description = "Customer ID") @PathVariable Long id) {
        try {
            CustomerDTO customer = customerService.getCustomerById(id);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "Customer retrieved successfully", customer));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    @Operation(
        summary = "Get customer by email",
        description = "Retrieves a customer by their email address"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Customer found"),
        @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<CustomerDTO>> getCustomerByEmail(
            @Parameter(description = "Customer email") @PathVariable String email) {
        try {
            CustomerDTO customer = customerService.getCustomerByEmail(email);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "Customer retrieved successfully", customer));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    @Operation(
        summary = "Get all customers",
        description = "Retrieves a list of all customers"
    )
    @ApiResponse(responseCode = "200", description = "List of customers retrieved successfully")
    @GetMapping
    public ResponseEntity<ApiResponse<List<CustomerDTO>>> getAllCustomers() {
        List<CustomerDTO> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "Customers retrieved successfully", customers));
    }

    @Operation(
        summary = "Update customer",
        description = "Updates an existing customer's information"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Customer updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or email already exists"),
        @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerDTO>> updateCustomer(
            @Parameter(description = "Customer ID") @PathVariable Long id,
            @Valid @RequestBody CustomerDTO customerDTO) {
        try {
            CustomerDTO updatedCustomer = customerService.updateCustomer(id, customerDTO);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "Customer updated successfully", updatedCustomer));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    @Operation(
        summary = "Delete customer",
        description = "Deletes a customer by their ID"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Customer deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCustomer(
            @Parameter(description = "Customer ID") @PathVariable Long id) {
        try {
            customerService.deleteCustomer(id);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "Customer deleted successfully", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }
} 
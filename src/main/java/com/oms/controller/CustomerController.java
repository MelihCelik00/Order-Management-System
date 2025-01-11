package com.oms.controller;

import com.oms.config.ApiEndpoints;
import com.oms.dto.CreateCustomerRequest;
import com.oms.dto.CustomerDTO;
import com.oms.dto.UpdateCustomerRequest;
import com.oms.service.CustomerService;
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
@RequestMapping(ApiEndpoints.CUSTOMERS)
@Tag(name = "Customer Management", description = "APIs for managing customers")
public class CustomerController {
    
    @Autowired
    private CustomerService customerService;

    @Operation(
        summary = "Create a new customer",
        description = "Creates a new customer with the provided details. Email must be unique."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Customer created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or email already exists")
    })
    @PostMapping(ApiEndpoints.Customer.CREATE)
    public ResponseEntity<CustomerDTO> createCustomer(
            @Parameter(description = "Customer details") @Valid @RequestBody CreateCustomerRequest request) {
        CustomerDTO createdCustomer = customerService.createCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCustomer);
    }

    @Operation(
        summary = "Get customer by ID",
        description = "Retrieves a customer by their unique identifier"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Customer found"),
        @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @GetMapping(ApiEndpoints.Customer.GET_BY_ID)
    public ResponseEntity<CustomerDTO> getCustomerById(
            @Parameter(description = "Customer ID") @PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @Operation(
        summary = "Get customer by email",
        description = "Retrieves a customer by their email address"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Customer found"),
        @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @GetMapping(ApiEndpoints.Customer.GET_BY_EMAIL)
    public ResponseEntity<CustomerDTO> getCustomerByEmail(
            @Parameter(description = "Customer email") @PathVariable String email) {
        return ResponseEntity.ok(customerService.getCustomerByEmail(email));
    }

    @Operation(
        summary = "Get all customers",
        description = "Retrieves a list of all customers in the system"
    )
    @ApiResponse(responseCode = "200", description = "List of customers retrieved successfully")
    @GetMapping(ApiEndpoints.Customer.GET_ALL)
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
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
    @PutMapping(ApiEndpoints.Customer.UPDATE)
    public ResponseEntity<CustomerDTO> updateCustomer(
            @Parameter(description = "Customer ID") @PathVariable Long id,
            @Parameter(description = "Updated customer details") @Valid @RequestBody UpdateCustomerRequest request) {
        return ResponseEntity.ok(customerService.updateCustomer(id, request));
    }

    @Operation(
        summary = "Delete customer",
        description = "Deletes a customer by their ID"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Customer deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @DeleteMapping(ApiEndpoints.Customer.DELETE)
    public ResponseEntity<Void> deleteCustomer(
            @Parameter(description = "Customer ID") @PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
} 
package com.oms.controller;

import com.oms.dto.CreateCustomerRequest;
import com.oms.dto.CustomerDTO;
import com.oms.dto.UpdateCustomerRequest;
import com.oms.entity.CustomerTier;
import com.oms.integration.AbstractIntegrationTest;
import com.oms.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CustomerControllerIntegrationTest extends AbstractIntegrationTest {

    @Test
    void createCustomer_Success() throws Exception {
        CreateCustomerRequest request = TestUtil.createCustomerRequest();

        ResultActions response = performPost("/api/customers", request);

        response.andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", notNullValue()))
            .andExpect(jsonPath("$.name", is(request.name())))
            .andExpect(jsonPath("$.email", is(request.email())))
            .andExpect(jsonPath("$.tier", is(request.tier().toString())))
            .andExpect(jsonPath("$.totalOrders", is(0)));
    }

    @Test
    void createCustomer_DuplicateEmail_ReturnsBadRequest() throws Exception {
        CreateCustomerRequest request = TestUtil.createCustomerRequest();

        // Create first customer
        performPost("/api/customers", request);

        // Try to create second customer with same email
        ResultActions response = performPost("/api/customers", request);

        response.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", is("Email already exists")));
    }

    @Test
    void getCustomerById_Success() throws Exception {
        // Create a customer first
        CreateCustomerRequest request = TestUtil.createCustomerRequest();
        String createResponse = performPost("/api/customers", request)
            .andReturn()
            .getResponse()
            .getContentAsString();

        CustomerDTO createdCustomer = fromJson(createResponse, CustomerDTO.class);

        // Get the customer by ID
        ResultActions response = performGet("/api/customers/" + createdCustomer.id());

        response.andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(createdCustomer.id().intValue())))
            .andExpect(jsonPath("$.name", is(request.name())))
            .andExpect(jsonPath("$.email", is(request.email())));
    }

    @Test
    void getCustomerById_NotFound_ReturnsNotFound() throws Exception {
        ResultActions response = performGet("/api/customers/999");

        response.andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message", is("Customer not found")));
    }

    @Test
    void updateCustomer_Success() throws Exception {
        // Create a customer first
        CreateCustomerRequest createRequest = TestUtil.createCustomerRequest();
        String createResponse = performPost("/api/customers", createRequest)
            .andReturn()
            .getResponse()
            .getContentAsString();

        CustomerDTO createdCustomer = fromJson(createResponse, CustomerDTO.class);

        // Update the customer
        UpdateCustomerRequest updateRequest = new UpdateCustomerRequest(
            "Updated Name",
            createdCustomer.email()
        );

        ResultActions response = performPut("/api/customers/" + createdCustomer.id(), updateRequest);

        response.andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is("Updated Name")))
            .andExpect(jsonPath("$.email", is(updateRequest.email())));
    }

    @Test
    void updateCustomer_WithExistingEmail_ReturnsBadRequest() throws Exception {
        // Create first customer
        CreateCustomerRequest firstCustomer = TestUtil.createCustomerRequest();
        performPost("/api/customers", firstCustomer);

        // Create second customer
        CreateCustomerRequest secondCustomer = new CreateCustomerRequest(
            "Second User",
            "second@example.com",
            CustomerTier.REGULAR
        );
        String createResponse = performPost("/api/customers", secondCustomer)
            .andReturn()
            .getResponse()
            .getContentAsString();

        CustomerDTO createdCustomer = fromJson(createResponse, CustomerDTO.class);

        // Try to update second customer with first customer's email
        UpdateCustomerRequest updateRequest = new UpdateCustomerRequest(
            createdCustomer.name(),
            firstCustomer.email()
        );

        ResultActions response = performPut("/api/customers/" + createdCustomer.id(), updateRequest);

        response.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", is("Email already exists")));
    }

    @Test
    void deleteCustomer_Success() throws Exception {
        // Create a customer first
        CreateCustomerRequest request = TestUtil.createCustomerRequest();
        String createResponse = performPost("/api/customers", request)
            .andReturn()
            .getResponse()
            .getContentAsString();

        CustomerDTO createdCustomer = fromJson(createResponse, CustomerDTO.class);

        // Delete the customer
        ResultActions response = performDelete("/api/customers/" + createdCustomer.id());

        response.andExpect(status().isNoContent());

        // Verify customer is deleted
        performGet("/api/customers/" + createdCustomer.id())
            .andExpect(status().isNotFound());
    }
} 
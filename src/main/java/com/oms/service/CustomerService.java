package com.oms.service;

import com.oms.dto.CustomerDTO;
import com.oms.dto.CreateCustomerRequest;
import com.oms.dto.UpdateCustomerRequest;
import java.util.List;

public interface CustomerService {
    CustomerDTO createCustomer(CreateCustomerRequest request);
    CustomerDTO getCustomerById(Long id);
    CustomerDTO getCustomerByEmail(String email);
    List<CustomerDTO> getAllCustomers();
    void deleteCustomer(Long id);
    CustomerDTO updateCustomer(Long id, UpdateCustomerRequest request);
} 
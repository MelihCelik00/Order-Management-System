package com.oms.service;

import com.oms.dto.CustomerDTO;
import java.util.List;

public interface CustomerService {
    CustomerDTO createCustomer(CustomerDTO customerDTO);
    CustomerDTO getCustomerById(Long id);
    CustomerDTO getCustomerByEmail(String email);
    List<CustomerDTO> getAllCustomers();
    void deleteCustomer(Long id);
    CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO);
} 
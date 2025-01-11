package com.oms.service.impl;

import com.oms.dto.CustomerDTO;
import com.oms.dto.CreateCustomerRequest;
import com.oms.dto.UpdateCustomerRequest;
import com.oms.entity.Customer;
import com.oms.repository.CustomerRepository;
import com.oms.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.regex.Pattern;

@Service
public class CustomerServiceImpl implements CustomerService {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    
    @Autowired
    private CustomerRepository customerRepository;

    @Override
    @Transactional
    public CustomerDTO createCustomer(CreateCustomerRequest request) {
        if (request.name() == null || request.name().trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be blank");
        }
        
        if (request.email() == null || request.email().trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be blank");
        }
        
        if (!EMAIL_PATTERN.matcher(request.email()).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
        
        if (customerRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already exists");
        }
        
        Customer customer = Customer.builder()
            .name(request.name())
            .email(request.email())
            .totalOrders(0)
            .build();
        
        return toDTO(customerRepository.save(customer));
    }

    @Override
    public CustomerDTO getCustomerById(Long id) {
        return customerRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
    }

    @Override
    public CustomerDTO getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email)
                .map(this::toDTO)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
    }

    @Override
    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new IllegalArgumentException("Customer not found");
        }
        customerRepository.deleteById(id);
    }

    @Override
    @Transactional
    public CustomerDTO updateCustomer(Long id, UpdateCustomerRequest request) {
        if (request.name() == null || request.name().trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be blank");
        }
        
        if (request.email() == null || request.email().trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be blank");
        }
        
        if (!EMAIL_PATTERN.matcher(request.email()).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
        
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        
        if (!existingCustomer.getEmail().equals(request.email()) && 
            customerRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already exists");
        }
        
        Customer updatedCustomer = Customer.builder()
            .id(existingCustomer.getId())
            .name(request.name())
            .email(request.email())
            .tier(existingCustomer.getTier())
            .totalOrders(existingCustomer.getTotalOrders())
            .build();
        
        return toDTO(customerRepository.save(updatedCustomer));
    }

    private CustomerDTO toDTO(Customer customer) {
        return CustomerDTO.builder()
            .id(customer.getId())
            .name(customer.getName())
            .email(customer.getEmail())
            .tier(customer.getTier())
            .totalOrders(customer.getTotalOrders())
            .build();
    }
} 
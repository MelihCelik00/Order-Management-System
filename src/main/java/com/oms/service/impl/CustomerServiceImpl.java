package com.oms.service.impl;

import com.oms.dto.CustomerDTO;
import com.oms.dto.CreateCustomerRequest;
import com.oms.entity.Customer;
import com.oms.repository.CustomerRepository;
import com.oms.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {
    
    @Autowired
    private CustomerRepository customerRepository;

    @Override
    @Transactional
    public CustomerDTO createCustomer(CreateCustomerRequest request) {
        if (customerRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already exists");
        }
        
        Customer customer = new Customer();
        customer.setName(request.name());
        customer.setEmail(request.email());
        customer.setTier(request.tier());
        customer.setTotalOrders(0);
        
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
    public CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        
        if (!customer.getEmail().equals(customerDTO.email()) && 
            customerRepository.existsByEmail(customerDTO.email())) {
            throw new IllegalArgumentException("Email already exists");
        }
        
        customer.setName(customerDTO.name());
        customer.setEmail(customerDTO.email());
        
        return toDTO(customerRepository.save(customer));
    }

    private CustomerDTO toDTO(Customer customer) {
        return new CustomerDTO(
            customer.getId(),
            customer.getName(),
            customer.getEmail(),
            customer.getTier(),
            customer.getTotalOrders()
        );
    }
} 
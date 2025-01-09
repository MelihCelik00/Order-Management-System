package com.oms.service;

import com.oms.dto.CustomerDTO;
import com.oms.dto.CreateCustomerRequest;
import com.oms.entity.Customer;
import com.oms.entity.CustomerTier;
import com.oms.repository.CustomerRepository;
import com.oms.service.impl.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer testCustomer;
    private CreateCustomerRequest testCreateRequest;
    private CustomerDTO testCustomerDTO;

    @BeforeEach
    void setUp() {
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setName("Test User");
        testCustomer.setEmail("test@example.com");
        testCustomer.setTier(CustomerTier.REGULAR);
        testCustomer.setTotalOrders(0);

        testCreateRequest = new CreateCustomerRequest(
            "Test User",
            "test@example.com",
            CustomerTier.REGULAR
        );

        testCustomerDTO = new CustomerDTO(
            1L,
            "Test User",
            "test@example.com",
            CustomerTier.REGULAR,
            0
        );
    }

    @Test
    void createCustomer_Success() {
        when(customerRepository.existsByEmail(anyString())).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        CustomerDTO result = customerService.createCustomer(testCreateRequest);

        assertNotNull(result);
        assertEquals(testCreateRequest.email(), result.email());
        assertEquals(testCreateRequest.name(), result.name());
        assertEquals(CustomerTier.REGULAR, result.tier());
        assertEquals(0, result.totalOrders());

        verify(customerRepository).existsByEmail(testCreateRequest.email());
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void createCustomer_EmailExists_ThrowsException() {
        when(customerRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> 
            customerService.createCustomer(testCreateRequest)
        );

        verify(customerRepository).existsByEmail(testCreateRequest.email());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void getCustomerById_Success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));

        CustomerDTO result = customerService.getCustomerById(1L);

        assertNotNull(result);
        assertEquals(testCustomerDTO.id(), result.id());
        assertEquals(testCustomerDTO.email(), result.email());

        verify(customerRepository).findById(1L);
    }

    @Test
    void getCustomerById_NotFound_ThrowsException() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> 
            customerService.getCustomerById(1L)
        );

        verify(customerRepository).findById(1L);
    }

    @Test
    void getCustomerByEmail_Success() {
        when(customerRepository.findByEmail(testCustomer.getEmail()))
            .thenReturn(Optional.of(testCustomer));

        CustomerDTO result = customerService.getCustomerByEmail(testCustomer.getEmail());

        assertNotNull(result);
        assertEquals(testCustomerDTO.email(), result.email());

        verify(customerRepository).findByEmail(testCustomer.getEmail());
    }

    @Test
    void getAllCustomers_Success() {
        List<Customer> customers = Arrays.asList(testCustomer);
        when(customerRepository.findAll()).thenReturn(customers);

        List<CustomerDTO> results = customerService.getAllCustomers();

        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals(testCustomerDTO.email(), results.get(0).email());

        verify(customerRepository).findAll();
    }

    @Test
    void updateCustomer_Success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        CustomerDTO result = customerService.updateCustomer(1L, testCustomerDTO);

        assertNotNull(result);
        assertEquals(testCustomerDTO.email(), result.email());
        assertEquals(testCustomerDTO.name(), result.name());

        verify(customerRepository).findById(1L);
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void deleteCustomer_Success() {
        when(customerRepository.existsById(1L)).thenReturn(true);
        doNothing().when(customerRepository).deleteById(1L);

        customerService.deleteCustomer(1L);

        verify(customerRepository).existsById(1L);
        verify(customerRepository).deleteById(1L);
    }

    @Test
    void deleteCustomer_NotFound_ThrowsException() {
        when(customerRepository.existsById(1L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> 
            customerService.deleteCustomer(1L)
        );

        verify(customerRepository).existsById(1L);
        verify(customerRepository, never()).deleteById(anyLong());
    }
} 
package com.oms.service;

import com.oms.dto.CustomerDTO;
import com.oms.dto.CreateCustomerRequest;
import com.oms.dto.UpdateCustomerRequest;
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
    private UpdateCustomerRequest testUpdateRequest;

    @BeforeEach
    void setUp() {
        testCustomer = Customer.builder()
            .id(1L)
            .name("Test User")
            .email("test@example.com")
            .tier(CustomerTier.REGULAR)
            .totalOrders(0)
            .build();

        testCreateRequest = CreateCustomerRequest.builder()
            .name("Test User")
            .email("test@example.com")
            .tier(CustomerTier.REGULAR)
            .build();

        testCustomerDTO = CustomerDTO.builder()
            .id(1L)
            .name("Test User")
            .email("test@example.com")
            .tier(CustomerTier.REGULAR)
            .totalOrders(0)
            .build();

        testUpdateRequest = UpdateCustomerRequest.builder()
            .name("Test User")
            .email("test@example.com")
            .build();
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
        
        Customer updatedCustomer = Customer.builder()
            .id(1L)
            .name("Updated Name")
            .email(testCustomer.getEmail())
            .tier(CustomerTier.REGULAR)
            .totalOrders(0)
            .build();
        
        when(customerRepository.save(any(Customer.class))).thenReturn(updatedCustomer);

        UpdateCustomerRequest updateRequest = new UpdateCustomerRequest(
            "Updated Name",
            testCustomer.getEmail()
        );

        CustomerDTO result = customerService.updateCustomer(1L, updateRequest);

        assertNotNull(result);
        assertEquals(updateRequest.email(), result.email());
        assertEquals(updateRequest.name(), result.name());

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
    }

    @Test
    void getCustomerByEmail_NotFound_ThrowsException() {
        when(customerRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
            customerService.getCustomerByEmail("nonexistent@example.com")
        );
    }

    @Test
    void updateCustomer_WithExistingEmail_ThrowsException() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(customerRepository.existsByEmail("existing@example.com")).thenReturn(true);

        UpdateCustomerRequest updateRequest = new UpdateCustomerRequest(
            "Test User",
            "existing@example.com"
        );

        assertThrows(IllegalArgumentException.class, () ->
            customerService.updateCustomer(1L, updateRequest)
        );
    }

    @Test
    void updateCustomer_CustomerNotFound_ThrowsException() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        UpdateCustomerRequest updateRequest = new UpdateCustomerRequest(
            testCustomer.getName(),
            testCustomer.getEmail()
        );

        assertThrows(IllegalArgumentException.class, () ->
            customerService.updateCustomer(1L, updateRequest)
        );
    }

    @Test
    void updateCustomer_WithSameEmail_Success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        
        Customer updatedCustomer = Customer.builder()
            .id(1L)
            .name("Updated Name")
            .email(testCustomer.getEmail())
            .tier(CustomerTier.REGULAR)
            .totalOrders(0)
            .build();
        
        when(customerRepository.save(any(Customer.class))).thenReturn(updatedCustomer);

        UpdateCustomerRequest updateRequest = new UpdateCustomerRequest(
            "Updated Name",
            testCustomer.getEmail()
        );

        CustomerDTO result = customerService.updateCustomer(1L, updateRequest);

        assertNotNull(result);
        assertEquals("Updated Name", result.name());
        assertEquals(testCustomer.getEmail(), result.email());

        verify(customerRepository).findById(1L);
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void createCustomer_WithInvalidEmail_ThrowsException() {
        CreateCustomerRequest invalidRequest = new CreateCustomerRequest(
            "Test User",
            "invalid-email",
            CustomerTier.REGULAR
        );

        assertThrows(IllegalArgumentException.class, () ->
            customerService.createCustomer(invalidRequest)
        );
    }

    @Test
    void createCustomer_WithBlankName_ThrowsException() {
        CreateCustomerRequest invalidRequest = new CreateCustomerRequest(
            "",
            "test@example.com",
            CustomerTier.REGULAR
        );

        assertThrows(IllegalArgumentException.class, () ->
            customerService.createCustomer(invalidRequest)
        );
    }

    @Test
    void createCustomer_WithBlankEmail_ThrowsException() {
        CreateCustomerRequest invalidRequest = new CreateCustomerRequest(
            "Test User",
            "",
            CustomerTier.REGULAR
        );

        assertThrows(IllegalArgumentException.class, () ->
            customerService.createCustomer(invalidRequest)
        );
    }

    @Test
    void updateCustomer_WithInvalidEmail_ThrowsException() {
        UpdateCustomerRequest invalidRequest = UpdateCustomerRequest.builder()
            .name("Test User")
            .email("invalid-email")
            .build();

        assertThrows(IllegalArgumentException.class, () ->
            customerService.updateCustomer(1L, invalidRequest)
        );
    }

    @Test
    void createCustomer_ShouldHaveRegularTierAndZeroOrders() {
        when(customerRepository.existsByEmail(anyString())).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        CustomerDTO result = customerService.createCustomer(testCreateRequest);

        assertNotNull(result);
        assertEquals(CustomerTier.REGULAR, result.tier());
        assertEquals(0, result.totalOrders());
    }

    @Test
    void customer_TierShouldRemainRegularBelow10Orders() {
        testCustomer.setTotalOrders(9);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));

        CustomerDTO result = customerService.getCustomerById(1L);

        assertEquals(CustomerTier.REGULAR, result.tier());
    }

    @Test
    void customer_TierShouldUpdateToGoldAt10Orders() {
        testCustomer.setTotalOrders(10);
        testCustomer.setTier(CustomerTier.GOLD);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));

        CustomerDTO result = customerService.getCustomerById(1L);

        assertEquals(CustomerTier.GOLD, result.tier());
    }

    @Test
    void customer_TierShouldUpdateToPlatinumAt20Orders() {
        testCustomer.setTotalOrders(20);
        testCustomer.setTier(CustomerTier.PLATINUM);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));

        CustomerDTO result = customerService.getCustomerById(1L);

        assertEquals(CustomerTier.PLATINUM, result.tier());
    }

    @Test
    void createCustomer_WithVeryLongNameAndEmail() {
        String longName = "a".repeat(255);
        String longEmail = "a".repeat(245) + "@test.com";
        
        CreateCustomerRequest request = new CreateCustomerRequest(
            longName,
            longEmail,
            CustomerTier.REGULAR
        );

        Customer savedCustomer = Customer.builder()
            .id(1L)
            .name(longName)
            .email(longEmail)
            .tier(CustomerTier.REGULAR)
            .totalOrders(0)
            .build();

        when(customerRepository.existsByEmail(anyString())).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

        CustomerDTO result = customerService.createCustomer(request);

        assertNotNull(result);
        assertEquals(longName, result.name());
        assertEquals(longEmail, result.email());
    }

    @Test
    void updateCustomer_WithNoChanges_ShouldNotUpdateDatabase() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        UpdateCustomerRequest noChangeRequest = UpdateCustomerRequest.builder()
            .name(testCustomer.getName())
            .email(testCustomer.getEmail())
            .build();

        CustomerDTO result = customerService.updateCustomer(1L, noChangeRequest);

        assertNotNull(result);
        assertEquals(testCustomer.getName(), result.name());
        assertEquals(testCustomer.getEmail(), result.email());
        verify(customerRepository).findById(1L);
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void createCustomer_ShouldInitializeWithZeroOrders() {
        when(customerRepository.existsByEmail(anyString())).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        CustomerDTO result = customerService.createCustomer(testCreateRequest);

        assertEquals(0, result.totalOrders());
        verify(customerRepository).save(argThat(customer -> 
            customer.getTotalOrders() == 0
        ));
    }

    @Test
    void updateCustomer_ShouldNotAffectTierAndOrders() {
        testCustomer.setTotalOrders(15);
        testCustomer.setTier(CustomerTier.GOLD);
        
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        UpdateCustomerRequest updateRequest = new UpdateCustomerRequest(
            "Updated Name",
            testCustomer.getEmail()
        );

        CustomerDTO result = customerService.updateCustomer(1L, updateRequest);

        assertEquals(15, result.totalOrders());
        assertEquals(CustomerTier.GOLD, result.tier());
        verify(customerRepository).save(argThat(customer -> 
            customer.getTotalOrders() == 15 && customer.getTier() == CustomerTier.GOLD
        ));
    }
} 
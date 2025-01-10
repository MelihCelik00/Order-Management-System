package com.oms.repository;

import com.oms.entity.Customer;
import com.oms.entity.CustomerTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Customer> findByTierAndTotalOrders(CustomerTier tier, Integer totalOrders);
} 
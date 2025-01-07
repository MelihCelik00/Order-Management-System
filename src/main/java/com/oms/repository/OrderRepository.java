package com.oms.repository;

import com.oms.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o WHERE o.customer.id = :customerId")
    List<Order> findByCustomerId(Long customerId);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.customer.id = :customerId")
    int countByCustomerId(Long customerId);
} 
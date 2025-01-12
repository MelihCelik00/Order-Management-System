package com.oms.service.impl;

import com.oms.entity.Customer;
import com.oms.entity.CustomerTier;
import com.oms.repository.CustomerRepository;
import com.oms.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TierProgressionScheduler {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private NotificationService notificationService;


    @Scheduled(cron = "0 0 0 * * ?")
    public void checkTierProgressions() {
        // customers close to GOLD tier (9 orders)
        List<Customer> nearGoldCustomers = customerRepository.findByTierAndTotalOrders(
            CustomerTier.REGULAR, 9);
        nearGoldCustomers.forEach(customer -> 
            notificationService.sendTierProgressionAlert(customer, 1));

        // customers close to PLATINUM tier (19 orders)
        List<Customer> nearPlatinumCustomers = customerRepository.findByTierAndTotalOrders(
            CustomerTier.GOLD, 19);
        nearPlatinumCustomers.forEach(customer -> 
            notificationService.sendTierProgressionAlert(customer, 1));
    }
} 
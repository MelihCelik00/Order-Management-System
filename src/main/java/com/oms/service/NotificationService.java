package com.oms.service;

import com.oms.entity.Customer;

public interface NotificationService {
    void sendTierProgressionAlert(Customer customer, int ordersToNextTier);
    void sendTierUpgradeNotification(Customer customer);
} 
package com.oms.service.impl;

import com.oms.entity.Customer;
import com.oms.entity.CustomerTier;
import com.oms.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    @Override
    public void sendTierProgressionAlert(Customer customer, int ordersToNextTier) {
        CustomerTier nextTier = getNextTier(customer.getTier());
        String message = String.format(
            "Dear %s, you have placed %d orders with us. Place %d more order%s to be promoted to %s tier and enjoy %.0f%% discount!",
            customer.getName(),
            customer.getTotalOrders(),
            ordersToNextTier,
            ordersToNextTier == 1 ? "" : "s",
            nextTier,
            nextTier.getDiscountPercentage() * 100
        );
        
        sendEmail(customer.getEmail(), "Almost there! You're close to a tier upgrade!", message);
    }

    @Override
    public void sendTierUpgradeNotification(Customer customer) {
        String message = String.format(
            "Congratulations %s! You have been upgraded to %s tier. You now enjoy a %.0f%% discount on all your orders!",
            customer.getName(),
            customer.getTier(),
            customer.getTier().getDiscountPercentage() * 100
        );
        
        sendEmail(customer.getEmail(), "Congratulations on Your Tier Upgrade!", message);
    }

    private CustomerTier getNextTier(CustomerTier currentTier) {
        return switch (currentTier) {
            case REGULAR -> CustomerTier.GOLD;
            case GOLD -> CustomerTier.PLATINUM;
            case PLATINUM -> CustomerTier.PLATINUM; // Already at highest tier
        };
    }

    private void sendEmail(String email, String subject, String message) {
        // Dummy implementation - in production, this would use JavaMailSender or similar
        logger.info("Sending email to: {}", email);
        logger.info("Subject: {}", subject);
        logger.info("Message: {}", message);
        System.out.println("Sent mail to customer: " + email);
    }
} 
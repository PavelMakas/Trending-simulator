package com.auction.model;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicDouble;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Buyer {
    private static final Logger logger = LogManager.getLogger(Buyer.class);
    
    private final String name;
    private final AtomicDouble budget;
    private final ConcurrentHashMap<String, Boolean> blockedLots;

    public Buyer(String name, double budget) {
        this.name = name;
        this.budget = new AtomicDouble(budget);
        this.blockedLots = new ConcurrentHashMap<>();
    }

    public boolean placeBid(Lot lot, double bidAmount) {
        if (isBlockedForLot(lot) || budget.get() < bidAmount) {
            logger.info("Buyer {} cannot place bid on lot {}: blocked or insufficient funds", name, lot.getName());
            return false;
        }
        return true;
    }

    public boolean purchaseLot(Lot lot, double amount) {
        if (budget.get() < amount) {
            blockForLot(lot);
            logger.info("Buyer {} failed to purchase lot {}: insufficient funds", name, lot.getName());
            return false;
        }
        
        double newBudget = budget.addAndGet(-amount);
        logger.info("Buyer {} purchased lot {} for {}. Remaining budget: {}", name, lot.getName(), amount, newBudget);
        return true;
    }

    public void blockForLot(Lot lot) {
        blockedLots.put(lot.getName(), true);
    }

    public boolean isBlockedForLot(Lot lot) {
        return blockedLots.getOrDefault(lot.getName(), false);
    }

    public String getName() {
        return name;
    }

    public double getBudget() {
        return budget.get();
    }

    public boolean hasFunds() {
        return budget.get() > 0;
    }
} 
package com.auction.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Buyer {
    private static final Logger logger = LogManager.getLogger(Buyer.class);

    private final String name;
    private int budget;
    private final LotStorage lotStorage;

    public Buyer(String name, int budget) {
        this.name = name;
        this.budget = budget;
        this.lotStorage = LotStorage.getInstance();
    }

    public boolean placeBid(Lot lot, int bidAmount) {
        if (budget < bidAmount || lotStorage.isLotBlocked(lot.getName())) {
            logger.info("Buyer {} cannot place bid on lot {}: blocked or insufficient funds", name, lot.getName());
            return false;
        }
        return true;
    }

    public boolean purchaseLot(Lot lot, int amount) {
        if (budget < amount) {
            lotStorage.blockLot(lot.getName());
            logger.info("Buyer {} failed to purchase lot {}: insufficient funds", name, lot.getName());
            return false;
        }

        budget = budget - amount;
        logger.info("Buyer {} purchased lot {} for {}. Remaining budget: {}", name, lot.getName(), amount, budget);
        return true;
    }

    public String getName() {
        return name;
    }

    public int getBudget() {
        return budget;
    }

    public boolean hasFunds() {
        return budget > 0;
    }
}
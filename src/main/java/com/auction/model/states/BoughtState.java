package com.auction.model.states;

import com.auction.model.Lot;
import com.auction.model.LotState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BoughtState implements LotState {
    private static final Logger logger = LogManager.getLogger(BoughtState.class);
    private final Lot lot;

    public BoughtState(Lot lot) {
        this.lot = lot;
    }

    @Override
    public void process() {
        logger.info("Lot {} has been bought by {}", lot.getName(), lot.getOwnerName());
    }
} 
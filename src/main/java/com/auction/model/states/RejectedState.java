package com.auction.model.states;

import com.auction.model.Lot;
import com.auction.model.LotState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RejectedState implements LotState {
    private static final Logger logger = LogManager.getLogger(RejectedState.class);
    private final Lot lot;

    public RejectedState(Lot lot) {
        this.lot = lot;
    }

    @Override
    public void process() {
        logger.info("Lot {} has been rejected", lot.getName());
    }
} 
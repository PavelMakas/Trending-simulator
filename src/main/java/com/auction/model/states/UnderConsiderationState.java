package com.auction.model.states;

import com.auction.model.Lot;
import com.auction.model.LotState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UnderConsiderationState implements LotState {
    private static final Logger logger = LogManager.getLogger(UnderConsiderationState.class);
    private final Lot lot;

    public UnderConsiderationState(Lot lot) {
        this.lot = lot;
    }

    @Override
    public void process() {
        if (lot.isExpired()) {
            logger.info("Lot {} has expired", lot.getName());
            lot.setState(new RejectedState(lot));
        }
    }
} 
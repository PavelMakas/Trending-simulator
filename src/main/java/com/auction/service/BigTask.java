package com.auction.service;

import com.auction.model.Buyer;
import com.auction.model.Lot;

import java.util.concurrent.Callable;

public class BigTask implements Callable<Integer> {

    private final Buyer buyer;
    private final Lot lot;

    public BigTask(Buyer buyer, Lot lot) {
        this.buyer = buyer;
        this.lot = lot;
    }

    @Override
    public Integer call() throws Exception {
        if (buyer.placeBid(lot, lot.getCurrentPrice())) {
            return lot.getCurrentPrice();
        }
        return 0;
    }
}

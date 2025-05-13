package com.auction.model;

import java.time.LocalDateTime;
import java.util.concurrent.locks.ReentrantLock;

import com.auction.model.states.UnderConsiderationState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Lot {
    private static final Logger logger = LogManager.getLogger(Lot.class);
    private final ReentrantLock lock = new ReentrantLock();

    private final String name;
    private final int initialPrice;
    private final int autoBuyPrice;
    private final LocalDateTime expirationTime;
    private int currentPrice;
    private Buyer owner;
    private LotState state;

    public Lot(String name, int initialPrice, int autoBuyPrice, LocalDateTime expirationTime) {
        this.name = name;
        this.initialPrice = initialPrice;
        this.autoBuyPrice = autoBuyPrice;
        this.expirationTime = expirationTime;
        this.currentPrice = initialPrice;
        this.state = new UnderConsiderationState(this);
        this.owner = null;
    }

    public void setState(LotState state) {
        this.state = state;
    }

    public boolean tryLock() {
        return lock.tryLock();
    }

    public void unlock() {
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public int getInitialPrice() {
        return initialPrice;
    }

    public int getAutoBuyPrice() {
        return autoBuyPrice;
    }

    public LocalDateTime getExpirationTime() {
        return expirationTime;
    }

    public int getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(int currentPrice) {
        this.currentPrice = currentPrice;
    }

    public Buyer getOwner() {
        return owner;
    }

    public void setOwner(Buyer owner) {
        this.owner = owner;
    }

    public LotState getState() {
        return state;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expirationTime);
    }

    public void processState() {
        state.process();
    }
} 
package com.auction.model;

import java.time.LocalDateTime;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Lot {
    private static final Logger logger = LogManager.getLogger(Lot.class);
    private final ReentrantLock lock = new ReentrantLock();
    
    private final String name;
    private final double initialPrice;
    private final double autoBuyPrice;
    private final LocalDateTime expirationTime;
    private double currentPrice;
    private String ownerName;
    private LotState state;
    
    public Lot(String name, double initialPrice, double autoBuyPrice, LocalDateTime expirationTime) {
        this.name = name;
        this.initialPrice = initialPrice;
        this.autoBuyPrice = autoBuyPrice;
        this.expirationTime = expirationTime;
        this.currentPrice = initialPrice;
        this.state = new UnderConsiderationState(this);
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
    public String getName() { return name; }
    public double getInitialPrice() { return initialPrice; }
    public double getAutoBuyPrice() { return autoBuyPrice; }
    public LocalDateTime getExpirationTime() { return expirationTime; }
    public double getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(double currentPrice) { this.currentPrice = currentPrice; }
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    public LotState getState() { return state; }
    
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expirationTime);
    }
    
    public void processState() {
        state.process();
    }
} 
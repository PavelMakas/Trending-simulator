package com.auction.service;

import com.auction.model.Buyer;
import com.auction.model.Lot;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AuctionManager {
    private static final Logger logger = LogManager.getLogger(AuctionManager.class);
    private static final AtomicReference<AuctionManager> instance = new AtomicReference<>();
    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    
    private final List<Lot> activeLots;
    private final List<Buyer> buyers;
    private final ExecutorService executorService;
    private final ScheduledExecutorService scheduler;
    
    private AuctionManager() {
        this.activeLots = new ArrayList<>();
        this.buyers = new ArrayList<>();
        this.executorService = Executors.newCachedThreadPool();
        this.scheduler = Executors.newScheduledThreadPool(1);
    }
    
    public static AuctionManager getInstance() {
        if (instance.get() == null) {
            lock.writeLock().lock();
            try {
                if (instance.get() == null) {
                    instance.set(new AuctionManager());
                }
            } finally {
                lock.writeLock().unlock();
            }
        }
        return instance.get();
    }
    
    public void addLot(Lot lot) {
        lock.writeLock().lock();
        try {
            activeLots.add(lot);
            logger.info("Added new lot: {}", lot.getName());
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    public void addBuyer(Buyer buyer) {
        lock.writeLock().lock();
        try {
            buyers.add(buyer);
            logger.info("Added new buyer: {}", buyer.getName());
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    public void startAuction() {
        lock.readLock().lock();
        try {
            for (Lot lot : activeLots) {
                executorService.submit(() -> processLot(lot));
            }
        } finally {
            lock.readLock().unlock();
        }
    }
    
    private void processLot(Lot lot) {
        try {
            // Start bidding process
            CountDownLatch biddingLatch = new CountDownLatch(1);
            List<Future<Double>> bids = new ArrayList<>();
            
            // Collect bids from buyers
            for (Buyer buyer : buyers) {
                if (buyer.hasFunds()) {
                    bids.add(executorService.submit(() -> {
                        if (buyer.placeBid(lot, lot.getCurrentPrice())) {
                            return lot.getCurrentPrice();
                        }
                        return 0.0;
                    }));
                }
            }
            
            // Wait for bidding period
            scheduler.schedule(() -> biddingLatch.countDown(), 10, TimeUnit.SECONDS);
            biddingLatch.await();
            
            // Process bids
            double highestBid = 0.0;
            Buyer winningBuyer = null;
            
            for (Future<Double> bid : bids) {
                double bidAmount = bid.get();
                if (bidAmount > highestBid) {
                    highestBid = bidAmount;
                    // Find buyer who placed this bid
                    for (Buyer buyer : buyers) {
                        if (buyer.getBudget() >= bidAmount) {
                            winningBuyer = buyer;
                            break;
                        }
                    }
                }
            }
            
            // Process purchase
            if (winningBuyer != null && winningBuyer.purchaseLot(lot, highestBid)) {
                lot.setOwnerName(winningBuyer.getName());
                lot.setState(new com.auction.model.states.BoughtState(lot));
            } else {
                lot.setState(new com.auction.model.states.RejectedState(lot));
            }
            
        } catch (Exception e) {
            logger.error("Error processing lot {}: {}", lot.getName(), e.getMessage());
            lot.setState(new com.auction.model.states.RejectedState(lot));
        }
    }
    
    public void shutdown() {
        executorService.shutdown();
        scheduler.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
            if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
} 
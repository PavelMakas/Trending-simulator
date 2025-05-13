package com.auction.service;

import com.auction.model.Buyer;
import com.auction.model.Lot;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.auction.model.states.BoughtState;
import com.auction.model.states.RejectedState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AuctionManager {
    private static final Logger logger = LogManager.getLogger(AuctionManager.class);
    private static final AtomicReference<AuctionManager> instance = new AtomicReference<>();
    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private static final Random random = new Random();

    private final List<Lot> activeLots;
    private final List<Buyer> buyers;
    private final ExecutorService executorService;
    private final ScheduledExecutorService scheduler;
    private final ReentrantLock lotLocker = new ReentrantLock();
    private final ConcurrentHashMap<String, ReentrantLock> bidLocks = new ConcurrentHashMap<>();
    
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
            bidLocks.put(lot.getName(), new ReentrantLock());
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
            CountDownLatch biddingLatch = new CountDownLatch(1);
            List<Future<Integer>> bids = new ArrayList<>();
            ReentrantLock bidLock = bidLocks.get(lot.getName());

            for (Buyer buyer : buyers) {
                if (buyer.hasFunds()) {
                    bids.add(executorService.submit(() -> {
                        int lastPrice = lot.getInitialPrice();
                        boolean lotSold = false;
                        
                        while (!lotSold && buyer.hasFunds()) {
                            bidLock.lock();
                            try {
                                int amount = lot.getCurrentPrice() + random.nextInt(5, 50);
                                if (buyer.placeBid(lot, amount)) {
                                    lot.setCurrentPrice(amount);
                                    lot.setOwner(buyer);
                                    lastPrice = amount;
                                }
                                
                                if (lot.getState() instanceof BoughtState) {
                                    lotSold = true;
                                }
                            } finally {
                                bidLock.unlock();
                            }
                            
                            Thread.sleep(random.nextInt(200, 1000));
                        }
                        return lastPrice;
                    }));
                }
            }
            
            int taskNumber = bids.size();
            int taskCompleted = 0;
            while(taskCompleted < taskNumber) {
                for (Future<Integer> bid : bids) {
                    if (bid.isDone() || bid.isCancelled()) {
                        taskCompleted++;
                    }
                }
                Thread.sleep(300);
            }

            Buyer winningBuyer = lot.getOwner();
            if (winningBuyer != null) {
                lotLocker.lock();
                try {
                    if (winningBuyer.purchaseLot(lot, lot.getCurrentPrice())) {
                        lot.setState(new BoughtState(lot));
                    } else {
                        lot.setState(new RejectedState(lot));
                    }
                } finally {
                    lotLocker.unlock();
                }
            } else {
                lot.setState(new RejectedState(lot));
            }

        } catch (Exception e) {
            logger.error("Error processing lot {}: {}", lot.getName(), e.getMessage());
            lot.setState(new RejectedState(lot));
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
            System.out.println(e);
            Thread.currentThread().interrupt();
        }
        finally {
            executorService.shutdownNow();
            scheduler.shutdownNow();
        }
    }
} 
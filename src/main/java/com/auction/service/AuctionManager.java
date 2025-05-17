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
    private volatile boolean auctionRunning = false;
    
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
        lock.writeLock().lock();
        try {
            if (auctionRunning) {
                return;
            }
            auctionRunning = true;
            
            for (Buyer buyer : buyers) {
                executorService.submit(() -> processBuyer(buyer));
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    private void processBuyer(Buyer buyer) {
        while (auctionRunning && buyer.hasFunds()) {
            Lot availableLot = findAvailableLot();
            if (availableLot == null) {
                break;
            }
            
            ReentrantLock bidLock = bidLocks.get(availableLot.getName());
            bidLock.lock();
            try {
                if (availableLot.getState() instanceof BoughtState) {
                    continue;
                }
                
                int amount = availableLot.getCurrentPrice() + random.nextInt(5, 50);
                if (buyer.placeBid(availableLot, amount)) {
                    availableLot.setCurrentPrice(amount);
                    availableLot.setOwner(buyer);
                    
                    if (buyer.purchaseLot(availableLot, amount)) {
                        availableLot.setState(new BoughtState(availableLot));
                    }
                }
            } finally {
                bidLock.unlock();
            }
            
            try {
                Thread.sleep(random.nextInt(200, 1000));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    private Lot findAvailableLot() {
        lock.readLock().lock();
        try {
            return activeLots.stream()
                .filter(lot -> !(lot.getState() instanceof BoughtState))
                .findFirst()
                .orElse(null);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    public void shutdown() {
        auctionRunning = false;
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
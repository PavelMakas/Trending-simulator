package com.auction.model;

import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class LotStorage {
    private static LotStorage instance;
    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final ConcurrentSkipListSet<String> blockedLots;
    private final ExecutorService executorService;
    private final ConcurrentSkipListSet<Future<Void>> bids;

    private LotStorage(ExecutorService executorService) {
        this.blockedLots = new ConcurrentSkipListSet<>();
        this.executorService = executorService;
        this.bids = new ConcurrentSkipListSet<>();
    }

    public static LotStorage getInstance(ExecutorService executorService) {
        if (instance == null) {
            lock.writeLock().lock();
            try {
                if (instance == null) {
                    instance = new LotStorage(executorService);
                }
            } finally {
                lock.writeLock().unlock();
            }
        }
        return instance;
    }

    public void blockLot(String lotName) {
        lock.writeLock().lock();
        try {
            blockedLots.add(lotName);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean isLotBlocked(String lotName) {
        lock.readLock().lock();
        try {
            return blockedLots.contains(lotName);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void unblockLot(String lotName) {
        lock.writeLock().lock();
        try {
            blockedLots.remove(lotName);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void startAuction() {
        for (Lot lot : activeLots) {
            executorService.submit(() -> processLot(lot));
        }
    }

    private void processLot(Lot lot) {
        for (Buyer buyer : buyers) {
            if (buyer.hasFunds()) {
                bids.add(executorService.submit(() -> {
                    // Bidding logic for this specific lot
                }));
            }
        }
    }
} 
package com.auction.model;

import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LotStorage {
    private static LotStorage instance;
    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final ConcurrentSkipListSet<String> blockedLots;

    private LotStorage() {
        this.blockedLots = new ConcurrentSkipListSet<>();
    }

    public static LotStorage getInstance() {
        if (instance == null) {
            lock.writeLock().lock();
            try {
                if (instance == null) {
                    instance = new LotStorage();
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
} 
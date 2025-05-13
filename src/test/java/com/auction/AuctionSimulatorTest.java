package com.auction;

import com.auction.model.Buyer;
import com.auction.model.Lot;
import com.auction.model.states.BoughtState;
import com.auction.model.states.RejectedState;
import com.auction.model.states.UnderConsiderationState;
import com.auction.service.AuctionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;

public class AuctionSimulatorTest {
    private AuctionManager auctionManager;
    private Lot testLot;
    private Buyer testBuyer;

    @BeforeEach
    void setUp() {
        auctionManager = AuctionManager.getInstance();
        testLot = new Lot("Test Lot", 1000, 1500, LocalDateTime.now().plusMinutes(2));
        testBuyer = new Buyer("Test Buyer", 5000);
    }

    @Test
    void testLotInitialState() {
        assertTrue(testLot.getState() instanceof UnderConsiderationState);
    }

    @Test
    void testLotExpiration() {
        Lot expiredLot = new Lot("Expired Lot", 1000, 1500, LocalDateTime.now().minusMinutes(1));
        expiredLot.processState();
        assertTrue(expiredLot.getState() instanceof RejectedState);
    }

    @Test
    void testBuyerBudget() {
        assertTrue(testBuyer.hasFunds());
        assertTrue(testBuyer.purchaseLot(testLot, 1000));
        assertEquals(4000.0, testBuyer.getBudget());
    }

    @Test
    void testBuyerInsufficientFunds() {
        assertFalse(testBuyer.purchaseLot(testLot, 6000));
        assertTrue(testBuyer.isBlockedForLot(testLot));
    }

    @Test
    void testBuyerBlockedForLot() {
        testBuyer.blockForLot(testLot);
        assertFalse(testBuyer.placeBid(testLot, 1000));
    }

    @Test
    void testLotPriceUpdate() {
        int newPrice = 1200;
        testLot.setCurrentPrice(newPrice);
        assertEquals(newPrice, testLot.getCurrentPrice());
    }

    @Test
    void testLotOwnerAssignment() {
        testBuyer.purchaseLot(testLot, 1000);
        testLot.setOwnerName(testBuyer);
        assertEquals(testBuyer.getName(), testLot.getOwnerName());
    }

    @Test
    void testLotStateTransition() {
        testBuyer.purchaseLot(testLot, 1000);
        testLot.setState(new BoughtState(testLot));
        assertTrue(testLot.getState() instanceof BoughtState);
    }

    @Test
    void testAuctionManagerSingleton() {
        AuctionManager instance1 = AuctionManager.getInstance();
        AuctionManager instance2 = AuctionManager.getInstance();
        assertSame(instance1, instance2);
    }

    @Test
    void testLotLocking() {
        assertTrue(testLot.tryLock());
        testLot.unlock();
    }

    @Test
    void testBuyerBudgetAtomicity() {
        testBuyer.purchaseLot(testLot, 1000);
        testBuyer.purchaseLot(testLot, 1000);
        assertEquals(3000.0, testBuyer.getBudget());
    }

    @Test
    void testLotExpirationTime() {
        assertFalse(testLot.isExpired());
        Lot expiredLot = new Lot("Expired", 1000, 1500, LocalDateTime.now().minusMinutes(1));
        assertTrue(expiredLot.isExpired());
    }

    @Test
    void testBuyerInitialState() {
        assertFalse(testBuyer.isBlockedForLot(testLot));
        assertEquals(5000, testBuyer.getBudget());
    }

    @Test
    void testLotAutoBuyPrice() {
        assertEquals(1500, testLot.getAutoBuyPrice());
    }

    @Test
    void testBuyerName() {
        assertEquals("Test Buyer", testBuyer.getName());
    }

    @Test
    void testLotName() {
        assertEquals("Test Lot", testLot.getName());
    }

    @Test
    void testLotInitialPrice() {
        assertEquals(1000, testLot.getInitialPrice());
    }

    @Test
    void testBuyerBudgetZero() {
        testBuyer.purchaseLot(testLot, 5000);
        assertFalse(testBuyer.hasFunds());
    }

    @Test
    void testLotStateProcess() {
        testLot.processState();
        assertTrue(testLot.getState() instanceof UnderConsiderationState);
    }

    @Test
    void testBuyerMultiplePurchases() {
        assertTrue(testBuyer.purchaseLot(testLot, 1000));
        assertTrue(testBuyer.purchaseLot(testLot, 1000));
        assertTrue(testBuyer.purchaseLot(testLot, 1000));
        assertEquals(2000, testBuyer.getBudget());
    }
} 
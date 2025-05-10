package com.auction.util;

import com.auction.model.Buyer;
import com.auction.model.Lot;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class DataGeneratorTest {
    
    @Test
    void testGenerateLots() {
        int count = 1000;
        List<Lot> lots = DataGenerator.generateLots(count);
        
        assertEquals(count, lots.size());
        
        // Test lot properties
        for (Lot lot : lots) {
            assertNotNull(lot.getName());
            assertTrue(lot.getInitialPrice() >= 100.0 && lot.getInitialPrice() <= 10000.0);
            assertTrue(lot.getAutoBuyPrice() >= lot.getInitialPrice());
            assertTrue(lot.getAutoBuyPrice() <= lot.getInitialPrice() * 1.5);
        }
    }
    
    @Test
    void testGenerateBuyers() {
        int count = 500;
        List<Buyer> buyers = DataGenerator.generateBuyers(count);
        
        assertEquals(count, buyers.size());
        
        // Test buyer properties
        for (Buyer buyer : buyers) {
            assertNotNull(buyer.getName());
            assertTrue(buyer.getName().contains(" ")); // Should have first and last name
            assertTrue(buyer.getBudget() >= 1000.0 && buyer.getBudget() <= 100000.0);
        }
    }
    
    @Test
    void testUniqueNames() {
        List<Buyer> buyers = DataGenerator.generateBuyers(500);
        long uniqueNames = buyers.stream()
            .map(Buyer::getName)
            .distinct()
            .count();
        
        // Should have some unique names (not all names will be unique due to random selection)
        assertTrue(uniqueNames > 100);
    }
    
    @Test
    void testUniqueLotNames() {
        List<Lot> lots = DataGenerator.generateLots(1000);
        long uniqueNames = lots.stream()
            .map(Lot::getName)
            .distinct()
            .count();
        
        // All lot names should be unique due to the numbering
        assertEquals(1000, uniqueNames);
    }
} 
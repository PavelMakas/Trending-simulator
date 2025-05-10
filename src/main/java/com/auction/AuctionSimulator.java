package com.auction;

import com.auction.config.ConfigurationReader;
import com.auction.model.Buyer;
import com.auction.model.Lot;
import com.auction.service.AuctionManager;
import com.auction.util.DataGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AuctionSimulator {
    private static final Logger logger = LogManager.getLogger(AuctionSimulator.class);
    private static final int LOT_COUNT = 1000;
    private static final int BUYER_COUNT = 500;
    
    public static void main(String[] args) {
        try {
            // Generate test data
            logger.info("Generating {} lots and {} buyers...", LOT_COUNT, BUYER_COUNT);
            List<Lot> lots = DataGenerator.generateLots(LOT_COUNT);
            List<Buyer> buyers = DataGenerator.generateBuyers(BUYER_COUNT);
            
            // Get auction manager instance
            AuctionManager auctionManager = AuctionManager.getInstance();
            
            // Add lots and buyers
            logger.info("Adding lots to auction...");
            for (Lot lot : lots) {
                auctionManager.addLot(lot);
            }
            
            logger.info("Adding buyers to auction...");
            for (Buyer buyer : buyers) {
                auctionManager.addBuyer(buyer);
            }
            
            // Start auction
            logger.info("Starting auction simulation with {} lots and {} buyers...", lots.size(), buyers.size());
            auctionManager.startAuction();
            
            // Wait for auction to complete
            TimeUnit.MINUTES.sleep(2);
            
            // Shutdown
            auctionManager.shutdown();
            logger.info("Auction simulation completed.");
            
        } catch (Exception e) {
            logger.error("Error in auction simulation: {}", e.getMessage());
        }
    }
} 
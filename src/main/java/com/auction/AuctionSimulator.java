package com.auction;

import com.auction.config.ConfigurationReader;
import com.auction.model.Buyer;
import com.auction.model.Lot;
import com.auction.service.AuctionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AuctionSimulator {
    private static final Logger logger = LogManager.getLogger(AuctionSimulator.class);
    
    public static void main(String[] args) {
        try {
            // Read configuration
            List<Lot> lots = ConfigurationReader.readLots("lots.txt");
            List<Buyer> buyers = ConfigurationReader.readBuyers("buyers.txt");
            
            // Get auction manager instance
            AuctionManager auctionManager = AuctionManager.getInstance();
            
            // Add lots and buyers
            for (Lot lot : lots) {
                auctionManager.addLot(lot);
            }
            
            for (Buyer buyer : buyers) {
                auctionManager.addBuyer(buyer);
            }
            
            // Start auction
            logger.info("Starting auction simulation...");
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
package com.auction.config;

import com.auction.model.Buyer;
import com.auction.model.Lot;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfigurationReader {
    private static final Logger logger = LogManager.getLogger(ConfigurationReader.class);
    
    public static List<Lot> readLots(String filename) {
        List<Lot> lots = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    String name = parts[0].trim();
                    double initialPrice = Double.parseDouble(parts[1].trim());
                    double autoBuyPrice = Double.parseDouble(parts[2].trim());
                    LocalDateTime expirationTime = LocalDateTime.parse(parts[3].trim());
                    
                    lots.add(new Lot(name, initialPrice, autoBuyPrice, expirationTime));
                }
            }
        } catch (IOException e) {
            logger.error("Error reading lots configuration: {}", e.getMessage());
        }
        return lots;
    }
    
    public static List<Buyer> readBuyers(String filename) {
        List<Buyer> buyers = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String name = parts[0].trim();
                    double budget = Double.parseDouble(parts[1].trim());
                    
                    buyers.add(new Buyer(name, budget));
                }
            }
        } catch (IOException e) {
            logger.error("Error reading buyers configuration: {}", e.getMessage());
        }
        return buyers;
    }
} 
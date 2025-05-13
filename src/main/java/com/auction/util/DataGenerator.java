package com.auction.util;

import com.auction.model.Buyer;
import com.auction.model.Lot;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DataGenerator {
    private static final String[] ITEM_TYPES = {
        "Antique", "Vintage", "Rare", "Collectible", "Limited Edition",
        "Classic", "Modern", "Contemporary", "Exclusive", "Premium"
    };
    
    private static final String[] ITEM_CATEGORIES = {
        "Vase", "Painting", "Watch", "Jewelry", "Furniture",
        "Sculpture", "Coin", "Stamp", "Book", "Car",
        "Wine", "Art", "Instrument", "Clock", "Carpet"
    };
    
    private static final String[] FIRST_NAMES = {
        "John", "Jane", "Michael", "Emma", "William", "Olivia", "James", "Sophia",
        "Robert", "Isabella", "David", "Mia", "Joseph", "Charlotte", "Thomas",
        "Amelia", "Charles", "Harper", "Daniel", "Evelyn"
    };
    
    private static final String[] LAST_NAMES = {
        "Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller",
        "Davis", "Rodriguez", "Martinez", "Hernandez", "Lopez", "Gonzalez",
        "Wilson", "Anderson", "Thomas", "Taylor", "Moore", "Jackson", "Martin"
    };

    public static List<Lot> generateLots(int count) {
        List<Lot> lots = new ArrayList<>();
        Random random = new Random();
        LocalDateTime baseTime = LocalDateTime.now().plusMinutes(2);

        for (int i = 0; i < count; i++) {
            String type = ITEM_TYPES[random.nextInt(ITEM_TYPES.length)];
            String category = ITEM_CATEGORIES[random.nextInt(ITEM_CATEGORIES.length)];
            String name = type + " " + category + " #" + (i + 1);
            
            int initialPrice = random.nextInt(100, 9900); // 100-10000
            int autoBuyPrice = initialPrice * random.nextInt(initialPrice, initialPrice * 2); // 1-1.5x initial price
            LocalDateTime expirationTime = baseTime.plusMinutes(random.nextInt(60)); // Random time within next hour
            
            lots.add(new Lot(name, initialPrice, autoBuyPrice, expirationTime));
        }
        
        return lots;
    }

    public static List<Buyer> generateBuyers(int count) {
        List<Buyer> buyers = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < count; i++) {
            String firstName = FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];
            String lastName = LAST_NAMES[random.nextInt(LAST_NAMES.length)];
            String name = firstName + " " + lastName;
            
            int budget = random.nextInt(1000, 99000); // 1000-100000
            
            buyers.add(new Buyer(name, budget));
        }
        
        return buyers;
    }
} 
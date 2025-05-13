import java.io.*;
import java.util.*;

public class AuctionSimulator {
    private static class Buyer {
        String name;
        int budget;
        public Buyer(String name, int budget) {
            this.name = name;
            this.budget = budget;
        }
    }

    private static class Product {
        String name;
        int price;
        public Product(String name, int price) {
            this.name = name;
            this.price = price;
        }
    }

    public static void main(String[] args) {
        List<Buyer> buyers = new ArrayList<>();
        List<Product> products = new ArrayList<>();

        try {
            BufferedReader buyerReader = new BufferedReader(new FileReader("buyers.txt"));
            String line;
            while ((line = buyerReader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    buyers.add(new Buyer(parts[0].trim(), Integer.parseInt(parts[1].trim())));
                }
            }
            buyerReader.close();

            BufferedReader productReader = new BufferedReader(new FileReader("products.txt"));
            while ((line = productReader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    products.add(new Product(parts[0].trim(), Integer.parseInt(parts[1].trim())));
                }
            }
            productReader.close();

            System.out.println("Loaded " + buyers.size() + " buyers and " + products.size() + " products.");

        } catch (IOException e) {
            System.out.println("Error reading files: " + e.getMessage());
        }
    }
} 
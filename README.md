# Multi-threaded Auction Simulator

This project implements a multi-threaded auction simulation system in Java, demonstrating various concurrent programming concepts and design patterns.

## Project Structure

The project is organized into the following packages:

- `com.auction.model`: Contains the core domain classes
  - `Lot`: Represents an auction item with properties like name, price, and state
  - `Buyer`: Represents a participant in the auction with a budget
  - `states`: Contains the state implementations for lots (UnderConsideration, Bought, Rejected)

- `com.auction.service`: Contains the business logic
  - `AuctionManager`: A thread-safe singleton that manages the auction process

- `com.auction.config`: Contains configuration-related classes
  - `ConfigurationReader`: Reads initialization data from files

## Key Features

1. **Thread Safety**
   - Uses `java.util.concurrent` and `java.util.concurrent.locks` for synchronization
   - Implements a thread-safe singleton pattern for the AuctionManager
   - Uses atomic operations for budget management
   - Employs read-write locks for concurrent access to shared resources

2. **State Pattern**
   - Implements the State pattern for lot states (UnderConsideration, Bought, Rejected)
   - Each state encapsulates its behavior and transitions

3. **Configuration**
   - Reads lot and buyer data from configuration files
   - Supports dynamic initialization of auction participants

4. **Bidding Process**
   - Implements a 10-second bidding window for each lot
   - Handles concurrent bids from multiple buyers
   - Manages buyer budgets and lot states
   - Implements blocking mechanism for buyers who fail to purchase

5. **Logging**
   - Uses Log4j2 for comprehensive logging
   - Tracks auction events, state changes, and errors

## Configuration Files

### lots.txt
Format: `name,initialPrice,autoBuyPrice,expirationTime`
Example:
```
Antique Vase,1000.0,1500.0,2024-03-20T15:30:00
Rare Painting,5000.0,7500.0,2024-03-20T15:30:00
Vintage Watch,2000.0,3000.0,2024-03-20T15:30:00
```

### buyers.txt
Format: `name,budget`
Example:
```
John Smith,10000.0
Alice Johnson,5000.0
Bob Wilson,8000.0
```

## Running the Application

1. Ensure you have Java 11 or later installed
2. Build the project using Maven:
   ```bash
   mvn clean install
   ```
3. Run the application:
   ```bash
   java -jar target/auction-simulator-1.0-SNAPSHOT.jar
   ```

## Design Decisions

1. **Thread Safety**
   - Used `ReentrantReadWriteLock` for the AuctionManager to allow concurrent reads
   - Implemented atomic operations for budget management to prevent race conditions
   - Used `ConcurrentHashMap` for tracking blocked lots

2. **State Management**
   - Implemented the State pattern to handle lot states
   - Each state encapsulates its behavior and transitions
   - States are immutable and thread-safe

3. **Concurrency**
   - Used `ExecutorService` for managing thread pools
   - Implemented `CountDownLatch` for synchronizing bidding periods
   - Used `ScheduledExecutorService` for timing operations

4. **Error Handling**
   - Comprehensive logging of all operations
   - Graceful handling of exceptions
   - Proper resource cleanup 
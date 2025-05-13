# Multi-threaded Auction Simulator

## Overview

The Multi-threaded Auction Simulator is a Java-based project that simulates an auction system using concurrent programming concepts. It demonstrates the use of multi-threading, synchronization, and design patterns to create a robust and efficient auction platform.

## Key Features

1. **Thread Safety**
   - Utilizes `java.util.concurrent` and `java.util.concurrent.locks` for synchronization.
   - Implements a thread-safe singleton pattern for the AuctionManager.
   - Uses atomic operations for budget management to prevent race conditions.

2. **State Pattern**
   - Implements the State pattern for lot states (UnderConsideration, Bought, Rejected).
   - Each state encapsulates its behavior and transitions, making the system flexible and maintainable.

3. **Configuration**
   - Reads lot and buyer data from configuration files.
   - Supports dynamic initialization of auction participants.

4. **Bidding Process**
   - Implements a 10-second bidding window for each lot.
   - Handles concurrent bids from multiple buyers.
   - Manages buyer budgets and lot states.
   - Implements a blocking mechanism for buyers who fail to purchase.

5. **Logging**
   - Uses Log4j2 for comprehensive logging.
   - Tracks auction events, state changes, and errors.

## Project Structure

The project is organized into the following packages:

- `com.auction.model`: Contains the core domain classes
  - `Lot`: Represents an auction item with properties like name, price, and state.
  - `Buyer`: Represents a participant in the auction with a budget.
  - `states`: Contains the state implementations for lots (UnderConsideration, Bought, Rejected).

- `com.auction.service`: Contains the business logic
  - `AuctionManager`: A thread-safe singleton that manages the auction process.

- `com.auction.config`: Contains configuration-related classes
  - `ConfigurationReader`: Reads initialization data from files.

## Running the Application

1. Ensure you have Java 11 or later installed.
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
   - Used `ReentrantReadWriteLock` for the AuctionManager to allow concurrent reads.
   - Implemented atomic operations for budget management to prevent race conditions.
   - Used `ConcurrentHashMap` for tracking blocked lots.

2. **State Management**
   - Implemented the State pattern to handle lot states.
   - Each state encapsulates its behavior and transitions.
   - States are immutable and thread-safe.

3. **Concurrency**
   - Used `ExecutorService` for managing thread pools.
   - Implemented `CountDownLatch` for synchronizing bidding periods.
   - Used `ScheduledExecutorService` for timing operations.

4. **Error Handling**
   - Comprehensive logging of all operations.
   - Graceful handling of exceptions.
   - Proper resource cleanup.

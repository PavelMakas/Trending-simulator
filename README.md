# Auction Simulator

A multi-threaded auction system that simulates concurrent bidding and purchasing of items by multiple buyers.

## System Architecture

### Core Components

#### 1. LotStorage (Singleton)
- Thread-safe singleton implementation using ReentrantReadWriteLock
- Manages blocked lots using ConcurrentSkipListSet
- Provides synchronized access to lot blocking operations
- Key methods:
  - `blockLot(String lotName)`: Blocks a lot for purchase
  - `isLotBlocked(String lotName)`: Checks if a lot is blocked
  - `unblockLot(String lotName)`: Removes lot from blocked list

#### 2. Buyer
- Represents an auction participant
- Manages individual budget and lot interactions
- Uses LotStorage for lot blocking operations
- Key methods:
  - `placeBid(Lot lot, int bidAmount)`: Attempts to place a bid
  - `purchaseLot(Lot lot, int amount)`: Attempts to purchase a lot
  - `hasFunds()`: Checks if buyer has remaining budget

#### 3. AuctionManager (Singleton)
- Central auction control system
- Manages lots and buyers
- Handles concurrent bidding process
- Uses multiple synchronization mechanisms:
  - ReentrantReadWriteLock for instance management
  - ReentrantLock for lot operations
  - ConcurrentHashMap for bid locks
- Key features:
  - Thread pool management for concurrent operations
  - Bid locking mechanism
  - Continuous bidding loop until lot is sold
  - Proper shutdown handling

### Thread Safety Mechanisms

1. **LotStorage**
   - Double-checked locking for singleton
   - ReadWriteLock for concurrent access
   - Thread-safe collection (ConcurrentSkipListSet)

2. **AuctionManager**
   - AtomicReference for singleton instance
   - ReadWriteLock for instance management
   - ReentrantLock for lot operations
   - ConcurrentHashMap for bid locks
   - Thread pool for concurrent operations

3. **Buyer**
   - Thread-safe lot storage access
   - Synchronized budget management
   - Atomic operations for fund checking

### Bidding Process

1. **Lot Processing**
   - Each lot is processed in a separate thread
   - Bids are collected concurrently
   - Continuous bidding until lot is sold or buyers run out of funds

2. **Bid Management**
   - Individual locks for each lot's bidding process
   - Price updates are synchronized
   - Owner updates are atomic

3. **Purchase Process**
   - Lot locking during purchase
   - Budget verification
   - State management (Bought/Rejected)

## Usage

1. Create buyers with initial budgets
2. Add lots to the auction
3. Start the auction process
4. Monitor the results

## Data Files

### buyers.txt
- Format: `name,budget`
- Contains buyer information and their budgets

### products.txt
- Format: `name,price`
- Contains product information and initial prices

## Thread Safety Features

- Concurrent bid processing
- Thread-safe collections
- Proper lock management
- Atomic operations
- Safe shutdown handling

## Error Handling

- Proper exception handling in all operations
- Graceful shutdown procedures
- Logging of important events and errors

## Performance Considerations

- Efficient thread pool usage
- Minimal lock contention
- Proper resource cleanup
- Optimized collection choices 
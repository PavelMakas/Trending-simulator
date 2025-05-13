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
- ------------------------------------------------------------------------
# Сімулятар аўкцыёну

Шматструнная сістэма аўкцыёну, якая імітуе адначасовыя таргі і куплю тавараў мноствам пакупнікоў.

## Архітэктура сістэмы

### Асноўныя кампаненты

#### 1. LotStorage (Сінглтан)
- Патокабяспечная рэалізацыя сінглтан з выкарыстаннем ReentrantReadWriteLock
- Кіруе заблакаванымі лотамі з дапамогай ConcurrentSkipListSet
- Забяспечвае сінхранізаваны доступ да аперацый блакіроўкі лотаў
- Асноўныя метады:
  - `blockLot(String lotName)`: Блакуе лот для пакупкі
  - `isLotBlocked(String lotName)`: Правярае, ці заблакаваны лот
  - `unblockLot(String lotName)`: Выдаляе лот са спісу заблакаваных

#### 2. Buyer (Пакупнік)
- Прадстаўляе ўдзельніка аўкцыёну
- Кіруе індывідуальным бюджэтам і ўзаемадзеяннем з лотамі
- Выкарыстоўвае LotStorage для аперацый блакіроўкі лотаў
- Асноўныя метады:
  - `placeBid(Lot lot, int bidAmount)`: Спрабуе зрабіць стаўку
  - `purchaseLot(Lot lot, int amount)`: Спрабуе купіць лот
  - `hasFunds()`: Правярае наяўнасць дастатковай колькасці сродкаў у пакупніка

#### 3. AuctionManager (Сінглтан)
- Цэнтральная сістэма кіравання аўкцыёнам
- Кіруе лотамі і пакупнікамі
- Апрацоўвае адначасовы працэс таргоў
- Выкарыстоўвае мноства механізмаў сінхранізацыі:
  - ReentrantReadWriteLock для кіравання экземплярам
  - ReentrantLock для аперацый з лотамі
  - ConcurrentHashMap для блакіровак ставак
- Асноўныя магчымасці:
  - Кіраванне пулам патокаў для адначасовых аперацый
  - Механізм блакіроўкі ставак
  - Бесперапынны цыкл таргоў да продажу лота
  - Правільная апрацоўка завяршэння працы

### Механізмы патокабяспекі

1. **LotStorage**
   - Двайная праверка блакіроўкі для сінглтан
   - ReadWriteLock для адначасовага доступу
   - Патокабяспечная калекцыя (ConcurrentSkipListSet)

2. **AuctionManager**
   - AtomicReference для экземпляра сінглтан
   - ReadWriteLock для кіравання экземплярам
   - ReentrantLock для аперацый з лотамі
   - ConcurrentHashMap для блакіровак ставак
   - Пул патокаў для адначасовых аперацый

3. **Buyer**
   - Патокабяспечны доступ да сховішча лотаў
   - Сінхранізаванае кіраванне бюджэтам
   - Атамныя аперацыі для праверкі сродкаў

### Працэс таргоў

1. **Апрацоўка лота**
   - Кожны лот апрацоўваецца ў асобным патоку
   - Стаўкі збіраюцца адначасова
   - Бесперапынныя таргі, пакуль лот не будзе прададзены або ў пакупнікоў не скончацца сродкі

2. **Кіраванне стаўкамі**
   - Індывідуальныя блакіроўкі для працэсу таргоў кожнага лота
   - Абнаўленні цаны сінхранізаваныя
   - Абнаўленні ўладальніка атамныя

3. **Працэс пакупкі**
   - Блакіроўка лота падчас пакупкі
   - Праверка бюджэту
   - Кіраванне станам (Куплены/Адхілены)

## Выкарыстанне

1. Стварыце пакупнікоў з пачатковымі бюджэтамі
2. Дадайце лоты на аўкцыён
3. Запусціце працэс аўкцыёну
4. Сачыце за вынікамі

## Файлы даных

### buyers.txt
- Фармат: `імя,бюджэт`
- Змяшчае інфармацыю пра пакупнікоў і іх бюджэты

### products.txt
- Фармат: `назва,цана`
- Змяшчае інфармацыю пра тавары і іх пачатковыя цэны

## Асаблівасці патокабяспекі

- Адначасовая апрацоўка ставак
- Патокабяспечныя калекцыі
- Правільнае кіраванне блакіроўкамі
- Атамныя аперацыі
- Бяспечная апрацоўка завяршэння працы

## Апрацоўка памылак

- Правільная апрацоўка выключэнняў ва ўсіх аперацыях
- Плыўныя працэдуры завяршэння працы
- Лагіраванне важных падзей і памылак

## Меркаванні аб прадукцыйнасці

- Эфектыўнае выкарыстанне пула патокаў
- Мінімальнае змаганне за блакіроўкі
- Правільная ачыстка рэсурсаў
- Аптымізаваны выбар калекцый

## Performance Considerations

- Efficient thread pool usage
- Minimal lock contention
- Proper resource cleanup
- Optimized collection choices

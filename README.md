# Auto-bid Auction-system

This application emulates an auto-bid auction system. It provides the ability to submit new items 
- for the sellers to list new items in the auction system
- for the buyers to submit bid offer for one or more items
- view current auction listings in the system

## Getting started

This project uses scala version 2.13 with sbt 1.4.3 and it has following dependencies.
- play framework
- typeSafe config
- scala test
- mockito

### Compilation

To compile the project, run the below command from the root directory, 
```sbt compile```

### Tests

To run unit tests, 
```sbt test```

### Run 

**How to Run the application**

To start the auction system,
```sbt run```

The application prompts for input with the following options:

1. Use json file as input to ingest auction items and bids in bulk. The ingestion rate and json file path can be configured [here](src/main/Resources/application.conf). 

2. Submit an item to sell in json format. This will add the auction item to the system and starts the count down timer to accept bids. 

   Example Json:
   ```
   {
    "id": "a8cfcb76-7f24-4420-a5ba-d46dd77bdfyu",
    "type": "newItem",
    "name": "Bicycle",
    "description": "Hot Wheels Child's Bicycle",
    "timeOfAuction": 100
   }
   ```

   **Note**: Press enter after typing in the json. And another time for the system to accept the input. (Press enter twice)

3. Submit a bid for an existing auction item. This option accepts bid offer with starting bid, auto-increment amount and max bid. Once the bid is added into the system, all the bidders in the losing position relative to the highest bid will be auto-incremented with the increment amount.

   Example Json: 
   ```
   {
    "id": "58e9b5fe-3fde-4a27-8e98-682e58a4a65d",
    "type": "bid",
    "name": "Alice",
    "itemName": "Bicycle",
    "item": "a8cfcb76-7f24-4420-a5ba-d46dd77bdffd", 
    "startingBid": 50,
    "maxBid": 80,
    "bidIncrement": 3
   }
   ```

   **Note**: Press enter after typing in the json. And another time for the system to accept the input. (Press enter twice)

4. Get current auction listings in the system. This option prints the current list of auction items in the system with the highest bid at the time. 

   Example Listing:
   ```
   Auction Listings:
   Item Id: 85ce0c8f-da34-4587-8fde-94a57ff75ae1    Item Name: Scooter   Description: Some(Classic Vespa Scooter)   Time Remaining: 0   Highest Bid: 700.0   Active:false
   Item Id: a8cfcb76-7f24-4420-a5ba-d46dd77bdffd    Item Name: Bicycle   Description: Some(Hot Wheels Child's Bicycle)   Time Remaining: 0   Highest Bid: 60.0   Active:false
   ```

5. Exit the program to stop running the program. 

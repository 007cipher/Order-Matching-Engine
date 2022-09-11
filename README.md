# Trading
This is an order matching engine for trading exchanges (Mainly for crypto trading exchanges). It contains
- Order-Matching-Engine
- Order-Matching-Engine-Test

## [Order-Matching-Engine](Order-Matching-Engine)
This is an order matching engine where [Order](Order-Matching-Engine/src/main/java/com/cipher/matching/engine/bean/Order.java) comes for execution

### Prerequisites
- Java 8 or above should be installed
- Redis should be running and address should be configured in [RedisConfig.java](Order-Matching-Engine/src/main/java/com/cipher/config/RedisConfig.java) (Currently redis is configured using single node only but it can be updated with cluster)

### Technologies Used
Java 8,
Redis

#### Redis
Redis is using the default address (127.0.0.1) and port (6379)

### Working
- To create the jar file use `mvn clean package`
- Then it can be used in any kind of project. Just need to add the jar file in build path (It can be added as a maven library)
- Create a new object of **OrderExecutionService** and call the method **submitOrder** with an [Order](Order-Matching-Engine/src/main/java/com/cipher/matching/engine/bean/Order.java) object.
- After some validation, it sends the order to **Matching Engine**
- Once order will be processed by the **Matching Engine** the **submitOrder** method will return an [OrderTradeDto](Order-Matching-Engine/src/main/java/com/cipher/matching/engine/dto/OrderTradeDto.java) with the processed order, matched orders and trades.
- If order not executed or partially executed then the order will be stored in redis until it gets cancelled or executed.

## [Order-Matching-Engine-Test](Order-Matching-Engine-Test)
This is an spring boot application to test and demonstrate that how we can use the matching engine.

### Prerequisites
- Java 8 or above should be installed

### Technologies Used
Java 8, Spring-Boot, H2 Database

#### Spring-Boot
Spring-Boot configuration is available in [application.properties](Order-Matching-Engine-Test/src/main/resources/application.properties) file.

#### H2 Database
H2 Database configuration is available in [application.properties](Order-Matching-Engine-Test/src/main/resources/application.properties) file.

### Working
- To create the jar file use `mvn clean package`
- Then it can be started using `java -jar target/Order-Matching-Engine-Test-1.0.0.jar`

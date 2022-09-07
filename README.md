# Order-Matching-Engine
This is an order matching engine for trading exchanges (Mainly for crypto trading exchanges).

## Prerequisites
- Java 8 or above should be installed
- Kafka should be running and configured in [KafkaConstants.java](src/main/java/com/cipher/constants/KafkaConstants.java)
- Redis should be running and address should be configured in [RedisConfig.java](src/main/java/com/cipher/config/RedisConfig.java) (Currently redis is configured using single node only but it can be updated with cluster)

## Technologies Used
Java 8,
Kafka,
Redis

#### Kafka
Configuration constants are defined in [KafkaConstants.java](src/main/java/com/cipher/constants/KafkaConstants.java)

#### Redis
Redis is using the default address (127.0.0.1) and port (6379)

## Working
- To create the jar file use `mvn clean package`
- Start the application using java -jar [Order-Matching-Engine-1.0.0.jar](target/Order-Matching-Engine-1.0.0.jar)
- Once application starts, It started consuming the order from topic **order**
- After some validation, it sends the order to **Matching Engine**
- If order executes completely in matching engine it publish the order, against order and the trade using kafka (topics can be found in [KafkaConstants.java](src/main/java/com/cipher/constants/KafkaConstants.java))
- If order not executed or partially executed that the order will be stored in redis until it gets cancelled or executed.
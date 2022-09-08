package com.cipher.constants;

public interface KafkaConstants {

    String BOOTSTRAP_SERVER = "127.0.0.1:9092";
    String GROUP_ID = "matchingEngine";

    /**
     * Kafka topics
     */
    String ORDER_PRODUCER = "order";
    String PROCESSED_ORDER_CONSUMER = "processedOrder";
    String TRADE_COMSUMER = "trade";
}

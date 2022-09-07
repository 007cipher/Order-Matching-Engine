package com.cipher.constants;

public class KafkaConstants {

    private KafkaConstants() {

    }

    public static final String BOOTSTRAP_SERVER = "127.0.0.1:9092";
    public static final String GROUP_ID = "matchingEngine";

    /**
     * Kafka topics
     */
    public static final String ORDER_CONSUMER = "order";
    public static final String PROCESSED_ORDER_PRODUCER = "processedOrder";
    public static final String TRADE_PRODUCER = "trade";
}

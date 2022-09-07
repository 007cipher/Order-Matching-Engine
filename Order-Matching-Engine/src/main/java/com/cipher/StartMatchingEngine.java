package com.cipher;

import com.cipher.consumer.OrderConsumer;

public class StartMatchingEngine {

    public static void main(String[] args) {
        OrderConsumer orderConsumer = new OrderConsumer();
        orderConsumer.consumeOrder();
    }
}

package com.cipher;

import com.cipher.consumer.OrderConsumer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StartMatchingEngine {

    public static void main(String[] args) {
        OrderConsumer orderConsumer = new OrderConsumer();
        orderConsumer.consumeOrder();
    }
}

package com.cipher.services;

import static com.cipher.constants.KafkaConstants.ORDER_PRODUCER;

import com.cipher.entities.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProduceOrder {

    @Autowired
    KafkaTemplate<String, Order> kafkaTemplate;

    public void sendOrderForExecution(Order savedOrder) {
        log.info("savedOrder: {}",savedOrder);
        kafkaTemplate.send(ORDER_PRODUCER, savedOrder);
    }
}

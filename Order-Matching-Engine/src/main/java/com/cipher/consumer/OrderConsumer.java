package com.cipher.consumer;

import com.cipher.bean.Order;
import com.cipher.bean.Trade;
import com.cipher.config.KafkaConfig;
import com.cipher.dto.OrderTradeDto;
import com.cipher.services.ExecutionService;
import com.cipher.services.OrderBookService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.Producer;

import java.time.Duration;

@Slf4j
public class OrderConsumer {

    public void consumeOrder() {
        OrderBookService orderBookService = OrderBookService.getInstance();
        Producer<String, Order> orderProducer = KafkaConfig.getOrderProducer();
        Producer<String, Trade> tradeProducer = KafkaConfig.getTradeProducer();
        ExecutionService executionService = new ExecutionService(orderBookService, orderProducer, tradeProducer);
        KafkaConsumer<String, Order> consumer = KafkaConfig.getConsumer();
        new Thread(() -> {
            while (true) {
                ConsumerRecords<String, Order> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, Order> record : records) {
                    try {
                        executionService.submitOrder(record.value());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}

package com.cipher.publisher;

import static com.cipher.constants.KafkaConstants.PROCESSED_ORDER_PRODUCER;

import com.cipher.bean.Order;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class OrderProducer implements DataProducer<Order> {

    private OrderProducer() {

    }

    @Override
    public void produce(Order order, Producer<String, Order> producer) {
        producer.send(new ProducerRecord<>(PROCESSED_ORDER_PRODUCER, null, order));
    }

    @Override
    public String getName() {
        return OrderProducer.class.getName();
    }

    public static DataProducer<Order> getInstance() {
        return OrderProducerHolder.ORDER_PRODUCER;
    }

    private static class OrderProducerHolder {
        static final OrderProducer ORDER_PRODUCER = new OrderProducer();
    }
}

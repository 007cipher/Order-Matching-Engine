package com.cipher.config;

import com.cipher.bean.Order;
import com.cipher.bean.Trade;
import com.cipher.serialization.OrderDeserializer;
import com.cipher.serialization.OrderSerializer;
import com.cipher.serialization.TradeSerializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Collections;
import java.util.Properties;

import static com.cipher.constants.KafkaConstants.BOOTSTRAP_SERVER;
import static com.cipher.constants.KafkaConstants.GROUP_ID;
import static com.cipher.constants.KafkaConstants.ORDER_CONSUMER;

public class KafkaConfig {
    public static KafkaConsumer<String, Order> getConsumer() {
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, OrderDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        KafkaConsumer<String, Order> consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Collections.singletonList(ORDER_CONSUMER));
        return consumer;
    }

    public static Producer<String, Order> getOrderProducer() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, OrderSerializer.class.getName());
        return new KafkaProducer<>(properties);
    }

    public static Producer<String, Trade> getTradeProducer() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, TradeSerializer.class.getName());
        return new KafkaProducer<>(properties);
    }
}

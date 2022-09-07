package com.cipher;

import com.cipher.bean.Order;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

public class TestOrderExecution {

    @Test
    public void submitOrders() {
        Gson gson = new Gson();
        JsonReader reader;
        try {
            ClassLoader classLoader = getClass().getClassLoader();

            URL resource = classLoader.getResource("orders.json");
            if (resource == null) {
                throw new IllegalArgumentException("file not found! orders.json");
            }
            File file = new File(resource.toURI());
            reader = new JsonReader(new FileReader(file));
        } catch (FileNotFoundException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
        Order[] data = gson.fromJson(reader, Order[].class);
        KafkaProducer<String, Order> producer = producer();
        long start = System.currentTimeMillis();
        System.out.println(start);
        for (int i = 0; i < data.length; i++) {
            Order order = data[i];
            order.setId((long) (i + 1));
            producer.send(new ProducerRecord<>("order", null, order));
        }
        long end = System.currentTimeMillis();
        System.out.println(end);
        System.out.println(end - start);
    }

    private KafkaProducer<String, Order> producer() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        properties.put(ProducerConfig.RETRIES_CONFIG, 0);
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        properties.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, TestCustomSerializer.class.getName());
        return new KafkaProducer<>(properties);
    }
}

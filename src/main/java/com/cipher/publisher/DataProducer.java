package com.cipher.publisher;

import com.cipher.bean.Order;
import org.apache.kafka.clients.producer.Producer;

public interface DataProducer<T> {

    void produce(T t, Producer<String, T> producer);

    String getName();
}

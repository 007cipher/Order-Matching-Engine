package com.cipher.serialization;

import com.cipher.bean.Order;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

@Slf4j
public class OrderSerializer implements Serializer<Order> {

    private final Gson gson = new Gson();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public byte[] serialize(String topic, Order data) {
        try {
            if (data == null) {
                log.error("Null received at serializing");
                return null;
            }
            return gson.toJson(data).getBytes();
        } catch (Exception e) {
            throw new SerializationException("Error when serializing MessageDto to byte[]");
        }
    }

    @Override
    public void close() {
    }
}
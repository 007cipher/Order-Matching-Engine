package com.cipher.config;

import com.cipher.bean.Order;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
public class OrderDeserializer implements Deserializer<Order> {
    private final Gson gson = new Gson();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public Order deserialize(String topic, byte[] data) {
        try {
            if (data == null) {
                log.error("Null received at deserializing");
                return null;
            }
            String s = new String(data, StandardCharsets.UTF_8);
            return gson.fromJson(s, Order.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void close() {
    }
}

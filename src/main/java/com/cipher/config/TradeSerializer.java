package com.cipher.config;

import com.cipher.bean.Trade;
import com.cipher.dto.OrderTradeDto;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

@Slf4j
public class TradeSerializer implements Serializer<Trade> {
    private final Gson gson = new Gson();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public byte[] serialize(String topic, Trade data) {
        try {
            if (data == null) {
                System.out.println("Null received at serializing");
                return null;
            }
            System.out.println("Serializing...");
            return gson.toJson(data).getBytes();
        } catch (Exception e) {
            throw new SerializationException("Error when serializing MessageDto to byte[]");
        }
    }

    @Override
    public void close() {
    }
}
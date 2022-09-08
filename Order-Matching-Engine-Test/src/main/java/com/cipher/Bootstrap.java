package com.cipher;

import com.cipher.entities.Order;
import com.cipher.enums.OrderStatus;
import com.cipher.enums.Side;
import com.cipher.services.OrderService;
import com.cipher.services.ProduceOrder;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
public class Bootstrap implements ApplicationListener<ApplicationEvent> {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProduceOrder produceOrder;

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationStartedEvent) {
            ExecutorService executorService = Executors.newFixedThreadPool(1000);
            Gson gson = new Gson();
            JsonReader reader;
            try {
                reader = new JsonReader(new FileReader(ResourceUtils.getFile("classpath:orders.json")));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            Order[] data = gson.fromJson(reader, Order[].class);
            long startTime = System.currentTimeMillis();
            Arrays.stream(data).forEach(order -> {
                Order saveOrder = orderService.saveOrder(order);
                CompletableFuture.runAsync(() -> produceOrder.sendOrderForExecution(saveOrder), executorService);
            });
            log.info("Time taken: {}", System.currentTimeMillis() - startTime);
        }
    }
}

package com.cipher;

import com.cipher.entities.Order;
import com.cipher.handler.CommandHandler;
import com.cipher.matching.engine.CommandExecutor;
import com.cipher.matching.engine.dto.OrderTradeDto;
import com.cipher.handler.AfterExecutionHandler;
import com.cipher.services.OrderService;
import com.cipher.services.TradeService;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
    private CommandHandler commandHandler;

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationStartedEvent) {
            ExecutorService executorService = Executors.newFixedThreadPool(100);
            Gson gson = new Gson();
            String reader = "";
            ClassPathResource cpr = new ClassPathResource("orders.json");
            try {
                byte[] bdata = FileCopyUtils.copyToByteArray(cpr.getInputStream());
                reader = new String(bdata, StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Order[] data = gson.fromJson(reader, Order[].class);
            long startTime = System.currentTimeMillis();
            Arrays.stream(data).forEach(order -> {
                Order saveOrder = orderService.saveOrder(order);
                CompletableFuture.runAsync(() -> {
                    commandHandler.handlePlaceOrderCommand(saveOrder);
                }, executorService);
            });
            log.info("Time taken: {}", System.currentTimeMillis() - startTime);
        }
    }
}

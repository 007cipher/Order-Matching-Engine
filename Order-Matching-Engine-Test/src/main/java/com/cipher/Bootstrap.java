package com.cipher;

import com.cipher.entities.Order;
import com.cipher.matching.engine.dto.OrderTradeDto;
import com.cipher.matching.engine.services.OrderExecutionService;
import com.cipher.services.AfterExecutionHandler;
import com.cipher.services.OrderService;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
public class Bootstrap implements ApplicationListener<ApplicationEvent> {

    @Autowired
    private OrderService orderService;

    @Autowired
    private AfterExecutionHandler afterExecutionHandler;

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationStartedEvent) {
            ExecutorService executorService = Executors.newFixedThreadPool(100);
            Gson gson = new Gson();
            JsonReader reader;
            try {
                reader = new JsonReader(new FileReader(ResourceUtils.getFile("classpath:orders.json")));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            Order[] data = gson.fromJson(reader, Order[].class);
            long startTime = System.currentTimeMillis();
            OrderExecutionService executionService = new OrderExecutionService();
            for (int i = 0; i < data.length; i++) {
                Order saveOrder = orderService.saveOrder(data[i]);
                CompletableFuture.runAsync(() -> {
                    com.cipher.matching.engine.bean.Order executableOrder = createExecutableOrder(saveOrder);
                    log.info("Executable Order: {}", executableOrder);
                    OrderTradeDto orderTradeDto = executionService.submitOrder(executableOrder);
                    afterExecutionHandler.orderTradeDtoHandler(orderTradeDto);
                }, executorService);
            }
//            Arrays.stream(data).forEach(order -> {
//
//            });
            log.info("Time taken: {}", System.currentTimeMillis() - startTime);
        }
    }

    com.cipher.matching.engine.bean.Order createExecutableOrder(Order order) {
        return new com.cipher.matching.engine.bean.Order(order.getId(), order.getInstrument(), order.getQty(), order.getRemQty(), order.getPrice(), order.getSide(), order.getStatus());
    }
}

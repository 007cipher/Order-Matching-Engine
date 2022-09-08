package com.cipher.services;

import com.cipher.entities.Order;
import com.cipher.entities.Trade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.cipher.constants.KafkaConstants.*;

@Slf4j
@Component
public class DataConsumer {

    @Autowired
    private OrderService orderService;

    @Autowired
    private TradeService tradeService;

    @KafkaListener(topics = PROCESSED_ORDER_CONSUMER, groupId = GROUP_ID, containerFactory = "orderKafkaListenerContainerFactory")
    public void consumeProcessedOrder(Order processedOrder) {
        log.info("processedOrder: {}",processedOrder);
        orderService.saveOrder(processedOrder);
    }

    @KafkaListener(topics = TRADE_COMSUMER, groupId = GROUP_ID, containerFactory = "tradeKafkaListenerContainerFactory")
    public void consumeTrade(Trade trade) {
        log.info("trade: {}", trade);
        tradeService.save(trade);
    }
}

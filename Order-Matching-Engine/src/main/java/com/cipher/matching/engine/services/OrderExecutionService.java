package com.cipher.matching.engine.services;

import com.cipher.matching.engine.bean.Order;
import com.cipher.matching.engine.dto.OrderTradeDto;
import com.cipher.matching.engine.validator.OrderValidator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OrderExecutionService {

    private final OrderBookService orderBookService;

    public OrderExecutionService() {
        this.orderBookService = OrderBookService.getInstance();
    }

    public OrderTradeDto submitOrder(Order order) {
        if (!OrderValidator.validateOrder(order)) {
            log.error("Invalid order: {}", order);
            throw new RuntimeException("Invalid order submitted!");
        }
        log.debug("order: {}", order);
        return orderBookService.processOrder(order);
    }
}

package com.cipher.matching.engine;

import com.cipher.matching.engine.bean.Order;
import com.cipher.matching.engine.bean.OrderBook;
import com.cipher.matching.engine.dto.OrderTradeDto;
import com.cipher.matching.engine.enums.Side;
import com.cipher.matching.engine.holders.InstrumentHolder;
import com.cipher.matching.engine.holders.OrderBookHolder;
import com.cipher.matching.engine.holders.OrderHolder;
import com.cipher.matching.engine.validator.OrderValidator;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Slf4j
public class CommandExecutor {

    private final OrderBookHolder orderBookHolder;

    private final OrderHolder ORDER_HOLDER = new OrderHolder();

    public CommandExecutor() {
        this.orderBookHolder = OrderBookHolder.getInstance();
    }

    public OrderTradeDto processOrder(Order order) {
        if (!OrderValidator.validateOrder(order)) {
            log.error("Invalid order: {}", order);
            throw new RuntimeException("Invalid order submitted!");
        }
        log.debug("order: {}", order);
        return orderBookHolder.processOrder(order);
    }

    public Order cancelOrder(Order order) {
        if (!OrderValidator.validateOrder(order)) {
            log.error("Invalid order: {}", order);
            throw new RuntimeException("Invalid order submitted!");
        }
        log.debug("order to cancel: {}", order);
        return orderBookHolder.cancelOrder(order);
    }

    public Collection<Order> listOrders(List<String> instruments) {
        return ORDER_HOLDER.findAllByInstruments(instruments);
    }

    public Map<Side, Map<BigDecimal, OrderBook>> fetchOrderBook(String instrument) {
        if (!InstrumentHolder.validateInstrument(instrument))
            throw new RuntimeException("Invalid instrument: " + instrument);
        return orderBookHolder.getOrderBook(instrument);
    }
}

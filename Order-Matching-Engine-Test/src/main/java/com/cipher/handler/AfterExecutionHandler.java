package com.cipher.handler;

import com.cipher.converter.Converter;
import com.cipher.entities.Order;
import com.cipher.entities.Trade;
import com.cipher.matching.engine.dto.OrderTradeDto;
import com.cipher.services.OrderService;
import com.cipher.services.TradeService;

import java.util.List;

public class AfterExecutionHandler {

    private OrderService orderService;

    private TradeService tradeService;

    public AfterExecutionHandler(OrderService orderService, TradeService tradeService) {
        this.orderService = orderService;
        this.tradeService = tradeService;
    }

    public void orderTradeDtoHandler(OrderTradeDto orderTradeDto) {
        processedOrderHandler(orderTradeDto.getOrder());
        matchedOrderHandler(orderTradeDto.getMatchedOrder());
        tradeHandler(orderTradeDto.getTrade());
    }

    private void processedOrderHandler(com.cipher.matching.engine.bean.Order processedOrder) {
        Order order = Converter.convert(processedOrder, Order.class);
        orderService.saveOrder(order);
    }

    private void matchedOrderHandler(List<com.cipher.matching.engine.bean.Order> orderList) {
        orderList.forEach(matchedOrder -> {
            Order order = Converter.convert(matchedOrder, Order.class);
            orderService.saveOrder(order);
        });
    }

    private void tradeHandler(List<com.cipher.matching.engine.bean.Trade> tradeList) {
        tradeList.forEach(t -> {
            Trade trade = Converter.convert(t, Trade.class);
            tradeService.save(trade);
        });
    }
}

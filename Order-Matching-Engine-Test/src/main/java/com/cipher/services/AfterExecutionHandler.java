package com.cipher.services;

import com.cipher.matching.engine.bean.Order;
import com.cipher.matching.engine.bean.Trade;
import com.cipher.matching.engine.dto.OrderTradeDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AfterExecutionHandler {

    @Autowired
    private OrderService orderService;

    @Autowired
    private TradeService tradeService;

    public void orderTradeDtoHandler(OrderTradeDto orderTradeDto) {
        processedOrderHandler(orderTradeDto.getOrder());
        matchedOrderHandler(orderTradeDto.getMatchedOrder());
        tradeHandler(orderTradeDto.getTrade());
    }

    private void processedOrderHandler(Order processedOrder) {
        com.cipher.entities.Order order = convertOrder(processedOrder);
        BeanUtils.copyProperties(processedOrder, order);
        orderService.saveOrder(order);
    }

    private void matchedOrderHandler(List<Order> orderList) {
        for (Order matchedOrder : orderList) {
            com.cipher.entities.Order order = convertOrder(matchedOrder);
            orderService.saveOrder(order);
        }
    }

    private void tradeHandler(List<Trade> tradeList) {
        for (Trade t : tradeList) {
            com.cipher.entities.Trade trade = convertTrade(t);
            tradeService.save(trade);
        }
    }

    private com.cipher.entities.Order convertOrder(Order order) {
        return new com.cipher.entities.Order(order.getId(), order.getInstrument(), order.getQty(), order.getRemQty(), order.getPrice(), order.getSide(), order.getStatus());
    }

    private com.cipher.entities.Trade convertTrade(Trade trade) {
        return new com.cipher.entities.Trade(trade.getId(), trade.getOrderId(), trade.getAgainstOrderId(), trade.getInstrument(), trade.getQty(), trade.getPrice());
    }
}

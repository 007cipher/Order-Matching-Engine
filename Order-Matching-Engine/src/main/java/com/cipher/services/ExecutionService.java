package com.cipher.services;

import com.cipher.bean.Trade;
import com.cipher.factory.DataProducerFactory;
import com.cipher.bean.Order;
import com.cipher.dto.OrderTradeDto;
import com.cipher.publisher.DataProducer;
import com.cipher.publisher.OrderProducer;
import com.cipher.publisher.TradeProducer;
import com.cipher.validator.OrderValidator;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;

@Slf4j
public class ExecutionService {

    private final OrderBookService orderBookService;

    private final Producer<String, Order> orderProducer;

    private final Producer<String, Trade> tradeProducer;

    public ExecutionService(OrderBookService orderBookService, Producer<String, Order> orderProducer, Producer<String, Trade> tradeProducer) {
        this.orderBookService = orderBookService;
        this.orderProducer = orderProducer;
        this.tradeProducer = tradeProducer;
    }

    public void submitOrder(Order order) {
        if (!OrderValidator.validateOrder(order)) {
            log.error("Invalid order: " + order);
            return;
        }
        OrderTradeDto orderTradeDto = orderBookService.processOrder(order);
        publishData(orderTradeDto);
    }

    private void publishData(OrderTradeDto orderTradeDto) {
        try {
            publishOrder(orderTradeDto);
            publishMatchedOrder(orderTradeDto);
            publishTrade(orderTradeDto);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void publishOrder(OrderTradeDto orderTradeDto) throws ClassNotFoundException {
        if (orderTradeDto.getOrder() != null) {
            DataProducer<Order> dataProducer = (DataProducer<Order>) DataProducerFactory.get(OrderProducer.class.getName());
            dataProducer.produce(orderTradeDto.getOrder(), orderProducer);
        }
    }

    private void publishMatchedOrder(OrderTradeDto orderTradeDto) throws ClassNotFoundException {
        if (orderTradeDto.getMatchedOrder() != null) {
            DataProducer<Order> dataProducer = (DataProducer<Order>) DataProducerFactory.get(OrderProducer.class.getName());
            orderTradeDto.getMatchedOrder().forEach(matchedOrder -> dataProducer.produce(matchedOrder, orderProducer));
        }
    }

    private void publishTrade(OrderTradeDto orderTradeDto) throws ClassNotFoundException {
        if (orderTradeDto.getTrade() != null) {
            DataProducer<Trade> dataProducer = (DataProducer<Trade>) DataProducerFactory.get(TradeProducer.class.getName());
            orderTradeDto.getTrade().forEach(trade -> dataProducer.produce(trade, tradeProducer));
        }
    }
}

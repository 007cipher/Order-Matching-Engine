package com.cipher.handler;

import com.cipher.converter.Converter;
import com.cipher.entities.Order;
import com.cipher.generator.OrderIdGenerator;
import com.cipher.generator.OrderIdGeneratorStrategy;
import com.cipher.matching.engine.CommandExecutor;
import com.cipher.matching.engine.dto.OrderTradeDto;
import com.cipher.matching.engine.enums.OrderStatus;
import com.cipher.services.OrderService;
import com.cipher.services.TradeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
public class CommandHandler {

    @Autowired
    private OrderService orderService;

    @Autowired
    private TradeService tradeService;

    private AfterExecutionHandler afterExecutionHandler;

    private CommandExecutor commandExecutionService = new CommandExecutor();

    @PostConstruct
    public void init() {
        afterExecutionHandler = new AfterExecutionHandler(orderService, tradeService);
    }

    public String handlePlaceOrderCommand(Order order) {
        Order savedOrder = orderService.saveOrder(order);
        com.cipher.matching.engine.bean.Order executableOrder = Converter.convert(savedOrder, com.cipher.matching.engine.bean.Order.class);
        log.info("Executable Order: {}", executableOrder);
        OrderTradeDto orderTradeDto = commandExecutionService.processOrder(executableOrder);
        afterExecutionHandler.orderTradeDtoHandler(orderTradeDto);
        if (orderTradeDto.getOrder().getStatus().equals(OrderStatus.EXECUTED) || orderTradeDto.getOrder().getStatus().equals(OrderStatus.PARTIALLY_EXECUTED)) {
            return "Order executed!";
        }
        return "Order placed successfully!";
    }

    public String handleCancelOrderCommand(Order order) {
        com.cipher.matching.engine.bean.Order cancelledOrder = commandExecutionService.cancelOrder(Converter.convert(order, com.cipher.matching.engine.bean.Order.class));
        log.debug("cancelledOrder: {}", cancelledOrder);
        if (cancelledOrder.getStatus().equals(OrderStatus.CANCELLED)) {
            orderService.saveOrder(Converter.convert(cancelledOrder, Order.class));
            return "Order Cancelled!";
        }
        return "There is some issue in cancellation!";
    }

    public List<Order> handleListOrderCommand(String instrument) {
        Collection<com.cipher.matching.engine.bean.Order> listOrders = commandExecutionService.listOrders(Arrays.asList(instrument));
        List<Order> orders = orderService.findOpenOrdersByInstrument(instrument);
        if (listOrders.size() == orders.size())
            return orders;
        log.info("There is some mismatch in database orders and order engine orders! database count is {}, matching engine count {}",
                orders.size(), listOrders.size());
        if (listOrders.size() > orders.size()) {
            listOrders.forEach(engineOrder -> {
                Long engineOrderId = engineOrder.getId();
                AtomicBoolean matched = new AtomicBoolean(false);
                orders.forEach(order -> {
                    if (engineOrderId.equals(order.getId())) {
                        matched.set(true);
                    }
                });
                if (!matched.get())
                    log.info("Order not exist in database or executed: {}", engineOrder);
            });
        } else {
            orders.forEach(order -> {
                Long orderId = order.getId();
                AtomicBoolean matched = new AtomicBoolean(false);
                listOrders.forEach(engineOrder -> {
                    if (orderId.equals(engineOrder.getId())) {
                        matched.set(true);
                    }
                });
                if (!matched.get())
                    log.info("Order not exist in engine or executed: {}", order);
            });
        }
        return orders;
    }

}

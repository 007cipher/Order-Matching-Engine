package com.cipher.matching.engine.engine;

import com.cipher.matching.engine.bean.Order;
import com.cipher.matching.engine.bean.OrderBook;
import com.cipher.matching.engine.bean.Trade;
import com.cipher.matching.engine.dto.OrderTradeDto;
import com.cipher.matching.engine.enums.OrderStatus;
import com.cipher.matching.engine.enums.Side;
import com.cipher.matching.engine.services.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
public class MatchingEngine {

    private static final OrderService orderService = new OrderService();

    private MatchingEngine() {

    }

    public static OrderTradeDto match(Order order, RMap<String, Map<Side, Map<BigDecimal, OrderBook>>> orderBooks) {
        OrderTradeDto orderTradeDto = new OrderTradeDto(order, new ArrayList<>(), new ArrayList<>());
        Map<Side, Map<BigDecimal, OrderBook>> orderBook = orderBooks.get(order.getInstrument());
        Map<BigDecimal, OrderBook> buySideOrderBook = orderBook.get(Side.BUY);
        Map<BigDecimal, OrderBook> sellSideOrderBook = orderBook.get(Side.SELL);
        while (order.getRemQty().compareTo(BigDecimal.ZERO) > 0) {
            if (Side.BUY.equals(order.getSide())) {
                Optional<BigDecimal> firstIndex = sellSideOrderBook.keySet().stream().findFirst();
                if (firstIndex.isPresent()) {
                    BigDecimal price = firstIndex.get();
                    if (price.compareTo(order.getPrice()) <= 0) {
                        executeOrder(order, sellSideOrderBook, price, orderTradeDto);
                    } else {
                        addToOrderBook(order, order.getPrice(), buySideOrderBook);
                        break;
                    }
                } else {
                    addToOrderBook(order, order.getPrice(), buySideOrderBook);
                    break;
                }
            } else {
                Optional<BigDecimal> firstIndex = buySideOrderBook.keySet().stream().findFirst();
                if (firstIndex.isPresent()) {
                    BigDecimal price = firstIndex.get();
                    if (price.compareTo(order.getPrice()) >= 0) {
                        executeOrder(order, buySideOrderBook, price, orderTradeDto);
                    } else {
                        addToOrderBook(order, order.getPrice(), sellSideOrderBook);
                        break;
                    }
                } else {
                    addToOrderBook(order, order.getPrice(), sellSideOrderBook);
                    break;
                }
            }
        }
        orderBooks.put(order.getInstrument(), orderBook);
        return orderTradeDto;
    }

    private static void executeOrder(Order order, Map<BigDecimal, OrderBook> oneSideOrderBook,
                                     BigDecimal firstIndexPrice, OrderTradeDto orderTradeDto) {
        BigDecimal executedQty;
        OrderBook orderBook = oneSideOrderBook.remove(firstIndexPrice);
        if (orderBook.getQty().compareTo(order.getRemQty()) == 0) {
            executedQty = order.getRemQty();
            order.setRemQty(BigDecimal.ZERO);
            order.setStatus(OrderStatus.EXECUTED);
            updateOrder(order, orderBook, executedQty, orderTradeDto);
        } else if (orderBook.getQty().compareTo(order.getRemQty()) > 0) {
            executedQty = order.getRemQty();
            orderBook.setQty(orderBook.getQty().subtract(executedQty));
            order.setRemQty(BigDecimal.ZERO);
            order.setStatus(OrderStatus.EXECUTED);
            oneSideOrderBook.put(firstIndexPrice, orderBook);
            updateOrder(order, orderBook, executedQty, orderTradeDto);
        } else {
            executedQty = orderBook.getQty();
            order.setRemQty(order.getRemQty().subtract(executedQty));
            order.setStatus(OrderStatus.PARTIALLY_EXECUTED);
            updateOrder(order, orderBook, executedQty, orderTradeDto);
        }
    }

    private static void updateOrder(Order order, OrderBook orderBook, BigDecimal executedQty, OrderTradeDto orderTradeDto) {
        Collection<Order> orders = orderService.getOrders(orderBook.getInstrument(), orderBook.getPrice(),
                orderBook.getSide(), Arrays.asList(OrderStatus.OPEN, OrderStatus.PARTIALLY_EXECUTED));
        orders.forEach(savedOrder -> {
            Order updatedOrder;
            if (executedQty.compareTo(savedOrder.getRemQty()) >= 0) {
                updatedOrder = orderService.deleteOrder(savedOrder);
                updatedOrder.setRemQty(updatedOrder.getRemQty().subtract(executedQty));
                updatedOrder.setStatus(OrderStatus.EXECUTED);
            } else {
                updatedOrder = orderService.detachOrder(savedOrder);
                updatedOrder.setRemQty(savedOrder.getRemQty().subtract(executedQty));
                updatedOrder.setStatus(OrderStatus.PARTIALLY_EXECUTED);
                orderService.saveOrder(updatedOrder);
            }
            orderTradeDto.setOrder(order);
            orderTradeDto.getMatchedOrder().add(updatedOrder);
            Trade trade = createTrade(order, updatedOrder, executedQty);
            orderTradeDto.getTrade().add(trade);
        });
    }

    private static void addToOrderBook(Order order, BigDecimal price, Map<BigDecimal, OrderBook> oneSideOrderBook) {
        OrderBook orderBook;
        if (oneSideOrderBook.containsKey(price)) {
            orderBook = oneSideOrderBook.get(price);
            orderBook.setQty(orderBook.getQty().add(order.getRemQty()));
        } else {
            orderBook = new OrderBook(order.getInstrument(), order.getRemQty(), price, order.getSide());
        }
        oneSideOrderBook.put(price, orderBook);
        orderService.saveOrder(order);
    }

    private static void deleteFromOrderBook(Order order, BigDecimal qty, Map<BigDecimal, OrderBook> oneSideOrderBook) {
        OrderBook orderBook;
        BigDecimal price = order.getPrice();
        if (oneSideOrderBook.containsKey(price)) {
            orderBook = oneSideOrderBook.get(price);
            if (orderBook.getQty().compareTo(qty) > 0)
                orderBook.setQty(orderBook.getQty().subtract(qty));
            else
                oneSideOrderBook.remove(price);
        }
    }

    private static Trade createTrade(Order order, Order matchedOrder, BigDecimal qty) {
        return new Trade(null, order.getId(), matchedOrder.getId(), order.getInstrument(), qty,
                matchedOrder.getPrice());
    }
}

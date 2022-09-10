package com.cipher;

import com.cipher.matching.engine.bean.Order;
import com.cipher.matching.engine.bean.OrderBook;
import com.cipher.matching.engine.enums.Side;
import com.cipher.matching.engine.services.OrderBookService;
import com.cipher.matching.engine.services.OrderService;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public class TestOrderExecution {

    @Test
    public void submitOrders() {
        OrderBookService orderBookService = OrderBookService.getInstance();
        Map<Side, Map<BigDecimal, OrderBook>> orderBookMap = orderBookService.getOrderBook("XRPETH");
        Map<BigDecimal, OrderBook> buyBook = orderBookMap.get(Side.BUY);
        System.out.println(buyBook);
        buyBook.values().forEach(o -> System.out.print("qty: " + o.getQty() + " price: " + o.getPrice() + ","));
        System.out.println();
        Map<BigDecimal, OrderBook> sellBook = orderBookMap.get(Side.SELL);
        sellBook.values().forEach(o -> System.out.print("qty: " + o.getQty() + " price: " + o.getPrice() + ","));
        System.out.println();
        OrderService orderService = new OrderService();
        Collection<Order> col = orderService.findAllByInstruments(Arrays.asList("XRPETH"));
        col.forEach(o -> System.out.print("qty: " + o.getRemQty()+" type: " +o.getSide() + " price: " + o.getPrice() + ","));
    }
}

package com.cipher;

import com.cipher.matching.engine.bean.Order;
import com.cipher.matching.engine.bean.OrderBook;
import com.cipher.matching.engine.enums.Side;
import com.cipher.matching.engine.holders.OrderBookHolder;
import com.cipher.matching.engine.holders.OrderHolder;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public class TestOrderExecution {

    @Test
    public void submitOrders() {
        OrderBookHolder orderBookHolder = OrderBookHolder.getInstance();
        Map<Side, Map<BigDecimal, OrderBook>> orderBookMap = orderBookHolder.getOrderBook("XRPETH");
        Map<BigDecimal, OrderBook> buyBook = orderBookMap.get(Side.BUY);
        System.out.println(buyBook);
        buyBook.values().forEach(o -> System.out.print("qty: " + o.getQty() + " price: " + o.getPrice() + ","));
        System.out.println();
        Map<BigDecimal, OrderBook> sellBook = orderBookMap.get(Side.SELL);
        sellBook.values().forEach(o -> System.out.print("qty: " + o.getQty() + " price: " + o.getPrice() + ","));
        System.out.println();
        OrderHolder orderHolder = new OrderHolder();
        Collection<Order> col = orderHolder.findAllByInstruments(Arrays.asList("XRPETH"));
        col.forEach(o -> System.out.print("qty: " + o.getRemQty()+" type: " +o.getSide() + " price: " + o.getPrice() + ","));
    }
}

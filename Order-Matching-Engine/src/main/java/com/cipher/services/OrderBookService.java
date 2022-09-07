package com.cipher.services;

import com.cipher.bean.Order;
import com.cipher.bean.OrderBook;
import com.cipher.config.RedisConfig;
import com.cipher.dto.OrderTradeDto;
import com.cipher.engine.MatchingEngine;
import com.cipher.enums.Side;
import com.cipher.exception.IllegalAccessException;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Slf4j
public final class OrderBookService {

    private static final RedissonClient redissonClient = RedisConfig.getRedissonClient();

    {
        InstrumentService instrumentService = new InstrumentService();
        List<String> instruments = instrumentService.getInstruments();
        instruments.parallelStream().forEach(instrument -> CompletableFuture.runAsync(() -> initializeOrderBookWithInstruments(instrument)));
        log.info("Instrument initialization done");
    }

    private OrderBookService() throws IllegalAccessException {
        if (OrderBookServiceHolder.orderBookService != null)
            throw new IllegalAccessException("Object already available!");
    }

    public static OrderBookService getInstance() throws IllegalAccessException {
        return OrderBookServiceHolder.orderBookService;
    }

    private static void initializeOrderBookWithInstruments(String instrument) {
        RMap<Side, Map<BigDecimal, OrderBook>> orderBooks = redissonClient.getMap(instrument);
        if (!orderBooks.containsKey(Side.BUY)) {
            Map<BigDecimal, OrderBook> buyOrderBook = new TreeMap<>(Comparator.reverseOrder());
            orderBooks.put(Side.BUY, buyOrderBook);
        }
        if (!orderBooks.containsKey(Side.SELL)) {
            Map<BigDecimal, OrderBook> sellOrderBook = new TreeMap<>();
            orderBooks.put(Side.SELL, sellOrderBook);
        }
    }

    public OrderTradeDto processOrder(Order order) {
        RMap<Side, Map<BigDecimal, OrderBook>> orderBooks = redissonClient.getMap(order.getInstrument());
        return MatchingEngine.match(order, orderBooks);
    }

    public List<List<OrderBook>> getOrderBook(String instrument) {
        RMap<Side, Map<BigDecimal, OrderBook>> orderBooks = redissonClient.getMap(instrument);
        List<OrderBook> buyBook = new ArrayList<>(orderBooks.get(Side.BUY).values());
        List<OrderBook> sellBook = new ArrayList<>(orderBooks.get(Side.SELL).values());
        return Arrays.asList(buyBook, sellBook);
    }

    private static final class OrderBookServiceHolder {
        static final OrderBookService orderBookService = new OrderBookService();
    }
}

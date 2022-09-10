package com.cipher.matching.engine.services;

import com.cipher.matching.engine.bean.Order;
import com.cipher.matching.engine.bean.OrderBook;
import com.cipher.matching.engine.config.RedisConfig;
import com.cipher.matching.engine.dto.OrderTradeDto;
import com.cipher.matching.engine.engine.MatchingEngine;
import com.cipher.matching.engine.enums.Side;
import com.cipher.matching.engine.exception.IllegalAccessException;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
public final class OrderBookService {

    private static final RedissonClient redissonClient = RedisConfig.getRedissonClient();

    private static final String ORDER_BOOK = "orderBook";

    {
        InstrumentService instrumentService = new InstrumentService();
        List<String> instruments = instrumentService.getInstruments();
        instruments.forEach(instrument -> initializeOrderBookWithInstruments(instrument));
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
        RMap<String, Map<Side, Map<BigDecimal, OrderBook>>> orderBooks = redissonClient.getMap(ORDER_BOOK);
        if (!orderBooks.containsKey(instrument)) {
            Map<BigDecimal, OrderBook> buyOrderBook = new TreeMap<>(Comparator.reverseOrder());
            Map<BigDecimal, OrderBook> sellOrderBook = new TreeMap<>();
            Map<Side, Map<BigDecimal, OrderBook>> orderBook = new HashMap<>();
            orderBook.put(Side.BUY, buyOrderBook);
            orderBook.put(Side.SELL, sellOrderBook);
            orderBooks.put(instrument, orderBook);
        }
        Map<Side, Map<BigDecimal, OrderBook>> orderBook = orderBooks.get(instrument);
    }

    public OrderTradeDto processOrder(Order order) {
        OrderTradeDto orderTradeDto = null;
        RMap<String, Map<Side, Map<BigDecimal, OrderBook>>> orderBooks = redissonClient.getMap(ORDER_BOOK);
        RReadWriteLock rReadWriteLock = orderBooks.getReadWriteLock(order.getInstrument());
        RLock rLock = rReadWriteLock.writeLock();
        try {
            rLock.lock();
            log.debug("Order {} Locked by {}", order, Thread.currentThread().getName());
            orderTradeDto = MatchingEngine.match(order, orderBooks);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rLock.isLocked()) {
                rLock.unlock();
                log.debug("Unlocked");
            }
        }

        return orderTradeDto;
    }

    public Map<Side, Map<BigDecimal, OrderBook>> getOrderBook(String instrument) {
        RMap<String, Map<Side, Map<BigDecimal, OrderBook>>> orderBooks = redissonClient.getMap(ORDER_BOOK);
        return orderBooks.get(instrument);
    }

    private static final class OrderBookServiceHolder {
        static final OrderBookService orderBookService = new OrderBookService();
    }
}

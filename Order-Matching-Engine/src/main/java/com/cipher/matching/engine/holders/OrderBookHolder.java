package com.cipher.matching.engine.holders;

import com.cipher.matching.engine.bean.Order;
import com.cipher.matching.engine.bean.OrderBook;
import com.cipher.matching.engine.config.RedisConfig;
import com.cipher.matching.engine.dto.OrderTradeDto;
import com.cipher.matching.engine.core.MatchingEngine;
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
public final class OrderBookHolder {

    private static final RedissonClient redissonClient = RedisConfig.getRedissonClient();

    private static final String ORDER_BOOK = "orderBook";

    {
        List<String> instruments = InstrumentHolder.getInstruments();
        instruments.forEach(instrument -> initializeOrderBookWithInstruments(instrument));
        log.info("Instrument initialization done");
    }

    private OrderBookHolder() throws IllegalAccessException {
        if (OrderBookInstanceHolder.ORDER_BOOK_HOLDER != null)
            throw new IllegalAccessException("Object already available!");
    }

    public static OrderBookHolder getInstance() throws IllegalAccessException {
        return OrderBookInstanceHolder.ORDER_BOOK_HOLDER;
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

    public Order cancelOrder(Order order) {
        RMap<String, Map<Side, Map<BigDecimal, OrderBook>>> orderBooks = redissonClient.getMap(ORDER_BOOK);
        RReadWriteLock rReadWriteLock = orderBooks.getReadWriteLock(order.getInstrument());
        RLock rLock = rReadWriteLock.writeLock();
        Order cancelledOrder = null;
        try {
            rLock.lock();
            log.debug("Order {} Locked by {}", order, Thread.currentThread().getName());
            cancelledOrder = MatchingEngine.cancelOrder(order, orderBooks);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rLock.isLocked()) {
                rLock.unlock();
                log.debug("Unlocked");
            }
        }
        return cancelledOrder;
    }

    public Map<Side, Map<BigDecimal, OrderBook>> getOrderBook(String instrument) {
        RMap<String, Map<Side, Map<BigDecimal, OrderBook>>> orderBooks = redissonClient.getMap(ORDER_BOOK);
        return orderBooks.get(instrument);
    }

    private static final class OrderBookInstanceHolder {
        static final OrderBookHolder ORDER_BOOK_HOLDER = new OrderBookHolder();
    }
}

package com.cipher.liquidity.provider.orderbook;

import com.cipher.liquidity.provider.beans.OrderBook;
import com.cipher.liquidity.provider.enums.Side;

import java.math.BigDecimal;
import java.util.Map;
import java.util.TreeMap;

public interface LiquidityOrderBook {

    Map<Side, TreeMap<BigDecimal, OrderBook>> getOrderBook(String instrument);

    void subscribeNewTopic(String instrument);
}

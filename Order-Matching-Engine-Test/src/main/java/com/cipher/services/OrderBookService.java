package com.cipher.services;

import com.cipher.matching.engine.CommandExecutor;
import com.cipher.matching.engine.bean.OrderBook;
import com.cipher.matching.engine.enums.Side;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class OrderBookService {

    private CommandExecutor commandExecutor = new CommandExecutor();

    public Map<Side, Map<BigDecimal, OrderBook>> getOrderBook(String instrument) {
        return commandExecutor.fetchOrderBook(instrument);
    }
}

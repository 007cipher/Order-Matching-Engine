package com.cipher.services;

import com.cipher.entities.Order;
import com.cipher.entities.Trade;
import com.cipher.enums.OrderStatus;
import com.cipher.repositories.TradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TradeService {

    @Autowired
    private TradeRepository tradeRepository;

    public Trade save(Trade trade) {
        return tradeRepository.save(trade);
    }
}

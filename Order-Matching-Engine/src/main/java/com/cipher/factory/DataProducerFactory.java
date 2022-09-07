package com.cipher.factory;

import com.cipher.bean.Order;
import com.cipher.bean.Trade;
import com.cipher.publisher.DataProducer;
import com.cipher.publisher.OrderProducer;
import com.cipher.publisher.TradeProducer;

public class DataProducerFactory {

    private static final DataProducer<Trade> tradeProducer = TradeProducer.getInstance();
    private static final DataProducer<Order> orderProducer = OrderProducer.getInstance();

    public static DataProducer<? extends Object> get(String className) throws ClassNotFoundException {
        if (tradeProducer.getName().equals(className))
            return tradeProducer;
        else if (orderProducer.getName().equals(className))
            return orderProducer;
        else
            throw new ClassNotFoundException("Class not exists with name: " + className);
    }
}

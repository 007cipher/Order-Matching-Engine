package com.cipher.publisher;

import static com.cipher.constants.KafkaConstants.TRADE_PRODUCER;

import com.cipher.bean.Trade;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class TradeProducer implements DataProducer<Trade> {

    private TradeProducer() {

    }

    @Override
    public void produce(Trade order, Producer<String, Trade> producer) {
        producer.send(new ProducerRecord<>(TRADE_PRODUCER,
                null, order));
    }

    @Override
    public String getName() {
        return TradeProducer.class.getName();
    }

    public static DataProducer<Trade> getInstance() {
        return TradeProducerHolder.TRADE_PRODUCER;
    }

    private static class TradeProducerHolder {
        static final TradeProducer TRADE_PRODUCER = new TradeProducer();
    }
}

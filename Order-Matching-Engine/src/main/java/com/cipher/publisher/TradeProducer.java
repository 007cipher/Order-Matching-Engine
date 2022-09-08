package com.cipher.publisher;

import static com.cipher.constants.KafkaConstants.TRADE_PRODUCER;

import com.cipher.bean.Trade;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

@Slf4j
public class TradeProducer implements DataProducer<Trade> {

    private TradeProducer() {

    }

    @Override
    public void produce(Trade trade, Producer<String, Trade> producer) {
        log.info("Trade: {}", trade);
        producer.send(new ProducerRecord<>(TRADE_PRODUCER, null, trade));
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

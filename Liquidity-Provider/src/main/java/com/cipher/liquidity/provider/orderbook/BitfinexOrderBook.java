package com.cipher.liquidity.provider.orderbook;

import com.cipher.liquidity.provider.beans.OrderBook;
import com.cipher.liquidity.provider.enums.Side;
import com.cipher.liquidity.provider.websocket.WebsocketClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.websocket.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

@Slf4j
public class BitfinexOrderBook implements LiquidityOrderBook {

    private WebsocketClient websocketClient;

    private Map<String, String> channelMap = new HashMap<>();

    private Map<String, Map<Side, TreeMap<BigDecimal, OrderBook>>> orderBooks = new HashMap<>();

    private static final String BITFINEX_URL = "wss://api-pub.bitfinex.com/ws/2";

    public BitfinexOrderBook(String instrument) {
        try {
            connect(instrument);
        } catch (URISyntaxException | DeploymentException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void connect(String instrument) throws URISyntaxException, DeploymentException, IOException {
        websocketClient = new WebsocketClient(
                new URI(BITFINEX_URL), instrument);
        websocketClient.addMessageHandler(new WebsocketClient.MessageHandler() {
            public void handleMessage(String message) {
                log.debug("message: " + message);
                parseData(message);
            }
        });
    }

    private void parseData(String message) {
        JsonElement jsonElement = JsonParser.parseString(message);
        if (message.startsWith("{")) {
            /*
                This if condition will handle the first and second message
            */
            parseFirstResponse(jsonElement);
        } else {
            /*
                This else condition will handle the order book
            */
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            String channelId = jsonArray.get(0).getAsString();
            if (!jsonArray.get(1).isJsonArray()) {
                return;
            }
            JsonArray books = jsonArray.get(1).getAsJsonArray();
            if (books.size() > 3) {
                /*
                    This if condition will handle order book is coming first time(means complete order book)
                */
                Map<Side, TreeMap<BigDecimal, OrderBook>> instrumentBook = orderBooks.containsKey(channelId) ? orderBooks.get(channelId) : new HashMap<>();
                TreeMap<BigDecimal, OrderBook> buyBook = new TreeMap<>(Comparator.reverseOrder());
                TreeMap<BigDecimal, OrderBook> sellBook = new TreeMap<>();
                for (int i = 0; i < books.size(); i++) {
                    createBuySellOrderBook(books.get(i).getAsJsonArray(), buyBook, sellBook, channelId);
                }
                instrumentBook.put(Side.BUY, buyBook);
                instrumentBook.put(Side.SELL, sellBook);
                orderBooks.put(channelId, instrumentBook);
            } else {
                /*
                    This else condition will handle if we already have the order book
                */
                Map<Side, TreeMap<BigDecimal, OrderBook>> instrumentBook = orderBooks.get(channelId);
                TreeMap<BigDecimal, OrderBook> buyBook = instrumentBook.get(Side.BUY);
                TreeMap<BigDecimal, OrderBook> sellBook = instrumentBook.get(Side.SELL);
                int orderCount = books.get(1).getAsInt();
                if (orderCount > 0) {
                    createBuySellOrderBook(books, buyBook, sellBook, channelId);
                } else {
                    BigDecimal qty = books.get(2).getAsBigDecimal();
                    Side side = BigDecimal.ZERO.compareTo(qty) > 0 ? Side.SELL : Side.BUY;
                    BigDecimal price = books.get(0).getAsBigDecimal();
                    if (Side.BUY.equals(side))
                        buyBook.remove(price);
                    else
                        sellBook.remove(price);
                }
            }
        }
    }

    private void parseFirstResponse(JsonElement jsonElement) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String event = jsonObject.get("event").getAsString();
        log.info("event: {}", event);
        if ("subscribed".equals(event)) {
            String pair = jsonObject.get("pair").getAsString();
            String channelId = jsonObject.get("chanId").getAsString();
            channelMap.put(channelId, pair);
            log.info("channelId: {} and pair: {}", channelId, pair);
        }
    }

    private void createBuySellOrderBook(JsonArray book, TreeMap<BigDecimal, OrderBook> buyBook, TreeMap<BigDecimal, OrderBook> sellBook, String channelId) {
        log.debug("book: {}", book);
        BigDecimal qty = book.get(2).getAsBigDecimal();
        Side side = BigDecimal.ZERO.compareTo(qty) > 0 ? Side.SELL : Side.BUY;
        BigDecimal price = book.get(0).getAsBigDecimal();
        Optional<String> optionalInstrument = channelMap.entrySet().stream().filter(v -> v.getValue().equals(channelId)).map(Map.Entry::getKey).findFirst();
        if (optionalInstrument.isPresent()) {
            if (Side.BUY.equals(side))
                buyBook.put(price, new OrderBook(optionalInstrument.get(), qty, price, side));
            else
                sellBook.put(price, new OrderBook(optionalInstrument.get(), qty.abs(), price, side));
        }
    }

    @Override
    public Map<Side, TreeMap<BigDecimal, OrderBook>> getOrderBook(String instrument) {
        if (channelMap.containsKey(instrument)) {
            String channelId = channelMap.get(instrument);
            return orderBooks.get(channelId);
        }
        return null;
    }

    @Override
    public void subscribeNewTopic(String instrument) {
        String msg = "{ \"event\": \"subscribe\", \"channel\": \"book\", \"symbol\": \"t" + instrument + "}";
        log.info("Sent message: {}", msg);
        websocketClient.sendMessage(msg);
    }
}
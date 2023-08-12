package com.cipher.liquidity.provider;

import com.cipher.liquidity.provider.orderbook.BitfinexOrderBook;
import com.cipher.liquidity.provider.orderbook.LiquidityOrderBook;

import java.net.URISyntaxException;

public class Test {

    public static void main(String[] args) throws URISyntaxException {
        try {
            LiquidityOrderBook liquidityOrderBook = new BitfinexOrderBook("BTCUSD");
            Thread.sleep(3000);
            LiquidityOrderBook liquidityOrderBook1 = new BitfinexOrderBook("ETHBTC");
            while (true) {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

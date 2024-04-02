package com.kucoin.sdk;

import com.kucoin.sdk.KucoinClientBuilder;
import com.kucoin.sdk.rest.response.OrderCreateResponse;
import com.kucoin.sdk.rest.response.UserSummaryInfoResponse;

import java.io.IOException;

public class UseExample {

    public static void main(String[] args) {

        String key = "your key";
        String secret = "your secret";
        String phrase = "your phrase";

        testRest(key, secret, phrase);
        testWss(key, secret, phrase);
    }

    public static void testRest(String key, String secret, String phrase) {
        KucoinRestWrapper kucoinRestWrapper = new KucoinRestWrapper(key, secret, phrase, true);
        UserSummaryInfoResponse response = kucoinRestWrapper.getUserInfo();
        System.out.println(response);
        String symbol = "ETH-BTC";
        String side = "buy";
        Double size = 0.1;
        Double price = 70000.01;

        OrderCreateResponse response1 = kucoinRestWrapper.createLimitOrder(symbol, side, size, price);
        System.out.println(response1);
    }

    public static void testWss(String key, String secret, String phrase) {
        KucoinWssWrapper kucoinPublicWSClient = new KucoinWssWrapper(key, secret, phrase, false);
        kucoinPublicWSClient.subTicker("ETH-BTC", "KCS-BTC");
        KucoinWssWrapper kucoinPrivateWSClient = new KucoinWssWrapper(key, secret, phrase, true);
        kucoinPrivateWSClient.subAccountBalance();
    }
}

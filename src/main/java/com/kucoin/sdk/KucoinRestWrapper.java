package com.kucoin.sdk;

import com.kucoin.sdk.rest.request.OrderCreateApiRequest;
import com.kucoin.sdk.rest.response.*;
import org.apache.commons.lang3.Validate;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;

public class KucoinRestWrapper {
    private static final String URL = "https://openapi-v2.kucoin.com";

    private KucoinRestClient restClient;

    public KucoinRestWrapper(String key, String secret, String phrase, Boolean isLogin) {
        KucoinClientBuilder builder = new KucoinClientBuilder().withBaseUrl(URL);
        if (isLogin) {
            Validate.notNull(key, "apiKey is null");
            Validate.notNull(secret, "secretKey is null");
            Validate.notNull(phrase, "passphrase is null");
            builder = builder.withApiKey(key, secret, phrase);
        }
        restClient = builder.buildRestClient();
    }

    // obtain account summary information.
    // https://www.kucoin.com/docs/rest/account/basic-info/get-account-summary-info
    public UserSummaryInfoResponse getUserInfo() {

        try {
            return restClient.accountAPI().getUserSummaryInfo();
        } catch (IOException e) {
            System.out.println("getUserInfo - error" + e.toString());
            return null;
        }
    }

    // Trade API require Login
    // more info refer to https://www.kucoin.com/docs/rest/spot-trading/orders/place-order
    public OrderCreateResponse createLimitOrder(String symbol, String side, Double size, Double price) {
        try {
            OrderCreateApiRequest request = OrderCreateApiRequest.builder()
                    .symbol(symbol) // ETH-BTC
                    .side(side) // buy or sell
                    .size(BigDecimal.valueOf(size))
                    .price(BigDecimal.valueOf(price))
                    .type("limit") // limit or market default is limit
                    .clientOid(UUID.randomUUID().toString()).build();
            return restClient.orderAPI().createOrder(request);
        } catch (IOException e) {
            System.out.println("createLimitOrder - error" + e.toString());
            return null;
        }
    }

    // https://www.kucoin.com/docs/rest/spot-trading/orders/cancel-order-by-orderid
    public OrderCancelResponse cancelByOrderID(String orderId) {
        try {

            return restClient.orderAPI().cancelOrder(orderId);
        } catch (IOException e) {
            System.out.println("cancleByOrderID - error" + e.toString());
            return null;
        }
    }

    // https://www.kucoin.com/docs/rest/spot-trading/orders/cancel-all-orders
    public OrderCancelResponse cancelALL(String symbol, String type) {
        try {

            return restClient.orderAPI().cancelAllOrders(symbol, type);
        } catch (IOException e) {
            System.out.println("cancelALL - error" + e.toString());
            return null;
        }
    }

    // Market Data can be used without a signed request.
    // refer to https://www.kucoin.com/docs/rest/spot-trading/market-data/get-ticker
    public TickerResponse getTicker(String symbol) {
        try {
            return restClient.symbolAPI().getTicker(symbol);
        } catch (IOException e) {
            System.out.println("getTicker - error" + e.toString());
            return null;
        }
    }

}

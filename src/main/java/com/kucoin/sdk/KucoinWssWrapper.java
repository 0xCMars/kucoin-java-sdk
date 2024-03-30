package com.kucoin.sdk;

import com.kucoin.sdk.websocket.KucoinAPICallback;
import com.kucoin.sdk.websocket.event.*;
import org.apache.commons.lang3.Validate;

import javax.imageio.IIOException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public class KucoinWssWrapper {
    private static final String URL = "https://openapi-v2.kucoin.com";

    private KucoinPrivateWSClient kcPrivateWs;
    private KucoinPublicWSClient kcPubWs;

    public KucoinWssWrapper(String key, String secret, String phrase, Boolean isPrivate) {
        KucoinClientBuilder builder = new KucoinClientBuilder().withBaseUrl(URL);
        try {
            if (isPrivate) {
                Validate.notNull(key, "apiKey is null");
                Validate.notNull(secret, "secretKey is null");
                Validate.notNull(phrase, "passphrase is null");
                kcPrivateWs = builder.withApiKey(key, secret, phrase).buildPrivateWSClient();
            } else {
                kcPubWs = builder.buildPublicWSClient();
            }
        } catch (IOException e) {
            System.out.println("getAccount - error" + e.toString());
        }
    }

    // https://www.kucoin.com/docs/websocket/spot-trading/public-channels/ticker
    public void subTicker(String... symbols) {
        kcPubWs.onTicker(response -> {
//            System.out.println(response);
            decryptEventPublic(response);
        }, symbols);
    }

    public void subKlines(String symbolAndType) {
        kcPubWs.onCandles(response -> {
            System.out.println(response);
        }, symbolAndType);
    }

    // push all change events of your orders.
    // https://www.kucoin.com/docs/websocket/spot-trading/private-channels/private-order-change-v2
    public void subOrderV2Change() {
        Validate.notNull(kcPrivateWs, "private channel only");
        kcPrivateWs.onOrderV2Change(response -> {
            System.out.println(response);
            decryptEventPrivate(response);
        });
    }

    // https://www.kucoin.com/docs/websocket/spot-trading/private-channels/account-balance-change
    public void subAccountBalance() {
        kcPrivateWs.onAccountBalance(response -> {
            System.out.println(response);
            decryptEventPrivate(response);
        });
    }

    private void decryptEventPublic(final KucoinEvent event){
        if (event.getData() != null) {
            final String subject = event.getSubject();
            final String topic = event.getTopic();
            if (subject.equals("trade.ticker")) {
                TickerChangeEvent msg = (TickerChangeEvent)event.getData();
                final BigDecimal bid = msg.getBestBid();
                final BigDecimal bsz = msg.getBestBidSize();
                final BigDecimal ask = msg.getBestAsk();
                final BigDecimal asz = msg.getBestAskSize();
                final BigDecimal last = msg.getPrice();
                final BigDecimal size = msg.getSize();
                final long ts = msg.getTime();
                System.out.println(new StringBuilder()
                        .append("kucoin,").append(topic)
                        .append(",").append(bsz)
                        .append(",").append(bid)
                        .append(",").append(ask)
                        .append(",").append(asz)
                        .append(",").append(last)
                        .append(",").append(size)
                        .append(",").append(ts).toString());
            }
        }
    }

    private void decryptEventPrivate(final KucoinEvent event){
        if (event.getData() != null) {
            final String subject = event.getSubject();
            final String topic = event.getTopic();
            if (subject.equals("account.balance")) {
                AccountChangeEvent msg = (AccountChangeEvent)event.getData();
                final BigDecimal total = msg.getTotal();
                final BigDecimal available = msg.getAvailable();
                final BigDecimal availableChange = msg.getAvailableChange();
                final String currency = msg.getCurrency();
                final String ts = msg.getTime();
                System.out.println(new StringBuilder()
                        .append("kucoin,").append(topic)
                        .append(",").append(total)
                        .append(",").append(available)
                        .append(",").append(availableChange)
                        .append(",").append(currency)
                        .append(",").append(ts).toString());
            } else if (subject.equals("orderChange")) {
                OrderChangeEvent msg = (OrderChangeEvent)event.getData();
                final String symbol = msg.getSymbol();
                final String type = msg.getType(); // open / match / done / filled / update
                final String status = msg.getStatus();
                final String orderType = msg.getOrderType(); // limit or not
                final String side = msg.getSide();
                final BigDecimal filledSz = msg.getFilledSize();
                final BigDecimal remainSize = msg.getRemainSize();
                final long ts = msg.getTs();
                System.out.println(new StringBuilder()
                        .append("kucoin,").append(topic)
                        .append(",").append(symbol)
                        .append(",").append(type)
                        .append(",").append(status)
                        .append(",").append(orderType)
                        .append(",").append(side)
                        .append(",").append(filledSz)
                        .append(",").append(remainSize)
                        .append(",").append(ts).toString());
            }
        }
    }
}

package com.devexperts.logvinov.zeromq;

import com.devexperts.logvinov.shared.Quote;
import com.devexperts.logvinov.shared.QuoteUtils;
import org.zeromq.ZMQ;

public class QuoteSubscriber {
    public static void main(String[] args) {
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket socket = context.socket(ZMQ.PULL);
//        socket.subscribe("EUR/USD".getBytes());
        socket.bind("tcp://*:" + QuoteDistributor.PORT);
        for (int i = 0; i < 100; i++) {
            Quote quote = QuoteUtils.deserialize(socket.recv());
            System.out.println("com.devexperts.logvinov.shared.Quote received: " + quote);
        }
        socket.close();
        context.term();
    }
}

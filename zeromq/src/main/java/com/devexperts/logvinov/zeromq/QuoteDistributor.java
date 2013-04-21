package com.devexperts.logvinov.zeromq;

import com.devexperts.logvinov.shared.Quote;
import org.zeromq.ZMQ;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.google.common.base.Preconditions.checkNotNull;

public class QuoteDistributor {

    public static final int PORT = 5556;

    private static final Quote[] BASE_QUOTES = {
            new Quote("EUR/USD", System.currentTimeMillis(), 1.3054, 1.3060),
            new Quote("USD/JPY", System.currentTimeMillis(), 99.4700, 99.4700),
            new Quote("GBP/EUR", System.currentTimeMillis(), 1.1663, 1.1671)
    };
    private static final ExecutorService executor = Executors.newFixedThreadPool(BASE_QUOTES.length);

    public static void main(String[] args) {
        for (Quote quote : BASE_QUOTES) {
            executor.submit(new QuoteGenerator(quote));
        }
        executor.shutdown();
    }

    private static class QuoteGenerator implements Runnable {

        private final Random random = new Random(System.currentTimeMillis());
        private final Quote baseQuote;

        private final ZMQ.Context context;
        private final ZMQ.Socket sender;

        private QuoteGenerator(Quote baseQuote) {
            this.baseQuote = checkNotNull(baseQuote, "baseQuote");
            // ZeroMQ part
            context = ZMQ.context(1);
            sender = context.socket(ZMQ.PUSH);
            sender.connect("tcp://localhost:" + PORT);
        }

        public void close() {
            sender.close();
            context.term();
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                Quote quote = generateQuote();
                sender.send(QuoteUtils.serialize(quote), 0);
                System.out.println(Thread.currentThread().getName() + " generated quote: " + quote);
                try {
                    Thread.sleep(random.nextInt(500));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        private Quote generateQuote() {
            int r = random.nextInt(10);
            int sign = (r % 2 == 0) ? 1 : -1;
            double delta = sign * r / 10000d;
            return new Quote(baseQuote.getSymbol(), System.currentTimeMillis(), baseQuote.getBid() + delta, baseQuote.getAsk() + delta);
        }
    }
}

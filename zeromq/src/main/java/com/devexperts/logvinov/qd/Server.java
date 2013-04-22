package com.devexperts.logvinov.qd;

import com.devexperts.connector.proto.ApplicationConnectionFactory;
import com.devexperts.qd.QDDistributor;
import com.devexperts.qd.QDFactory;
import com.devexperts.qd.QDStream;
import com.devexperts.qd.SymbolCodec;
import com.devexperts.qd.ng.RecordBuffer;
import com.devexperts.qd.ng.RecordCursor;
import com.devexperts.qd.qtp.AgentAdapter;
import com.devexperts.qd.qtp.ConfigurableMessageAdapterFactory;
import com.devexperts.qd.qtp.MessageConnector;
import com.devexperts.qd.qtp.MessageConnectors;
import com.devexperts.qd.stats.QDStats;
import com.devexperts.qd.util.Decimal;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class Server {

    private static final Scheme scheme = Scheme.getInstance();
    private static final SymbolCodec codec = scheme.getCodec();

    private static final String ADDRESS = "server:5555";

    public static void main(String[] args) throws InterruptedException {
        QDStream stream = QDFactory.getDefaultFactory().createStream(scheme);
        QDDistributor distributor = stream.createDistributor(null);

        ApplicationConnectionFactory acFactory = MessageConnectors.applicationConnectionFactory(
                (ConfigurableMessageAdapterFactory) new AgentAdapter.Factory(stream));
        List<MessageConnector> connectors = MessageConnectors.createMessageConnectors(acFactory, ADDRESS, QDStats.VOID);
        MessageConnectors.startMessageConnectors(connectors);

        RecordBuffer buffer = new RecordBuffer(); // write data here

        new Thread(new Publisher(buffer, distributor)).start();

        new CountDownLatch(1).await();
    }

    private static void appendQuote(RecordBuffer buffer, String symbol, double bid, double ask) {
        QuoteDescriptor descriptor = Scheme.getQuoteDescriptor();
        long timestamp = System.currentTimeMillis();
        RecordCursor cursor = buffer.add(descriptor.getRecord(), codec.encode(symbol), symbol);
        cursor.setObj(descriptor.getSymbol().getIndex(), symbol);
        cursor.setInt(descriptor.getTimeHi().getIndex(), QdUtils.hi(timestamp));
        cursor.setInt(descriptor.getTimeLo().getIndex(), QdUtils.lo(timestamp));
        cursor.setInt(descriptor.getBid().getIndex(), Decimal.compose(bid));
        cursor.setInt(descriptor.getAsk().getIndex(), Decimal.compose(ask));
    }

    private static class Publisher implements Runnable {

        private final Random rand = new Random();

        private final RecordBuffer buffer;
        private final QDDistributor distributor;

        private Publisher(RecordBuffer buffer, QDDistributor distributor) {
            this.buffer = buffer;
            this.distributor = distributor;
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                for (int i = 0; i < 100; i++) {
                    appendQuote(buffer, "EUR/USD", rand.nextDouble(), rand.nextDouble());
                }
                distributor.processData(buffer);
                buffer.clear();
            }
        }
    }
}
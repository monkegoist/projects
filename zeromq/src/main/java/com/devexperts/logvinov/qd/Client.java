package com.devexperts.logvinov.qd;

import com.devexperts.connector.proto.ApplicationConnectionFactory;
import com.devexperts.logvinov.shared.Quote;
import com.devexperts.qd.*;
import com.devexperts.qd.ng.RecordCursor;
import com.devexperts.qd.ng.RecordSource;
import com.devexperts.qd.qtp.ConfigurableMessageAdapterFactory;
import com.devexperts.qd.qtp.DistributorAdapter;
import com.devexperts.qd.qtp.MessageConnector;
import com.devexperts.qd.qtp.MessageConnectors;
import com.devexperts.qd.stats.QDStats;
import com.devexperts.qd.util.DataProcessor;
import com.devexperts.qd.util.Decimal;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Client {

    private static final String SYMBOL = "*";

    private static final String ADDRESS = "gftcore:10220";

    private static final Scheme scheme = Scheme.getInstance();
    private static final SymbolCodec codec = scheme.getCodec();

    private static AtomicInteger counter = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        DataRecord quote = scheme.findRecordByName(Scheme.QUOTE_RECORD_NAME);
        QDStream stream = QDFactory.getDefaultFactory().createStream(scheme);
        QDAgent agent = stream.createAgent(null);
        SubscriptionBuffer buffer = new SubscriptionBuffer();
        buffer.visitRecord(quote, codec.getWildcardCipher(), SYMBOL);
        agent.setSubscription(buffer);
        attachListener(agent);
        ApplicationConnectionFactory acFactory = MessageConnectors.applicationConnectionFactory(
                (ConfigurableMessageAdapterFactory) new DistributorAdapter.Factory(stream));
        List<MessageConnector> connectors = MessageConnectors.createMessageConnectors(acFactory, ADDRESS, QDStats.VOID);
        MessageConnectors.startMessageConnectors(connectors);

        new CountDownLatch(1).await();
    }

    private static void attachListener(QDAgent agent) {
        DataProcessor dataProcessor = new DataProcessor(Executors.newSingleThreadExecutor()) {
            @Override
            protected void processData(RecordSource source) {
                RecordCursor cur;
                while ((cur = source.next()) != null) {
                    countQuote(cur);
                }
            }
        };
        dataProcessor.startProcessing(agent);
    }

    private static void countQuote(RecordCursor cur) {
        QuoteDescriptor desc = Scheme.getQuoteDescriptor();
        String symbol = codec.decode(cur.getCipher(), cur.getSymbol());
        long timestamp = toLong(
                cur.getInt(desc.getTimeHi().getIndex()),
                cur.getInt(desc.getTimeLo().getIndex())
        );
        double bid = Decimal.toDouble(cur.getInt(desc.getBid().getIndex()));
        double ask = Decimal.toDouble(cur.getInt(desc.getAsk().getIndex()));
        Quote quote = new Quote(symbol, timestamp, bid, ask);
        System.out.println(quote);
        counter.incrementAndGet();
    }

    private static long toLong(long hi, long lo) {
        return (hi << 32) | (lo & 0xFFFFFFFFL);
    }
}

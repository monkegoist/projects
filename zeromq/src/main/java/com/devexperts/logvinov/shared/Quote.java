package com.devexperts.logvinov.shared;

import java.io.Serializable;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Immutable entity, representing quote.
 */
public final class Quote implements Serializable {

    private final String symbol;
    private final long timestamp;
    private final double bid;
    private final double ask;

    public Quote(String symbol, long timestamp, double bid, double ask) {
        this.symbol = checkNotNull(symbol, "symbol");
        checkArgument(timestamp > 0, "timestamp > 0");
        checkArgument(bid > 0, "bid > 0");
        checkArgument(ask > 0, "ask > 0");
        this.timestamp = timestamp;
        this.bid = bid;
        this.ask = ask;
    }

    public String getSymbol() {
        return symbol;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public double getBid() {
        return bid;
    }

    public double getAsk() {
        return ask;
    }

    @Override
    public String toString() {
        return "Quote{" +
                "symbol='" + symbol + '\'' +
                ", timestamp=" + timestamp +
                ", bid=" + bid +
                ", ask=" + ask +
                '}';
    }
}
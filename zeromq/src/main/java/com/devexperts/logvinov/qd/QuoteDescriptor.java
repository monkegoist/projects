package com.devexperts.logvinov.qd;

import com.devexperts.qd.DataIntField;
import com.devexperts.qd.DataObjField;
import com.devexperts.qd.DataRecord;

public final class QuoteDescriptor {

    private DataRecord record;
    private final DataObjField symbol;
    private final DataIntField timeHi;
    private final DataIntField timeLo;
    private final DataIntField bid;
    private final DataIntField ask;

    public QuoteDescriptor(DataObjField symbol, DataIntField timeHi, DataIntField timeLo, DataIntField bid, DataIntField ask) {
        this.symbol = symbol;
        this.timeHi = timeHi;
        this.timeLo = timeLo;
        this.bid = bid;
        this.ask = ask;
    }

    public void setRecord(DataRecord record) {
        this.record = record;
    }

    public DataRecord getRecord() {
        return record;
    }

    public DataObjField getSymbol() {
        return symbol;
    }

    public DataIntField getTimeHi() {
        return timeHi;
    }

    public DataIntField getTimeLo() {
        return timeLo;
    }

    public DataIntField getBid() {
        return bid;
    }

    public DataIntField getAsk() {
        return ask;
    }
}
package com.devexperts.gft.on.server.gen;

import com.devexperts.gft.on.shared.Instrument;
import com.devexperts.gft.on.shared.Record;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class RecordGenerator {

    private static final ThreadLocalRandom rand = ThreadLocalRandom.current();

    public static List<Record> generateRecords(Map<Instrument, Integer> instrumentsMap) {
        List<Record> records = new ArrayList<Record>(instrumentsMap.size());
        for (Map.Entry<Instrument, Integer> entry : instrumentsMap.entrySet()) {
            Record record = generateRecord(entry.getKey(), entry.getValue());
            records.add(record);
        }
        return records;
    }

    private static Record generateRecord(Instrument instrument, Integer underlying) {
        double clientVolume = rand.nextDouble(10000, 1000000);
        double clientPrice = rand.nextDouble();
        double clientPL = clientVolume * clientPrice;
        double brokerVolume = rand.nextDouble(10000, 1000000);
        double brokerPrice = rand.nextDouble();
        double brokerPL = brokerVolume * brokerPrice;
        double bid = rand.nextDouble(100);
        double ask = bid + rand.nextDouble();
        return new Record(instrument, underlying, clientVolume, clientPrice, clientPL,
                brokerVolume, brokerPrice, brokerPL, bid, ask);
    }
}
package com.devexperts.gft.on.server;

import com.devexperts.gft.on.server.gen.InstrumentGenerator;
import com.devexperts.gft.on.server.gen.RecordGenerator;
import com.devexperts.gft.on.shared.Instrument;
import com.devexperts.gft.on.shared.Record;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class SessionRecordProvider {
    private static final ThreadLocalRandom rand = ThreadLocalRandom.current();

    /**
     * Singleton to make things simpler and not mess around with Spring.
     */
    private static final SessionRecordProvider INSTANCE = new SessionRecordProvider();

    public static SessionRecordProvider getInstance() {
        return INSTANCE;
    }

    // contains registered sessions along with records that were sent to client
    private final Map<String, List<Record>> sessionContainer = new ConcurrentHashMap<String, List<Record>>();

    public String registerSession(int instrumentsCount) {
        List<Instrument> instruments = InstrumentGenerator.generateInstruments(instrumentsCount);
        Map<Instrument, Integer> mapping = new LinkedHashMap<Instrument, Integer>();

        int maxChildren = 4;
        int startIndex = 0;
        int endIndex;
        while ((endIndex = rand.nextInt(startIndex, startIndex + maxChildren + 1)) < instrumentsCount) {
            List<Instrument> pack = instruments.subList(startIndex, endIndex);
            processPack(pack, mapping);
            startIndex = endIndex;
        }
        // process rest
        processPack(instruments.subList(startIndex, instrumentsCount), mapping);

        List<Record> initialData = RecordGenerator.generateRecords(mapping);

        String uid = UUID.randomUUID().toString();
        sessionContainer.put(uid, initialData);

        return uid;
    }

    public List<Record> getInitialData(String key) {
        checkSession(key);
        return sessionContainer.get(key);
    }

    public List<Record> getUpdates(String key, int updatesCount) {
        List<Record> initialData = getInitialData(key);
        List<Record> candidates;
        if (updatesCount >= initialData.size()) {
            candidates = initialData;
        } else {
            Collections.shuffle(initialData); // choose updated instruments at random
            candidates = initialData.subList(0, updatesCount);
        }

        Map<Instrument, Integer> mapping = new LinkedHashMap<Instrument, Integer>(updatesCount);
        for (Record record : candidates) {
            mapping.put(record.getInstrument(), record.getUnderlyingId());
        }
        return RecordGenerator.generateRecords(mapping);
    }

    private void checkSession(String key) {
        if (!sessionContainer.containsKey(key)) {
            throw new IllegalArgumentException("No session corresponding to key '" + key + "' was found");
        }
    }

    private static void processPack(List<Instrument> pack, Map<Instrument, Integer> mapping) {
        if (pack.isEmpty()) return;
        Instrument underlying = pack.get(0);
        mapping.put(underlying, null);
        if (pack.size() > 1) {
            for (int i = 1; i < pack.size(); i++) {
                mapping.put(pack.get(i), underlying.getId());
            }
        }
    }
}
package com.devexperts.gft.on.server.gen;

import com.devexperts.gft.on.shared.Instrument;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class InstrumentGenerator {

    private static final ThreadLocalRandom rand = ThreadLocalRandom.current();

    private static final char[] ALPHABET = new char[]{
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
            'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    };

    public static List<Instrument> generateInstruments(int count) {
        List<Instrument> instruments = new ArrayList<Instrument>(count);
        int startIndex = rand.nextInt(10000, 20000);
        for (int i = 0; i < count; i++) {
            instruments.add(randomInstrument(startIndex));
            startIndex += rand.nextInt(1, 100);
        }
        return instruments;
    }

    private static Instrument randomInstrument(int id) {
        Instrument.Type type = randomType();
        return new Instrument(id, type, randomSymbol(type));
    }

    private static Instrument.Type randomType() {
        int random = rand.nextInt(1, 4);
        switch (random) {
            case 1:
                return Instrument.Type.FX;
            case 2:
                return Instrument.Type.CFD;
            default:
                return Instrument.Type.SB;
        }
    }

    private static String randomSymbol(Instrument.Type type) {
        switch (type) {
            case FX:
                return randomSequence(3) + "/" + randomSequence(3);
            case CFD:
                return randomSequence(5);
            default:
                return "." + randomSequence(4);
        }
    }

    private static char randomCharacter() {
        return ALPHABET[rand.nextInt(0, ALPHABET.length)];
    }

    private static String randomSequence(int length) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(randomCharacter());
        }
        return builder.toString();
    }
}
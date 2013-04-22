package com.devexperts.logvinov.qd;

public final class QdUtils {

    private QdUtils() {
        // no instances
    }

    public static long toLong(long hi, long lo) {
        return (hi << 32) | (lo & 0xFFFFFFFFL);
    }

    public static int hi(long a) {
        return (int) ((a >> 32) & 0xFFFFFFFFL);
    }

    public static int lo(long a) {
        return (int) (a & 0xFFFFFFFFL);
    }
}
package com.devexperts.gft.on.client;

public class AppSettings {
    private final int instrumentsCount;
    private final int pollingPeriod;
    private final int updatesCount;

    public AppSettings(int instrumentsCount, int pollingPeriod, int updatesCount) {
        this.instrumentsCount = instrumentsCount;
        this.pollingPeriod = pollingPeriod;
        this.updatesCount = updatesCount;
    }

    public int getInstrumentsCount() {
        return instrumentsCount;
    }

    public int getPollingPeriod() {
        return pollingPeriod;
    }

    public int getUpdatesCount() {
        return updatesCount;
    }
}
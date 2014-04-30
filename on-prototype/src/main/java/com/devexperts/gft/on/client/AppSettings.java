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

    /**
     * @return how many instruments will we display?
     */
    public int getInstrumentsCount() {
        return instrumentsCount;
    }

    /**
     * @return how frequently will we query for new data?
     */
    public int getPollingPeriod() {
        return pollingPeriod;
    }

    /**
     * @return how many updated records are we willing to receive upon each query?
     */
    public int getUpdatesCount() {
        return updatesCount;
    }
}
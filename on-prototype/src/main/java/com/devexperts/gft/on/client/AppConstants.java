package com.devexperts.gft.on.client;

import com.google.gwt.i18n.client.Constants;
import com.google.gwt.i18n.client.NumberFormat;

public interface AppConstants extends Constants {

    public static final NumberFormat VOLUME_FORMAT = NumberFormat.getFormat("#.##");
    public static final NumberFormat PRICE_FORMAT = NumberFormat.getFormat("#.#####");

    @DefaultStringValue("Instruments count")
    String instrumentsCount();

    @DefaultStringValue("Polling period, ms")
    String pollingPeriod();

    @DefaultStringValue("Updates count / period")
    String updatesCount();

    @DefaultStringValue("Session initialization failed")
    String failedToRegisterSession();

    @DefaultStringValue("Initial data load failed")
    String failedToLoadInitialData();

    @DefaultStringValue("Updates data load failed")
    String failedToLoadUpdates();

    @DefaultStringValue("Instrument")
    String instrumentColumnName();

    @DefaultStringValue("Client volume")
    String clientVolumeColumnName();

    @DefaultStringValue("Client price")
    String clientPriceColumnName();

    @DefaultStringValue("Client PL")
    String clientPLColumnName();

    @DefaultStringValue("Broker volume")
    String brokerVolumeColumnName();

    @DefaultStringValue("Broker price")
    String brokerPriceColumnName();

    @DefaultStringValue("Broker PL")
    String brokerPLColumnName();

    @DefaultStringValue("Bid")
    String bidColumnName();

    @DefaultStringValue("Ask")
    String askColumnName();
}
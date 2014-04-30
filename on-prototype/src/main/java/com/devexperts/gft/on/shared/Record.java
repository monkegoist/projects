package com.devexperts.gft.on.shared;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gwt.user.client.rpc.IsSerializable;

public class Record implements IsSerializable {

    private Record() {
        // GWT serialization
    }

    private Instrument instrument;
    private Integer underlyingId;
    private double clientVolume;
    private double clientPrice;
    private double clientPL;
    private double brokerVolume;
    private double brokerPrice;
    private double brokerPL;
    private double bid;
    private double ask;

    @JsonCreator
    public Record(@JsonProperty("instrument") Instrument instrument,
                  @JsonProperty("underlyingId") Integer underlyingId,
                  @JsonProperty("clientVolume") double clientVolume,
                  @JsonProperty("clientPrice") double clientPrice,
                  @JsonProperty("clientPL") double clientPL,
                  @JsonProperty("brokerVolume") double brokerVolume,
                  @JsonProperty("brokerPrice") double brokerPrice,
                  @JsonProperty("brokerPL") double brokerPL,
                  @JsonProperty("bid") double bid,
                  @JsonProperty("ask") double ask) {
        this.instrument = instrument;
        this.underlyingId = underlyingId;
        this.clientVolume = clientVolume;
        this.clientPrice = clientPrice;
        this.clientPL = clientPL;
        this.brokerVolume = brokerVolume;
        this.brokerPrice = brokerPrice;
        this.brokerPL = brokerPL;
        this.bid = bid;
        this.ask = ask;
    }

    public Instrument getInstrument() {
        return instrument;
    }

    public Integer getUnderlyingId() {
        return underlyingId;
    }

    public double getClientVolume() {
        return clientVolume;
    }

    public double getClientPrice() {
        return clientPrice;
    }

    public double getClientPL() {
        return clientPL;
    }

    public double getBrokerVolume() {
        return brokerVolume;
    }

    public double getBrokerPrice() {
        return brokerPrice;
    }

    public double getBrokerPL() {
        return brokerPL;
    }

    public double getBid() {
        return bid;
    }

    public double getAsk() {
        return ask;
    }
}
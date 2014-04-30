package com.devexperts.gft.on.shared;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gwt.user.client.rpc.IsSerializable;

public class Instrument implements IsSerializable {
    public enum Type {FX, CFD, SB}

    private Instrument() {
        // GWT serialization
    }

    private int id;
    private Type type;
    private String symbol;

    @JsonCreator
    public Instrument(@JsonProperty("id") int id,
                      @JsonProperty("type") Type type,
                      @JsonProperty("symbol") String symbol) {
        this.id = id;
        this.type = type;
        this.symbol = symbol;
    }

    public int getId() {
        return id;
    }

    public Type getType() {
        return type;
    }

    public String getSymbol() {
        return symbol;
    }

    @Override
    public String toString() {
        return "Instrument{" +
                "id=" + id +
                ", type=" + type +
                ", symbol='" + symbol + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Instrument that = (Instrument) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
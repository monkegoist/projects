package com.devexperts.gft.on.client.util;

import com.sencha.gxt.core.client.ValueProvider;

public abstract class UnmodifiableValueProvider<T, V> implements ValueProvider<T, V> {

    private final String path;

    protected UnmodifiableValueProvider(String path) {
        this.path = path;
    }

    @Override
    public void setValue(T object, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getPath() {
        return path;
    }
}
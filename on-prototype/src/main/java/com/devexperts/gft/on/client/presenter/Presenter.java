package com.devexperts.gft.on.client.presenter;

import com.google.gwt.user.client.ui.RootPanel;

/**
 * Presenter abstraction.
 */
public interface Presenter {
    /**
     * Add necessary view handlers.
     */
    void bind();

    /**
     * Display view on the panel.
     *
     * @param panel root panel
     */
    void display(RootPanel panel);
}
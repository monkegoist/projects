package com.devexperts.gft.on.client;

import com.devexperts.gft.on.client.presenter.GWTPresenter;
import com.devexperts.gft.on.client.presenter.GXTPresenter;
import com.devexperts.gft.on.client.presenter.Presenter;
import com.devexperts.gft.on.client.presenter.RootPresenter;
import com.devexperts.gft.on.client.view.GWTView;
import com.devexperts.gft.on.client.view.GXTView;
import com.devexperts.gft.on.client.view.RootView;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point of application.
 */
public class AppEntryPoint implements EntryPoint {

    public enum WidgetType {GWT, GXT}

    public static final String WIDGET_TYPE = "type";
    public static final String INSTRUMENTS_COUNT = "instruments";
    public static final String PERIOD = "period";
    public static final String UPDATES_COUNT = "updates";

    @Override
    public void onModuleLoad() {
        Presenter presenter;

        String type = Window.Location.getParameter(WIDGET_TYPE);
        if (type != null) {
            AppSettings settings = new AppSettings(
                    Integer.parseInt(Window.Location.getParameter(INSTRUMENTS_COUNT)),
                    Integer.parseInt(Window.Location.getParameter(PERIOD)),
                    Integer.parseInt(Window.Location.getParameter(UPDATES_COUNT))
            );
            WidgetType widgetType = WidgetType.valueOf(type);
            if (widgetType == WidgetType.GWT) {
                GWTView view = new GWTView();
                presenter = new GWTPresenter(settings, view);
            } else {
                GXTView view = new GXTView();
                presenter = new GXTPresenter(settings, view);
            }
        } else {
            RootView view = new RootView();
            presenter = new RootPresenter(view);
        }

        presenter.bind();
        presenter.display(RootPanel.get());
    }
}

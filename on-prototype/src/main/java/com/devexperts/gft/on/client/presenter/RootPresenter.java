package com.devexperts.gft.on.client.presenter;

import com.devexperts.gft.on.client.AppEntryPoint;
import com.devexperts.gft.on.client.view.RootView;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.sencha.gxt.widget.core.client.event.SelectEvent;

/**
 * Presenter of main application view.
 */
public class RootPresenter implements Presenter {
    /**
     * View abstraction.
     */
    @SuppressWarnings("UnusedDeclaration")
    public interface Display {
        boolean isDataValid();

        int getInstrumentsCount();

        int getPollingPeriod();

        int getUpdatesCount();

        void addGwtHandler(SelectEvent.SelectHandler selectHandler);

        void addGxtHandler(SelectEvent.SelectHandler selectHandler);
    }

    private final RootView view;

    public RootPresenter(RootView view) {
        this.view = view;
    }

    @Override
    public void bind() {
        view.addGwtHandler(handlerFor(AppEntryPoint.WidgetType.GWT));
        view.addGxtHandler(handlerFor(AppEntryPoint.WidgetType.GXT));
    }

    @Override
    public void display(RootPanel panel) {
        panel.add(view.asWidget());
    }

    private SelectEvent.SelectHandler handlerFor(final AppEntryPoint.WidgetType widgetType) {
        return new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent selectEvent) {
                if (view.isDataValid()) {
                    String url = constructUrl(widgetType);
                    Window.open(url, "_blank", "");
                }
            }
        };
    }

    private String constructUrl(AppEntryPoint.WidgetType type) {
        return GWT.getHostPageBaseURL() + "?" + AppEntryPoint.WIDGET_TYPE + "=" + type.name() +
                "&" + AppEntryPoint.INSTRUMENTS_COUNT + "=" + view.getInstrumentsCount() +
                "&" + AppEntryPoint.PERIOD + "=" + view.getPollingPeriod() +
                "&" + AppEntryPoint.UPDATES_COUNT + "=" + view.getUpdatesCount();
    }
}

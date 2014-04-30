package com.devexperts.gft.on.client.presenter;

import com.devexperts.gft.on.client.AppConstants;
import com.devexperts.gft.on.client.AppSettings;
import com.devexperts.gft.on.client.view.GXTView;
import com.devexperts.gft.on.shared.Record;
import com.devexperts.gft.on.shared.RecordService;
import com.devexperts.gft.on.shared.RecordServiceAsync;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

import java.util.List;

/**
 * GXT widget presenter.
 */
public class GXTPresenter implements Presenter {
    /**
     * View abstraction.
     */
    @SuppressWarnings("UnusedDeclaration")
    public interface Display {
        void addRecord(Record record);

        void updateRecord(Record record);
    }

    private final AppConstants constants = GWT.create(AppConstants.class);
    private final RecordServiceAsync recordService = GWT.create(RecordService.class);

    private final AppSettings settings;
    private final GXTView view;

    public GXTPresenter(AppSettings settings, GXTView view) {
        this.settings = settings;
        this.view = view;
    }

    @Override
    public void bind() {
        recordService.registerSession(settings.getInstrumentsCount(), new AsyncCallback<String>() {
            @Override
            public void onFailure(Throwable caught) {
                Window.alert(constants.failedToRegisterSession());
            }

            @Override
            public void onSuccess(final String key) {
                recordService.getInitialData(key, new AsyncCallback<List<Record>>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        Window.alert(constants.failedToLoadInitialData());
                    }

                    @Override
                    public void onSuccess(List<Record> result) {
                        for (Record record : result) {
                            view.addRecord(record);
                        }
                        schedulePoller(key);
                    }
                });
            }
        });
    }

    private void schedulePoller(final String key) {
        Timer timer = new Timer() {
            @Override
            public void run() {
                recordService.getUpdates(key, settings.getUpdatesCount(), new AsyncCallback<List<Record>>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        Window.alert(constants.failedToLoadUpdates());
                    }

                    @Override
                    public void onSuccess(List<Record> result) {
                        for (Record record : result) {
                            view.updateRecord(record);
                        }
                    }
                });
            }
        };
        timer.scheduleRepeating(settings.getPollingPeriod());
    }

    @Override
    public void display(RootPanel panel) {
        panel.add(view.asWidget());
    }
}

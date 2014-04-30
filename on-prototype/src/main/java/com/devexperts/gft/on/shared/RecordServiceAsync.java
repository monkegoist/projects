package com.devexperts.gft.on.shared;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;

public interface RecordServiceAsync {
    void registerSession(int instrumentsCount, AsyncCallback<String> async);

    void getInitialData(String key, AsyncCallback<List<Record>> async);

    void getUpdates(String key, int updatesCount, AsyncCallback<List<Record>> async);
}
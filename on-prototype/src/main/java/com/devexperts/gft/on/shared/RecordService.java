package com.devexperts.gft.on.shared;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import java.util.List;

@RemoteServiceRelativePath(RecordService.SERVLET_NAME)
public interface RecordService extends RemoteService {
    static final String SERVLET_NAME = "RecordService";

    String registerSession(int instrumentsCount);

    List<Record> getInitialData(String key);

    List<Record> getUpdates(String key, int updatesCount);
}
package com.devexperts.gft.on.server;

import com.devexperts.gft.on.shared.Record;
import com.devexperts.gft.on.shared.RecordService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import javax.servlet.annotation.WebServlet;
import java.util.List;

@SuppressWarnings("GwtServiceNotRegistered")
@WebServlet(name = RecordService.SERVLET_NAME, value = "/ONPrototype/" + RecordService.SERVLET_NAME)
public class RecordServiceImpl extends RemoteServiceServlet implements RecordService {

    @Override
    public String registerSession(int instrumentsCount) {
        return SessionRecordProvider.getInstance().registerSession(instrumentsCount);
    }

    @Override
    public List<Record> getInitialData(String key) {
        return SessionRecordProvider.getInstance().getInitialData(key);
    }

    @Override
    public List<Record> getUpdates(String key, int updatesCount) {
        return SessionRecordProvider.getInstance().getUpdates(key, updatesCount);
    }
}
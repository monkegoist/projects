package com.devexperts.gft.on.client.presenter;

import com.devexperts.gft.on.client.AppConstants;
import com.devexperts.gft.on.client.AppSettings;
import com.devexperts.gft.on.client.view.GWTView;
import com.devexperts.gft.on.shared.Record;
import com.devexperts.gft.on.shared.RecordService;
import com.devexperts.gft.on.shared.RecordServiceAsync;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.DefaultCellTableBuilder;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.view.client.ListDataProvider;

import java.util.*;

/**
 * GWT widget presenter.
 */
public class GWTPresenter implements Presenter {
    /**
     * View abstraction.
     */
    @SuppressWarnings("UnusedDeclaration")
    public interface Display {
        void setPresenter(GWTPresenter presenter);

        CellTable<Record> getCellTable();

        void setRowClickHandler(FieldUpdater<Record, ImageResource> rowClickHandler);
    }

    private final AppConstants constants = GWT.create(AppConstants.class);
    private final RecordServiceAsync recordService = GWT.create(RecordService.class);

    private final Map<Integer, List<Record>> recordsMap = new HashMap<Integer, List<Record>>();
    private final Set<Integer> expandedNodes = new HashSet<Integer>();

    private final ListDataProvider<Record> dataStore = new ListDataProvider<Record>();
    private final AppSettings settings;
    private final GWTView view;

    public GWTPresenter(AppSettings settings, GWTView view) {
        this.settings = settings;
        this.view = view;
    }

    @Override
    public void bind() {
        view.setPresenter(this);

        final CellTable<Record> cellTable = view.getCellTable();
        cellTable.setPageSize(settings.getInstrumentsCount());
        cellTable.setTableBuilder(new DefaultCellTableBuilder<Record>(cellTable) {
            @Override
            public void buildRowImpl(Record rowValue, int absRowIndex) {
                super.buildRowImpl(rowValue, absRowIndex);
                // we're only interested in such cases
                if (rowValue.getUnderlyingId() == null && expandedNodes.contains(rowValue.getInstrument().getId())) {
                    for (Record child : recordsMap.get(rowValue.getInstrument().getId())) {
                        super.buildRowImpl(child, absRowIndex);
                    }
                }
            }
        });

        dataStore.addDataDisplay(cellTable);

        view.setRowClickHandler(new FieldUpdater<Record, ImageResource>() {
            @Override
            public void update(int index, Record object, ImageResource value) {
                Integer underlying = object.getUnderlyingId();
                // we're only interested in such cases
                if (underlying == null) {
                    int instrumentId = object.getInstrument().getId();
                    if (expandedNodes.contains(instrumentId)) {
                        expandedNodes.remove(instrumentId);
                    } else {
                        expandedNodes.add(instrumentId);
                    }
                    view.getCellTable().redrawRow(index);
                }
            }
        });

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
                        // filter out underlying instruments
                        List<Record> uns = new ArrayList<Record>();
                        Iterator<Record> iterator = result.iterator();
                        while (iterator.hasNext()) {
                            Record record = iterator.next();
                            if (record.getUnderlyingId() == null) {
                                uns.add(record);
                                iterator.remove();
                            }
                        }
                        // map other records to corresponding underlying instruments
                        for (Record record : uns) {
                            List<Record> children = new ArrayList<Record>();
                            for (Record child : result) {
                                if (child.getUnderlyingId() == record.getInstrument().getId()) {
                                    children.add(child);
                                }
                            }
                            recordsMap.put(record.getInstrument().getId(), children);
                        }
                        // display records on underlying instruments
                        Collections.sort(uns, new Comparator<Record>() {
                            @Override
                            public int compare(Record first, Record second) {
                                return first.getInstrument().getSymbol().compareTo(second.getInstrument().getSymbol());
                            }
                        });
                        for (Record record : uns) {
                            dataStore.getList().add(record);
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
                            if (record.getUnderlyingId() == null) {
                                updateRecord(record);
                            } else {
                                List<Record> existingRecords = recordsMap.get(record.getUnderlyingId());
                                for (int i = 0; i < existingRecords.size(); i++) {
                                    Record child = existingRecords.get(i);
                                    if (child.getInstrument().equals(record.getInstrument())) {
                                        existingRecords.set(i, record);
                                        break;
                                    }
                                }
                                if (expandedNodes.contains(record.getUnderlyingId())) {
                                    updateRecord(record);
                                }
                            }
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

    public boolean showIcon(int instrumentId) {
        return !recordsMap.get(instrumentId).isEmpty();
    }

    public boolean isExpanded(int instrumentId) {
        return expandedNodes.contains(instrumentId);
    }

    private void updateRecord(Record record) {
        List<Record> store = dataStore.getList();
        for (int i = 0; i < store.size(); i++) {
            Record candidate = store.get(i);
            if (candidate.getInstrument().equals(record.getInstrument())) {
                store.set(i, record);
                break;
            }
        }
    }
}
package com.devexperts.gft.on.client.view;

import com.devexperts.gft.on.client.AppConstants;
import com.devexperts.gft.on.client.presenter.GXTPresenter;
import com.devexperts.gft.on.client.util.UnmodifiableValueProvider;
import com.devexperts.gft.on.shared.Instrument;
import com.devexperts.gft.on.shared.Record;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.treegrid.TreeGrid;

import java.util.ArrayList;
import java.util.List;

import static com.devexperts.gft.on.client.AppConstants.PRICE_FORMAT;
import static com.devexperts.gft.on.client.AppConstants.VOLUME_FORMAT;

public class GXTView implements GXTPresenter.Display, IsWidget {

    @SuppressWarnings("FieldCanBeLocal")
    private final AppConstants constants = GWT.create(AppConstants.class);

    private final TreeGrid<Record> treeGrid;

    public GXTView() {
        TreeStore<Record> treeStore = new TreeStore<Record>(new KeyProvider());

        ColumnConfig<Record, String> instrumentColumn = new ColumnConfig<Record, String>(new UnmodifiableValueProvider<Record, String>(constants.instrumentColumnName()) {
            @Override
            public String getValue(Record record) {
                Instrument instrument = record.getInstrument();
                return instrument.getSymbol() + "." + instrument.getType() + ", " + instrument.getId();
            }
        });
        instrumentColumn.setHeader(constants.instrumentColumnName());

        ColumnConfig<Record, String> clientVolumeColumn = new ColumnConfig<Record, String>(new UnmodifiableValueProvider<Record, String>(constants.clientVolumeColumnName()) {
            @Override
            public String getValue(Record record) {
                return VOLUME_FORMAT.format(record.getClientVolume());
            }
        });
        clientVolumeColumn.setHeader(constants.clientVolumeColumnName());

        ColumnConfig<Record, String> clientPriceColumn = new ColumnConfig<Record, String>(new UnmodifiableValueProvider<Record, String>(constants.clientPriceColumnName()) {
            @Override
            public String getValue(Record record) {
                return PRICE_FORMAT.format(record.getClientPrice());
            }
        });
        clientPriceColumn.setHeader(constants.clientPriceColumnName());

        ColumnConfig<Record, String> clientPLColumn = new ColumnConfig<Record, String>(new UnmodifiableValueProvider<Record, String>(constants.clientPLColumnName()) {
            @Override
            public String getValue(Record record) {
                return VOLUME_FORMAT.format(record.getClientPL());
            }
        });
        clientPLColumn.setHeader(constants.clientPLColumnName());

        ColumnConfig<Record, String> brokerVolumeColumn = new ColumnConfig<Record, String>(new UnmodifiableValueProvider<Record, String>(constants.brokerVolumeColumnName()) {
            @Override
            public String getValue(Record record) {
                return VOLUME_FORMAT.format(record.getBrokerVolume());
            }
        });
        brokerVolumeColumn.setHeader(constants.brokerVolumeColumnName());

        ColumnConfig<Record, String> brokerPriceColumn = new ColumnConfig<Record, String>(new UnmodifiableValueProvider<Record, String>(constants.brokerPriceColumnName()) {
            @Override
            public String getValue(Record record) {
                return PRICE_FORMAT.format(record.getBrokerPrice());
            }
        });
        brokerPriceColumn.setHeader(constants.brokerPriceColumnName());

        ColumnConfig<Record, String> brokerPLColumn = new ColumnConfig<Record, String>(new UnmodifiableValueProvider<Record, String>(constants.brokerPLColumnName()) {
            @Override
            public String getValue(Record record) {
                return VOLUME_FORMAT.format(record.getBrokerPL());
            }
        });
        brokerPLColumn.setHeader(constants.brokerPLColumnName());

        ColumnConfig<Record, String> bidColumn = new ColumnConfig<Record, String>(new UnmodifiableValueProvider<Record, String>(constants.bidColumnName()) {
            @Override
            public String getValue(Record record) {
                return PRICE_FORMAT.format(record.getBid());
            }
        });
        bidColumn.setHeader(constants.bidColumnName());

        ColumnConfig<Record, String> askColumn = new ColumnConfig<Record, String>(new UnmodifiableValueProvider<Record, String>(constants.askColumnName()) {
            @Override
            public String getValue(Record record) {
                return PRICE_FORMAT.format(record.getAsk());
            }
        });
        askColumn.setHeader(constants.askColumnName());

        List<ColumnConfig<Record, ?>> columns = new ArrayList<ColumnConfig<Record, ?>>();
        columns.add(instrumentColumn);
        columns.add(clientVolumeColumn);
        columns.add(clientPriceColumn);
        columns.add(clientPLColumn);
        columns.add(brokerVolumeColumn);
        columns.add(brokerPriceColumn);
        columns.add(brokerPLColumn);
        columns.add(bidColumn);
        columns.add(askColumn);
        ColumnModel<Record> cm = new ColumnModel<Record>(columns);

        treeGrid = new TreeGrid<Record>(treeStore, cm, instrumentColumn);
        treeGrid.setBorders(true);
        treeGrid.getView().setAutoFill(true);
        treeGrid.getView().setStripeRows(true);
        treeGrid.getView().setColumnLines(true);
    }

    @Override
    public void addRecord(Record record) {
        if (record.getUnderlyingId() == null) {
            // root node here
            treeGrid.getTreeStore().add(record);
        } else {
            for (Record root : treeGrid.getTreeStore().getRootItems()) {
                if (record.getUnderlyingId() == root.getInstrument().getId()) {
                    treeGrid.getTreeStore().add(root, record);
                    break;
                }
            }
        }
    }

    @Override
    public void updateRecord(Record record) {
        if (record.getUnderlyingId() != null || !treeGrid.getTreeStore().hasChildren(record)) {
            // just update record
            treeGrid.getTreeStore().update(record);
        } else {
            // todo: find out what to do in this case...
        }
    }

    @Override
    public Widget asWidget() {
        return treeGrid;
    }

    private static class KeyProvider implements ModelKeyProvider<Record> {
        @Override
        public String getKey(Record record) {
            return String.valueOf(record.getInstrument().getId());
        }
    }
}
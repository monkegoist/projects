package com.devexperts.gft.on.client.view;

import com.devexperts.gft.on.client.AppConstants;
import com.devexperts.gft.on.client.presenter.GWTPresenter;
import com.devexperts.gft.on.client.util.ClickableImageResourceCell;
import com.devexperts.gft.on.shared.Instrument;
import com.devexperts.gft.on.shared.Record;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ProvidesKey;

import static com.devexperts.gft.on.client.AppConstants.PRICE_FORMAT;
import static com.devexperts.gft.on.client.AppConstants.VOLUME_FORMAT;

public class GWTView implements GWTPresenter.Display, IsWidget {

    @SuppressWarnings("FieldCanBeLocal")
    private final AppConstants constants = GWT.create(AppConstants.class);
    private final Tree.Resources images = GWT.create(Tree.Resources.class);

    private final CellTable<Record> cellTable;
    private final Column<Record, ImageResource> treeColumn;

    private GWTPresenter presenter;

    public GWTView() {
        cellTable = new CellTable<Record>(KEY_PROVIDER);
        cellTable.setWidth("100%", true);
        cellTable.setAutoHeaderRefreshDisabled(true);
        cellTable.setAutoFooterRefreshDisabled(true);

        treeColumn = new Column<Record, ImageResource>(new ClickableImageResourceCell()) {
            @Override
            public ImageResource getValue(Record record) {
                ImageResource image = null;
                // display icon for underlying instruments only
                if (record.getUnderlyingId() == null && presenter.showIcon(record.getInstrument().getId())) {
                    image = presenter.isExpanded(record.getInstrument().getId()) ? images.treeOpen() : images.treeClosed();
                }
                return image;
            }
        };
        cellTable.addColumn(treeColumn);
        cellTable.setColumnWidth(treeColumn, 5, Style.Unit.PCT);

        Column<Record, String> instrumentColumn = new Column<Record, String>(new TextCell()) {
            @Override
            public String getValue(Record record) {
                Instrument instrument = record.getInstrument();
                return instrument.getSymbol() + "." + instrument.getType() + ", " + instrument.getId();
            }
        };
        cellTable.addColumn(instrumentColumn, constants.instrumentColumnName());
        cellTable.setColumnWidth(instrumentColumn, 15, Style.Unit.PCT);

        Column<Record, String> clientVolumeColumn = new Column<Record, String>(new TextCell()) {
            @Override
            public String getValue(Record record) {
                return VOLUME_FORMAT.format(record.getClientVolume());
            }
        };
        cellTable.addColumn(clientVolumeColumn, constants.clientVolumeColumnName());
        cellTable.setColumnWidth(clientVolumeColumn, 10, Style.Unit.PCT);

        Column<Record, String> clientPriceColumn = new Column<Record, String>(new TextCell()) {
            @Override
            public String getValue(Record record) {
                return PRICE_FORMAT.format(record.getClientPrice());
            }
        };
        cellTable.addColumn(clientPriceColumn, constants.clientPriceColumnName());
        cellTable.setColumnWidth(clientPriceColumn, 10, Style.Unit.PCT);

        Column<Record, String> clientPLColumn = new Column<Record, String>(new TextCell()) {
            @Override
            public String getValue(Record record) {
                return VOLUME_FORMAT.format(record.getClientPL());
            }
        };
        cellTable.addColumn(clientPLColumn, constants.clientPLColumnName());
        cellTable.setColumnWidth(clientPLColumn, 10, Style.Unit.PCT);

        Column<Record, String> brokerVolumeColumn = new Column<Record, String>(new TextCell()) {
            @Override
            public String getValue(Record record) {
                return VOLUME_FORMAT.format(record.getBrokerVolume());
            }
        };
        cellTable.addColumn(brokerVolumeColumn, constants.brokerVolumeColumnName());
        cellTable.setColumnWidth(brokerVolumeColumn, 10, Style.Unit.PCT);

        Column<Record, String> brokerPriceColumn = new Column<Record, String>(new TextCell()) {
            @Override
            public String getValue(Record record) {
                return PRICE_FORMAT.format(record.getBrokerPrice());
            }
        };
        cellTable.addColumn(brokerPriceColumn, constants.brokerPriceColumnName());
        cellTable.setColumnWidth(brokerPriceColumn, 10, Style.Unit.PCT);

        Column<Record, String> brokerPLColumn = new Column<Record, String>(new TextCell()) {
            @Override
            public String getValue(Record record) {
                return VOLUME_FORMAT.format(record.getBrokerPL());
            }
        };
        cellTable.addColumn(brokerPLColumn, constants.brokerPLColumnName());
        cellTable.setColumnWidth(brokerPLColumn, 10, Style.Unit.PCT);

        Column<Record, String> bidColumn = new Column<Record, String>(new TextCell()) {
            @Override
            public String getValue(Record record) {
                return PRICE_FORMAT.format(record.getBid());
            }
        };
        cellTable.addColumn(bidColumn, constants.bidColumnName());
        cellTable.setColumnWidth(bidColumn, 10, Style.Unit.PCT);

        Column<Record, String> askColumn = new Column<Record, String>(new TextCell()) {
            @Override
            public String getValue(Record record) {
                return PRICE_FORMAT.format(record.getAsk());
            }
        };
        cellTable.addColumn(askColumn, constants.askColumnName());
        cellTable.setColumnWidth(askColumn, 10, Style.Unit.PCT);
    }

    @Override
    public void setPresenter(GWTPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public CellTable<Record> getCellTable() {
        return cellTable;
    }

    @Override
    public void setRowClickHandler(FieldUpdater<Record, ImageResource> rowClickHandler) {
        treeColumn.setFieldUpdater(rowClickHandler);
    }

    @Override
    public Widget asWidget() {
        return cellTable;
    }

    private static final ProvidesKey<Record> KEY_PROVIDER = new ProvidesKey<Record>() {
        @Override
        public Object getKey(Record item) {
            return item == null ? null : item.getInstrument().getId();
        }
    };
}
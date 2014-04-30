package com.devexperts.gft.on.client.view;

import com.devexperts.gft.on.client.AppConstants;
import com.devexperts.gft.on.client.presenter.GWTPresenter;
import com.devexperts.gft.on.shared.Instrument;
import com.devexperts.gft.on.shared.Record;
import com.google.gwt.cell.client.*;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.ImageResourceRenderer;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.devexperts.gft.on.client.AppConstants.PRICE_FORMAT;
import static com.devexperts.gft.on.client.AppConstants.VOLUME_FORMAT;

public class GWTView implements GWTPresenter.Display, IsWidget {

    @SuppressWarnings("FieldCanBeLocal")
    private final AppConstants constants = GWT.create(AppConstants.class);

    private final CellTable<Record> cellTable;
    private final ListDataProvider<Record> dataProvider;
    private final Column<Record, String> instrumentColumn;

    private final Set<Integer> expanded = new HashSet<Integer>();

    private FieldUpdater<Record, String> mainFieldUpdater;

    public GWTView() {
        cellTable = new CellTable<Record>(KEY_PROVIDER);
        cellTable.setWidth("100%", true);
        cellTable.setAutoHeaderRefreshDisabled(true);
        cellTable.setAutoFooterRefreshDisabled(true);

        // todo: add sorting
        // todo: move list data provider to presenter, operate on it directly
        // todo: handle enter key pressed event

        final Tree.Resources resources = GWT.create(Tree.Resources.class);
        Column<Record, ImageResource> imageResourceColumn = new Column<Record, ImageResource>(new ClickableImageResourceCell()) {
            @Override
            public ImageResource getValue(Record object) {
                if (object.getUnderlyingId() == null) {
                    return expanded.contains(object.getInstrument().getId()) ? resources.treeOpen() : resources.treeClosed();
                } else {
                    return null;
                }
            }

            @Override
            public void onBrowserEvent(Cell.Context context, Element elem, Record object, NativeEvent event) {
                super.onBrowserEvent(context, elem, object, event);
                if ("click".equals(event.getType())) {
                    getFieldUpdater().update(context.getIndex(), object, null);
                }
            }
        };
        imageResourceColumn.setFieldUpdater(new FieldUpdater<Record, ImageResource>() {
            @Override
            public void update(int index, Record object, ImageResource value) {
                if (expanded.contains(object.getInstrument().getId())) {
                    expanded.remove(object.getInstrument().getId());
                } else {
                    expanded.add(object.getInstrument().getId());
                }
                mainFieldUpdater.update(index, object, null);
                cellTable.redrawRow(index);
            }
        });
        cellTable.addColumn(imageResourceColumn);
        cellTable.setColumnWidth(imageResourceColumn, 5, Style.Unit.PCT);

        instrumentColumn = new Column<Record, String>(new ClickableTextCell()) {
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

        dataProvider = new ListDataProvider<Record>();
        dataProvider.addDataDisplay(cellTable);
    }

    @Override
    public void addRecord(Record record) {
        dataProvider.getList().add(record);
    }

    @Override
    public void updateRecord(Record record) {
        List<Record> store = dataProvider.getList();
        for (int i = 0; i < store.size(); i++) {
            Record candidate = store.get(i);
            if (candidate.getInstrument().equals(record.getInstrument())) {
                store.set(i, record);
                break;
            }
        }
    }

    @Override
    public CellTable<Record> getCellTable() {
        return cellTable;
    }

    @Override
    public void setFieldUpdater(FieldUpdater<Record, String> fieldUpdater) {
//        instrumentColumn.setFieldUpdater(fieldUpdater);
        mainFieldUpdater = fieldUpdater;
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

    private static class ClickableImageResourceCell extends AbstractCell<ImageResource> {
        private static ImageResourceRenderer renderer;

        /**
         * Construct a new ImageResourceCell.
         */
        public ClickableImageResourceCell() {
            super("click");
            if (renderer == null) {
                renderer = new ImageResourceRenderer();
            }
        }

        @Override
        public void render(Context context, ImageResource value, SafeHtmlBuilder sb) {
            if (value != null) {
                sb.append(renderer.render(value));
            }
        }
    }
}
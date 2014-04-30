package com.devexperts.gft.on.client.view;

import com.devexperts.gft.on.client.AppConstants;
import com.devexperts.gft.on.client.presenter.RootPresenter;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.core.client.util.Padding;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.Viewport;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.NumberField;
import com.sencha.gxt.widget.core.client.form.NumberPropertyEditor;

import static com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;

// todo: replace this with UiBinder
public class RootView implements RootPresenter.Display, IsWidget {

    @SuppressWarnings("FieldCanBeLocal")
    private final AppConstants constants = GWT.create(AppConstants.class);

    private final Viewport viewport;

    private final NumberField<Integer> instrumentsField;
    private final NumberField<Integer> periodField;
    private final NumberField<Integer> updatesField;

    private final TextButton gwtButton;
    private final TextButton gxtButton;

    public RootView() {
        viewport = new Viewport();

        VBoxLayoutContainer mainContainer = new VBoxLayoutContainer();
        mainContainer.setPadding(new Padding(5));
        mainContainer.setVBoxLayoutAlign(VBoxLayoutContainer.VBoxLayoutAlign.CENTER);

        instrumentsField = new NumberField<Integer>(new NumberPropertyEditor.IntegerPropertyEditor());
        instrumentsField.setAllowBlank(false);
        instrumentsField.setAllowNegative(false);
        FieldLabel instrumentsLabel = new FieldLabel(instrumentsField, constants.instrumentsCount());
        adjustLabel(instrumentsLabel);
        mainContainer.add(instrumentsLabel, new BoxLayoutData(new Margins(20, 0, 10, 0)));

        periodField = new NumberField<Integer>(new NumberPropertyEditor.IntegerPropertyEditor());
        periodField.setAllowBlank(false);
        periodField.setAllowNegative(false);
        FieldLabel periodLabel = new FieldLabel(periodField, constants.pollingPeriod());
        adjustLabel(periodLabel);
        mainContainer.add(periodLabel, new BoxLayoutData(new Margins(0, 0, 10, 0)));

        updatesField = new NumberField<Integer>(new NumberPropertyEditor.IntegerPropertyEditor());
        updatesField.setAllowBlank(false);
        updatesField.setAllowNegative(false);
        FieldLabel updatesLabel = new FieldLabel(updatesField, constants.updatesCount());
        adjustLabel(updatesLabel);
        mainContainer.add(updatesLabel, new BoxLayoutData(new Margins(0, 0, 20, 0)));

        // set default values
        instrumentsField.setValue(50);
        periodField.setValue(500);
        updatesField.setValue(20);

        HBoxLayoutContainer buttonsContainer = new HBoxLayoutContainer(HBoxLayoutContainer.HBoxLayoutAlign.MIDDLE);
        gwtButton = new TextButton("Show me some GWT");
        gxtButton = new TextButton("Show me some GXT");
        buttonsContainer.add(gwtButton, new BoxLayoutData(new Margins(0, 20, 0, 0)));
        buttonsContainer.add(gxtButton);
        buttonsContainer.setPack(BoxLayoutContainer.BoxLayoutPack.CENTER);
        mainContainer.add(buttonsContainer);

        viewport.add(mainContainer);
    }

    private void adjustLabel(FieldLabel label) {
        label.setLabelPad(50);
        label.setLabelWordWrap(false);
        label.setWidth(300);
    }

    @Override
    public Widget asWidget() {
        return viewport;
    }

    @Override
    public boolean isDataValid() {
        return instrumentsField.validate() & periodField.validate() & updatesField.validate();
    }

    @Override
    public int getInstrumentsCount() {
        return instrumentsField.getValue();
    }

    @Override
    public int getPollingPeriod() {
        return periodField.getValue();
    }

    @Override
    public int getUpdatesCount() {
        return updatesField.getValue();
    }

    @Override
    public void addGwtHandler(SelectEvent.SelectHandler selectHandler) {
        gwtButton.addSelectHandler(selectHandler);
    }

    @Override
    public void addGxtHandler(SelectEvent.SelectHandler selectHandler) {
        gxtButton.addSelectHandler(selectHandler);
    }
}
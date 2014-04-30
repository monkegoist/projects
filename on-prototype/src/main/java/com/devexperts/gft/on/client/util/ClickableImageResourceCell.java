package com.devexperts.gft.on.client.util;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.ImageResourceRenderer;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;
import static com.google.gwt.dom.client.BrowserEvents.KEYDOWN;

/**
 * Copy-paste of {@link com.google.gwt.cell.client.ImageResourceCell} that reacts to 'click' events.
 */
public class ClickableImageResourceCell extends AbstractCell<ImageResource> {

    private static ImageResourceRenderer renderer;

    public ClickableImageResourceCell() {
        super(CLICK, KEYDOWN);
        if (renderer == null) {
            renderer = new ImageResourceRenderer();
        }
    }

    @Override
    public void onBrowserEvent(Context context, Element parent, ImageResource value, NativeEvent event,
                               ValueUpdater<ImageResource> valueUpdater) {
        super.onBrowserEvent(context, parent, value, event, valueUpdater);
        if (CLICK.equals(event.getType())) {
            onEnterKeyDown(context, parent, value, event, valueUpdater);
        }
    }

    @Override
    protected void onEnterKeyDown(Context context, Element parent, ImageResource value, NativeEvent event,
                                  ValueUpdater<ImageResource> valueUpdater) {
        if (valueUpdater != null) {
            valueUpdater.update(value);
        }
    }

    @Override
    public void render(Cell.Context context, ImageResource value, SafeHtmlBuilder sb) {
        if (value != null) {
            sb.append(renderer.render(value));
        }
    }
}

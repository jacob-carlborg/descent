package com.hexapixel.framework.glazed.griddata;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

public abstract class AbstractDataObject implements IDataObject {

    @Override
    public Color getBackground(int index, boolean even) {
        return null;
    }

    @Override
    public Image getColumnImage(int index) {
        return null;
    }

    @Override
    public abstract String getColumnText(int index);

    @Override
    public String getFilterMatchText() {
        return null;
    }

    @Override
    public Font getFont(int index) {
        return null;
    }

    @Override
    public Color getForeground(int index, boolean even) {
        return null;
    }

    @Override
    public Color getHighlightBackgroundColor(int column) {
        return null;
    }

    @Override
    public Color getHighlightForegroundColor(int column) {
        return null;
    }

    @Override
    public String getSearchMatchText(int column) {
        return getColumnText(column);
    }

}

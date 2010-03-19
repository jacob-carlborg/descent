package com.hexapixel.framework.glazed.glazednatgridviewer.nat;

import net.sourceforge.nattable.typeconfig.style.DefaultStyleConfig;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

import com.hexapixel.framework.glazed.glazednatgridviewer.GlazedNatGridViewer;

public class NatCellStyleConfig extends DefaultStyleConfig {

    private static final long   serialVersionUID = 3896762088992667011L;

    private GlazedNatGridViewer _viewer;
    private NatCellRenderer     _renderer;

    public NatCellStyleConfig(NatCellRenderer renderer, GlazedNatGridViewer viewer) {
        super();
        _viewer = viewer;
        _renderer = renderer;
    }

    @Override
    public Color getBackgroundColor(int row, int col) {
        String val = _renderer.getValue(row, col).toString();
        if (_viewer.getHighlightText() != null) {
            if (_viewer.isCaseSensitiveHighlight()) {
                if (val.indexOf(_viewer.getHighlightText()) > -1) { return _viewer.getVisualConfig()
                        .getHighlightBackgroundColor(); }
            } else {
                if (val.toLowerCase().indexOf(_viewer.getHighlightText().toLowerCase()) > -1) { return _viewer
                        .getVisualConfig().getHighlightBackgroundColor(); }
            }
        }

        if (row % 2 == 0) return _viewer.getVisualConfig().getStripedBackgroundColor();

        return _viewer.getVisualConfig().getCellBackgroundColor();
    }

    @Override
    public Font getFont(int row, int col) {
        return _viewer.getVisualConfig().getCellFont();
    }

    @Override
    public Color getForegroundColor(int row, int col) {
        String val = _renderer.getValue(row, col).toString();
        if (_viewer.getHighlightText() != null) {
            if (_viewer.isCaseSensitiveHighlight()) {
                if (val.indexOf(_viewer.getHighlightText()) > -1) { return _viewer.getVisualConfig()
                        .getHighlightForegroundColor(); }
            } else {
                if (val.toLowerCase().indexOf(_viewer.getHighlightText().toLowerCase()) > -1) { return _viewer
                        .getVisualConfig().getHighlightForegroundColor(); }
            }
        }

        if (row % 2 == 0) return _viewer.getVisualConfig().getStripedForegroundColor();

        return _viewer.getVisualConfig().getCellForegroundColor();
    }

    @Override
    public Image getImage(int row, int col) {
        return super.getImage(row, col);
    }

}

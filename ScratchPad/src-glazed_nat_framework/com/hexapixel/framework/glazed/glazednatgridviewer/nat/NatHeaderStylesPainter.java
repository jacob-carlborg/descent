package com.hexapixel.framework.glazed.glazednatgridviewer.nat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

import com.hexapixel.framework.glazed.glazednatgridviewer.GlazedNatColorCache;
import com.hexapixel.framework.glazed.glazednatgridviewer.GlazedNatGridViewer;
import com.hexapixel.framework.glazed.glazednatgridviewer.NatGridViewerColumn;

public class NatHeaderStylesPainter {

    private static Color _windowsNonSelTop         = GlazedNatColorCache.getColor(235, 234, 219);
    private static Color _windowsNonSelGradient1   = GlazedNatColorCache.getColor(226, 222, 205);
    private static Color _windowsNonSelGradient2   = GlazedNatColorCache.getColor(214, 210, 194);
    private static Color _windowsNonSelGradient3   = GlazedNatColorCache.getColor(203, 199, 184);

    private static Color _windowsSelTop            = GlazedNatColorCache.getColor(250, 248, 243);
    private static Color _windowsSelGradient1      = GlazedNatColorCache.getColor(249, 169, 0);
    private static Color _windowsSelGradient2      = GlazedNatColorCache.getColor(246, 196, 86);
    private static Color _windowsSelGradient3      = GlazedNatColorCache.getColor(248, 179, 31);

    private static Color _windowsMultiSelGradient1 = GlazedNatColorCache.getColor(0, 70, 249);
    private static Color _windowsMultiSelGradient2 = GlazedNatColorCache.getColor(86, 130, 246);
    private static Color _windowsMultiSelGradient3 = GlazedNatColorCache.getColor(31, 92, 248);

    private static Color _borderLeftNonSel         = GlazedNatColorCache.getColor(199, 197, 178);
    private static Color _borderRightNonSel        = GlazedNatColorCache.getWhite();

    private static Color _outerBorder              = Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_DARK_SHADOW);
    private static Color _innerBorder              = Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW);
    private static Color _innerLightBorder         = GlazedNatColorCache.getWhite();
    private static Color _innerBgColor             = Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);

    public static void drawWindowsClassicColumnBackground(GlazedNatGridViewer viewer, GC gc, Rectangle rectangle, boolean multiSort) {
        Color currentFg = gc.getForeground();
        Color currentBg = gc.getBackground();

        NatGridViewerColumn col = viewer.getColumn(rectangle);
        boolean firstColumn = rectangle.x == 1;
        boolean lastColumn = viewer.isLastColumn(col);

        gc.setBackground(_innerBgColor);

        // empty column on right's bounds
        Rectangle lastColRect = new Rectangle(rectangle.x + rectangle.width, rectangle.y, viewer.getGrid().getClientArea().width
                - (rectangle.x + rectangle.width), rectangle.height);

        if (lastColumn) {
            gc.fillRectangle(lastColRect);
        }

        gc.setBackground(_innerBgColor);
        if (multiSort) {
            gc.setBackground(viewer.getVisualConfig().getColumnHeaderMultisortTopGradient());
        }

        // if (!firstColumn) {
        // kill border
        gc.fillRectangle(new Rectangle(rectangle.x, rectangle.y, rectangle.width + 1, rectangle.height));
        // }

        // draw outer border left and top
        rectangle.width -= 2;
        gc.setForeground(_outerBorder);
        if (firstColumn) {
            gc.drawLine(rectangle.x, rectangle.y, rectangle.x, rectangle.y + rectangle.height);
        } else {
            gc.drawLine(rectangle.x - 2, rectangle.y, rectangle.x - 2, rectangle.y + rectangle.height);
        }

        int bonus = 2;

        if (lastColumn) {
            bonus = 1;
            gc.drawLine(rectangle.x + rectangle.width + bonus, rectangle.y, rectangle.x + rectangle.width + bonus, rectangle.y + rectangle.height);
        }
        // top
        gc.drawLine(rectangle.x, rectangle.y, rectangle.x + rectangle.width + bonus, rectangle.y);

        // draw inner border, the highlight
        gc.setForeground(_innerLightBorder);
        if (!firstColumn) { // top
            gc.drawLine(rectangle.x - 1, rectangle.y + 1, rectangle.x + rectangle.width, rectangle.y + 1); // left
            gc.drawLine(rectangle.x - 1, rectangle.y + 1, rectangle.x - 1, rectangle.y + rectangle.height - 1);
        } else {
            // first column // top
            gc.drawLine(rectangle.x + 1, rectangle.y + 1, rectangle.x + rectangle.width, rectangle.y + 1); // left
            gc.drawLine(rectangle.x + 1, rectangle.y + 1, rectangle.x + 1, rectangle.y + rectangle.height - 1);
        }

        // draw other inner border, the darker one
        gc.setForeground(_innerBorder);
        if (!firstColumn) { // right
            gc.drawLine(rectangle.x + rectangle.width, rectangle.y + 2, rectangle.x + rectangle.width, rectangle.height); // bottom
            gc.drawLine(rectangle.x, rectangle.y + rectangle.height - 2, rectangle.x + rectangle.width, rectangle.y + rectangle.height - 2);
        } else { // right
            gc.drawLine(rectangle.x + rectangle.width, rectangle.y + 2, rectangle.x + rectangle.width, rectangle.height); // bottom
            gc.drawLine(rectangle.x + 2, rectangle.y + rectangle.height - 2, rectangle.x + rectangle.width, rectangle.y + rectangle.height - 2);
        }

        // bottom line
        gc.setForeground(_outerBorder);
        bonus = 2;
        if (lastColumn)
            bonus = 1;

        // cover bottom up with a line across
        gc.drawLine(rectangle.x - (firstColumn ? 0 : 2), rectangle.y+rectangle.height-1, rectangle.x + rectangle.width + bonus, rectangle.y+rectangle.height-1);
        
        drawSortNumber(viewer, gc, viewer.getColumn(rectangle), rectangle);

        gc.setForeground(currentFg);
        gc.setBackground(currentBg);
    }

    /**
     * Draws a column header as if it was Windows XP.
     * 
     * @param viewer
     * @param gc
     * @param rectangle
     * @param multiSort
     */
    public static void drawWindowsColumnBackground(GlazedNatGridViewer viewer, GC gc, Rectangle rectangle, boolean multiSort) {
        Color currentFg = gc.getForeground();
        Color currentBg = gc.getBackground();

        NatGridViewerColumn col = viewer.getColumn(rectangle);

        boolean hover = col.isHoverColumn();
        // boolean hoverSelect = col.isHoverSelectColumn();

        gc.setBackground(_windowsNonSelTop);

        Rectangle lastColRect = new Rectangle(rectangle.x + rectangle.width, rectangle.y, viewer.getGrid().getClientArea().width
                - (rectangle.x + rectangle.width), rectangle.height);
        boolean lastColumn = viewer.isLastColumn(col);

        if (lastColumn) {
            gc.fillRectangle(lastColRect);
        }

        if (multiSort) {
            gc.setBackground(_windowsNonSelTop);
        } else {
            if (!hover) {
                gc.setBackground(_windowsNonSelTop);
            } else {
                gc.setBackground(_windowsSelTop);
            }
        }

        rectangle.width += 1; // kill the border
        gc.fillRectangle(rectangle);

        if (multiSort) {
            gc.setForeground(_windowsMultiSelGradient1);
        } else {
            if (!hover) {
                gc.setForeground(_windowsNonSelGradient1);
            } else {
                gc.setForeground(_windowsSelGradient1);
            }
        }
        gc.drawLine(rectangle.x, rectangle.y + rectangle.height - 3, rectangle.x + rectangle.width, rectangle.y + rectangle.height - 3);
        if (lastColumn) {
            gc.setForeground(_windowsNonSelGradient1);
            gc.drawLine(lastColRect.x, lastColRect.y + lastColRect.height - 3, lastColRect.x + lastColRect.width, lastColRect.y + lastColRect.height
                    - 3);
        }

        if (multiSort) {
            gc.setForeground(_windowsMultiSelGradient2);
        } else {
            if (!hover) {
                gc.setForeground(_windowsNonSelGradient2);
            } else {
                gc.setForeground(_windowsSelGradient2);
            }
        }
        gc.drawLine(rectangle.x, rectangle.y + rectangle.height - 2, rectangle.x + rectangle.width, rectangle.y + rectangle.height - 2);
        if (lastColumn) {
            gc.setForeground(_windowsNonSelGradient2);
            gc.drawLine(lastColRect.x, lastColRect.y + lastColRect.height - 2, lastColRect.x + lastColRect.width, lastColRect.y + lastColRect.height
                    - 2);
        }

        if (multiSort) {
            gc.setForeground(_windowsMultiSelGradient3);
        } else {
            if (!hover) {
                gc.setForeground(_windowsNonSelGradient3);
            } else {
                gc.setForeground(_windowsSelGradient3);
            }
        }
        gc.drawLine(rectangle.x, rectangle.y + rectangle.height - 1, rectangle.x + rectangle.width, rectangle.y + rectangle.height - 1);
        if (lastColumn) {
            gc.setForeground(_windowsNonSelGradient3);
            gc.drawLine(lastColRect.x, lastColRect.y + lastColRect.height - 1, lastColRect.x + lastColRect.width, lastColRect.y + lastColRect.height
                    - 1);
        }

        if (!hover) {
            gc.setForeground(_borderLeftNonSel);
            gc.drawLine(rectangle.x + rectangle.width - 3, rectangle.y + 3, rectangle.x + rectangle.width - 3, rectangle.y + rectangle.height - 5);
            gc.setForeground(_borderRightNonSel);
            gc.drawLine(rectangle.x + rectangle.width - 2, rectangle.y + 3, rectangle.x + rectangle.width - 2, rectangle.y + rectangle.height - 5);
        }

        gc.setForeground(GlazedNatColorCache.getWhite());
        gc.drawLine(rectangle.x, rectangle.y, rectangle.x + rectangle.width, rectangle.y);

        drawSortNumber(viewer, gc, col, rectangle);

        gc.setForeground(currentFg);
        gc.setBackground(currentBg);
    }

    // draws the multisort number on the left side of the column
    private static void drawSortNumber(GlazedNatGridViewer viewer, GC gc, NatGridViewerColumn col, Rectangle rectangle) {
        if (col == null) return;

        int msNumber = viewer.getMultiSortOrder(col);
        if (msNumber != -1) {
            msNumber++;
            gc.setForeground(GlazedNatColorCache.getColor(255, 0, 0));
            gc.drawString("" + msNumber, rectangle.x + 1, rectangle.y + 1, true);
        }
    }

}

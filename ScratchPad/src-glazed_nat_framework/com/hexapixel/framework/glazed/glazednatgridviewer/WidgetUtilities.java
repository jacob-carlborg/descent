package com.hexapixel.framework.glazed.glazednatgridviewer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * Class that contains various utilities for centering a shell on the window, packing table columns, etc.
 * 
 * @author cre
 *
 */
public class WidgetUtilities {

    /**
     * Centers a shell on the current monitor.
     * 
     * @param shell Shell to center
     */
    public static void centerShell(Shell shell) {
        try {
            Monitor primary = Display.getDefault().getPrimaryMonitor();
            Rectangle bounds = primary.getBounds();
            Rectangle rect = shell.getBounds();
            int x = bounds.x + (bounds.width - rect.width) / 2;
            int y = bounds.y + (bounds.height - rect.height) / 2;
            shell.setLocation(x, y);
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    /**
     * Packs all table columns so that they all share the available width of the table.
     * If there's space left over, the last column will be given all of the extra space. 
     * 
     * @param table Table to pack
     */
    public static void smartPackAndFillLastCol(Table table) {
        try {
            smartPack(table);

            int cols = table.getColumnCount();

            if (cols < 1) return;

            TableColumn last = table.getColumn(cols - 1);

            Rectangle area = table.getClientArea();

            Point pref = table.computeSize(SWT.DEFAULT, SWT.DEFAULT);
            int width = area.width;
            if (pref.y > area.height) {
                Point vBarSize = table.getVerticalBar().getSize();
                width -= vBarSize.x;
            }

            int totColSpace = 0;
            for (TableColumn tc : table.getColumns())
                totColSpace += tc.getWidth();

            int remainder = width - totColSpace;
            if (remainder < 0) return;

            last.setWidth(last.getWidth() + remainder);

        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    /**
     * Smartly packs table columns so that they all header text is visible.
     * 
     * @param table Table to pack
     */
    public static void smartPack(Table table) {
        try {
            GC gc = new GC(table);
            table.setRedraw(false);

            if (table.getItemCount() == 0) {
                for (int i = 0; i < table.getColumnCount(); i++) {
                    String text = table.getColumn(i).getText();
                    Point width = gc.stringExtent(text);
                    table.getColumn(i).setWidth(width.x + 25);
                }
            } else {
                for (int i = 0; i < table.getColumnCount(); i++) {
                    String text = table.getColumn(i).getText();
                    table.getColumn(i).pack();
                    int widthAfterPack = table.getColumn(i).getWidth();
                    Point width = gc.stringExtent(text);
                    if ((width.x + 25) > widthAfterPack) {
                        table.getColumn(i).setWidth(width.x + 25);
                    }
                }
            }

            table.setRedraw(true);
            gc.dispose();
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    /**
     * Makes all columns in the table take up the equal amount of width depending on how
     * wide the table is. This method must be called after the widget has created or the 
     * table width will not have been calculated.
     * 
     * @param table Table to pack columns
     */
    public static void makeColumnsEqualWidth(Table table) {
        try {
            int cols = table.getColumnCount();

            Rectangle area = table.getClientArea();

            if (area.width == 0) {
                area = table.getParent().getClientArea();
                if (area.width == 0) {
                    for (int i = 0; i < table.getColumnCount(); i++) {
                        table.getColumn(i).pack();
                    }
                    return;
                }
            }

            Point pref = table.computeSize(SWT.DEFAULT, SWT.DEFAULT);
            int width = area.width;// - (2 * table.getBorderWidth());
            if (pref.y > area.height) {
                Point vBarSize = table.getVerticalBar().getSize();
                width -= vBarSize.x;
            }

            int countCols = cols;

            for (int i = 0; i < cols; i++) {
                float w = (float) width / (float) countCols;
                table.getColumn(i).setWidth((int) w);
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

}
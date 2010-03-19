package com.hexapixel.framework.glazed.glazednatgridviewer.nat;

import net.sourceforge.nattable.NatTable;
import net.sourceforge.nattable.data.IDataProvider;
import net.sourceforge.nattable.model.INatTableModel;
import net.sourceforge.nattable.painter.cell.ICellPainter;
import net.sourceforge.nattable.painter.cell.TextCellPainter;
import net.sourceforge.nattable.renderer.AbstractCellRenderer;
import net.sourceforge.nattable.renderer.ICellRenderer;
import net.sourceforge.nattable.typeconfig.style.DefaultStyleConfig;
import net.sourceforge.nattable.typeconfig.style.DisplayModeEnum;
import net.sourceforge.nattable.typeconfig.style.IStyleConfig;
import net.sourceforge.nattable.util.GUIHelper;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import com.hexapixel.framework.glazed.glazednatgridviewer.GlazedNatGridViewer;

public class NatCellRenderer extends AbstractCellRenderer {

    DefaultStyleConfig          _selectStyleConfig;
    NatCellStyleConfig          _defaultStyleConfig;

    private IDataProvider       _provider;
    private GlazedNatGridViewer _viewer;

    private AlignedPainter      _cellPainter = new AlignedPainter();

    public NatCellRenderer(GlazedNatGridViewer parent, IDataProvider provider) {
        this._provider = provider;
        _viewer = parent;

        _defaultStyleConfig = new NatCellStyleConfig(this, parent);
        _selectStyleConfig = new DefaultStyleConfig(_viewer.getVisualConfig().getSelectionBackgroundColor(), _viewer
                .getVisualConfig().getSelectionForegroundColor(), null, null);
    }

    @Override
    public String getDisplayText(int row, int col) {
        return _provider.getValue(row, col).toString();
    }

    @Override
    public Object getValue(int row, int col) {
        return _provider.getValue(row, col);
    }

    @Override
    public IStyleConfig getStyleConfig(String displayMode, int row, int col) {
        if (DisplayModeEnum.valueOf(displayMode).equals(DisplayModeEnum.SELECT)) {
            return _selectStyleConfig;
        } else {
            return _defaultStyleConfig;
        }

    }

    @Override
    public ICellPainter getCellPainter(int row, int col) {
        return _cellPainter;
    }

    // yet another override just so that we can align our cells, looks like in 1.7 we can align via IConfigStyles
    // until then, this works fine
    class AlignedPainter extends TextCellPainter {

        int alignment = 0;

        @Override
        public void drawCell(GC gc, Rectangle rectangle, NatTable natTable, ICellRenderer natCellRenderer, int row,
                int col, boolean selected) {
            try {
                Color orgFG = gc.getForeground();
                Color orgBG = gc.getBackground();
                Font orgFont = gc.getFont();

                // Selection Color
                IStyleConfig normalStyleConfig = natCellRenderer.getStyleConfig(DisplayModeEnum.NORMAL.toString(), row,
                        col);

                Color fg = selected ? _viewer.getVisualConfig().getSelectionForegroundColor() : normalStyleConfig
                        .getForegroundColor(row, col);
                Color bg = selected ? _viewer.getVisualConfig().getSelectionBackgroundColor() : normalStyleConfig
                        .getBackgroundColor(row, col);
                Font font = normalStyleConfig.getFont(row, col);

                alignment = _viewer.getColumn(col).getHorizontalCellAlignment();

                String text = natCellRenderer.getDisplayText(row, col);
                text = text == null ? "" : text;

                Image icon = getImage(natCellRenderer, row, col);

                gc.setFont(font);
                gc.setForeground(fg != null ? fg : GUIHelper.COLOR_LIST_FOREGROUND);
                gc.setBackground(bg != null ? bg : GUIHelper.COLOR_LIST_BACKGROUND);

                INatTableModel tableModel = natTable.getNatTableModel();
                // Allow display grid
                if (tableModel.isGridLineEnabled()) {
                    rectangle.x = rectangle.x + 1;
                    rectangle.width = rectangle.width - 1;
                    rectangle.y = rectangle.y + 1;
                    rectangle.height = rectangle.height - 1;
                }

                drawBackground(gc, rectangle);

                // Draw Single Cell Selection but will not called for column header
                if (selected && tableModel.isSingleCellSelection()) {
                    drawSingleCellSelection(gc, natTable, natCellRenderer, row, col, rectangle, fg, bg);
                }

                int imageWidth = icon != null ? icon.getBounds().width : 0;
                // Support Multiple Line
                int multiple = ((gc.getFontMetrics().getHeight() * GUIHelper.getNumberOfNewLine(text) + SPACE));

                int topAlign = rectangle.y + rectangle.height / 2
                        - ((multiple > rectangle.height ? rectangle.height / 2 : multiple / 2));

                if (imageWidth > 0) {
                    gc.drawImage(icon, rectangle.x + SPACE, topAlign);
                    imageWidth = imageWidth + SPACE;
                }

                // Draw Text
                drawText(gc, rectangle, text, imageWidth, topAlign);

                gc.setForeground(orgFG);
                gc.setBackground(orgBG);
                gc.setFont(orgFont);
            } catch (Exception err) {
                err.printStackTrace();
            }

        }

        @Override
        protected void drawSingleCellSelection(GC gc, NatTable natTable, ICellRenderer natCellRenderer, int row,
                int col, Rectangle rectangle, Color fg, Color bg) {
            Color origFg = gc.getForeground();
            Color origBg = gc.getBackground();

            INatTableModel tableModel = natTable.getNatTableModel();

            Color selectedfg = _viewer.getVisualConfig().getSelectionForegroundColor();//natCellRenderer.getStyleConfig(
            // DisplayModeEnum
            // .SELECT.toString(), row,
            // col).getForegroundColor(row,
            // col);
            Color selectedbg = _viewer.getVisualConfig().getSelectionBackgroundColor();//natCellRenderer.getStyleConfig(
            // DisplayModeEnum
            // .SELECT.toString(), row,
            // col).getBackgroundColor(row,
            // col);
            gc.setForeground(selectedfg != null ? selectedfg : GUIHelper.COLOR_LIST_FOREGROUND);
            gc.setBackground(selectedbg != null ? selectedbg : GUIHelper.COLOR_LIST_BACKGROUND);

            Rectangle cellRect = natTable.getModelBodyCellBound(row, col);
            if (tableModel.isGridLineEnabled()) {
                cellRect.x = cellRect.x + 1;
                cellRect.y = cellRect.y + 1;
                cellRect.width = cellRect.width - 1;
                cellRect.height = cellRect.height - 1;
            }
            drawBackground(gc, cellRect);

            if (_viewer.getVisualConfig().isDrawBorderOnCellSelectionCells()) {
                gc.setForeground(GUIHelper.COLOR_BLACK);
                gc.drawRectangle(rectangle.x, rectangle.y, rectangle.width - 1, rectangle.height - 1);
            }

            gc.setForeground(origFg);
            gc.setBackground(origBg);
        }

        @Override
        protected void drawText(GC gc, Rectangle rectangle, final String txt, int imageWidth, int topAlign) {
            gc.setClipping(rectangle);

            String originalText = txt;
            String text = txt;
            Point point;
            text = GUIHelper.getAvailableTextToDisplay(gc, rectangle, text);
            alignment = SWT.LEFT;

            switch (alignment) {
                case SWT.LEFT:
                    gc.drawText(text, imageWidth + rectangle.x + SPACE, topAlign, true);
                    break;
                case SWT.CENTER:
                    point = gc.textExtent(text);
                    gc.drawText(text, rectangle.x + (rectangle.width - point.x - SPACE), topAlign, true);
                    break;
                case SWT.RIGHT:
                    point = gc.textExtent(originalText);
                    int rightAlign = rectangle.x + rectangle.width / 2 - point.x / 2;

                    if (rectangle.width < (imageWidth + point.x + SPACE)) {
                        gc.drawText(text, imageWidth + rectangle.x + SPACE, topAlign, true);
                    } else {
                        gc.drawText(text, rightAlign < (rectangle.x + imageWidth) ? (rectangle.x + imageWidth)
                                : rightAlign, topAlign, true);
                    }
                    break;
            }
            gc.setClipping((Rectangle) null);
        }

    }

}

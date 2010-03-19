package com.hexapixel.framework.glazed.glazednatgridviewer.nat;

import net.sourceforge.nattable.NatTable;
import net.sourceforge.nattable.model.INatTableModel;
import net.sourceforge.nattable.painter.cell.HeaderCellPainter;
import net.sourceforge.nattable.painter.cell.TextCellPainter;
import net.sourceforge.nattable.renderer.ICellRenderer;
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
import org.eclipse.swt.widgets.Display;

import com.hexapixel.framework.glazed.glazednatgridviewer.GlazedNatGridViewer;

public class NatHeaderColumnCellPainter extends HeaderCellPainter {

    private int                 _style;
    private boolean             _multiSel;
    private GlazedNatGridViewer _viewer;

    public NatHeaderColumnCellPainter(GlazedNatGridViewer viewer, int style) {
        super(style);
        _style = style;
        _viewer = viewer;
    }

    public NatHeaderColumnCellPainter(GlazedNatGridViewer viewer, int style, boolean multiSel) {
        super(style);
        _style = style;
        _multiSel = multiSel;
        _viewer = viewer;
    }

    public NatHeaderColumnCellPainter() {
        super();
    }

    @Override
    public void drawBackground(GC gc, Rectangle rectangle) {

        switch (_viewer.getVisualConfig().getHeaderStyle()) {
            case STYLE_XP:
                NatHeaderStylesPainter.drawWindowsColumnBackground(_viewer, gc, rectangle, _multiSel);
                return;
            default:
            case STYLE_CLASSIC:
                NatHeaderStylesPainter.drawWindowsClassicColumnBackground(_viewer, gc, rectangle, _multiSel);
                return;
            case STYLE_AUTO_DETECT:
                Color bg = Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
                if (bg.getRed() == 212) {
                    NatHeaderStylesPainter.drawWindowsClassicColumnBackground(_viewer, gc, rectangle, _multiSel);
                    return;
                } else {
                    NatHeaderStylesPainter.drawWindowsColumnBackground(_viewer, gc, rectangle, _multiSel);
                    return;
                }
            case STYLE_INTERNAL_GRADIENT:
                break;
        }

        Color orgFG = gc.getForeground();
        Color orgBG = gc.getBackground();

        if ((_style & SWT.BORDER) == SWT.BORDER) {
            // GUIHelper.drawBorder(gc, rectangle);
            drawBorder(gc, rectangle);

        } else if ((_style & SWT.FLAT) == SWT.FLAT) {
            gc.setForeground(COLOR_BACKGROUND);
            gc.setBackground(GUIHelper.COLOR_WIDGET_LIGHT_SHADOW);
            gc.fillRectangle(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        } else {
            gc.setForeground(getGradientForeground());
            gc.setBackground(getGradientBackground());
            gc.fillGradientRectangle(rectangle.x, rectangle.y, rectangle.width, rectangle.height, true);
        }

        if (_multiSel) {
            gc.setForeground(_viewer.getVisualConfig().getColumnHeaderMultisortTopGradient());
            gc.setBackground(_viewer.getVisualConfig().getColumnHeaderMultisortBottomGradient());
            gc.fillGradientRectangle(rectangle.x, rectangle.y, rectangle.width, rectangle.height, true);
        } else {
            gc.setForeground(_viewer.getVisualConfig().getColumnHeaderTopGradient());
            gc.setBackground(_viewer.getVisualConfig().getColumnHeaderBottomGradient());
            gc.fillGradientRectangle(rectangle.x, rectangle.y, rectangle.width, rectangle.height, true);
        }

        gc.setForeground(orgFG);
        gc.setBackground(orgBG);

    }

    public void drawCell(GC gc, Rectangle rectangle, NatTable natTable, ICellRenderer natCellRenderer, int row,
            int col, boolean selected) {
        Color orgFG = gc.getForeground();
        Color orgBG = gc.getBackground();
        Font orgFont = gc.getFont();

        // Selection Color
        IStyleConfig normalStyleConfig = natCellRenderer.getStyleConfig(DisplayModeEnum.NORMAL.toString(), row, col);
        IStyleConfig selectionStyleConfig = natCellRenderer.getStyleConfig(DisplayModeEnum.SELECT.toString(), row, col);

        Color fg = selected ? selectionStyleConfig.getForegroundColor(row, col) : normalStyleConfig.getForegroundColor(
                row, col);
        Color bg = selected ? selectionStyleConfig.getBackgroundColor(row, col) : normalStyleConfig.getBackgroundColor(
                row, col);

        Font font = _viewer.getVisualConfig().getColumnHeaderFont();

        if (_multiSel) {
            if (_viewer.isMultiSorting()) {
                fg = _viewer.getVisualConfig().getColumnHeaderMultisortForegroundColor();
                if (_viewer.getVisualConfig().getColumnHeaderMultisortFont() != null) {
                    font = _viewer.getVisualConfig().getColumnHeaderMultisortFont();
                }
            }
        }

        // int alignment = normalStyleConfig.getHorizontalAlignment(row, col);

        String text = natCellRenderer.getDisplayText(row, col);
        text = text == null ? "" : text;

        Image icon = getImage(natCellRenderer, row, col);

        gc.setFont(font);
        gc.setForeground(fg != null ? fg : _viewer.getVisualConfig().getColumnHeaderForegroundColor());
        gc.setBackground(bg != null ? bg : _viewer.getVisualConfig().getCellBackgroundColor());
        
        //xx
        //gc.setForeground(_viewer.getVisualConfig().getColumnHeaderForegroundColor());

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

        int imgX = rectangle.x + rectangle.width - imageWidth - SPACE;
        if (imageWidth > 0 && imgX > rectangle.x) {
            gc.drawImage(icon, imgX, topAlign);
            imageWidth = imageWidth + SPACE;
        }

        // Draw Text
        drawText(gc, rectangle, text, imageWidth, topAlign, col);

        gc.setForeground(orgFG);
        gc.setBackground(orgBG);
        gc.setFont(orgFont);
    }

    @Override
    protected Image getImage(ICellRenderer cellRenderer, int row, int col) {
        Image img = cellRenderer.getStyleConfig(DisplayModeEnum.NORMAL.name(), row, col).getImage(row, col);
        if (img != null) cellRenderer.getStyleConfig(DisplayModeEnum.NORMAL.name(), row, col).getImage(row, col);

        return img;
    }

    private void drawBorder(GC gc, Rectangle rectangle) {
        gc.setBackground(TextCellPainter.COLOR_BACKGROUND);
        gc.fillRectangle(rectangle);

        // Up
        gc.setForeground(TextCellPainter.COLOR_WIDGET_LIGHT_SHADOW);
        gc.drawLine(rectangle.x, rectangle.y, rectangle.x + rectangle.width - 1, rectangle.y);
        gc.drawLine(rectangle.x, rectangle.y, rectangle.x, rectangle.y + rectangle.height);

        gc.setForeground(TextCellPainter.COLOR_WIDGET_LIGHT_SHADOW);
        gc.drawLine(rectangle.x + 1, rectangle.y + 1, rectangle.x + rectangle.width - 1, rectangle.y + 1);
        gc.drawLine(rectangle.x + 1, rectangle.y + 1, rectangle.x + 1, rectangle.y + rectangle.height - 1);

        // Down
        gc.setForeground(TextCellPainter.COLOR_WIDGET_DARK_SHADOW);
        gc.drawLine(rectangle.x, rectangle.y + rectangle.height - 1, rectangle.x + rectangle.width - 1, rectangle.y
                + rectangle.height - 1);
        gc.drawLine(rectangle.x + rectangle.width - 1, rectangle.y, rectangle.x + rectangle.width - 1, rectangle.y
                + rectangle.height - 1);

        gc.setForeground(TextCellPainter.COLOR_WIDGET_LIGHT_SHADOW);
        gc.drawLine(rectangle.x, rectangle.y + rectangle.height - 2, rectangle.x + rectangle.width - 1, rectangle.y
                + rectangle.height - 2);
        gc.drawLine(rectangle.x + rectangle.width - 2, rectangle.y, rectangle.x + rectangle.width - 2, rectangle.y
                + rectangle.height - 2);

    }

    // modified to take off image width when calculating text or it's completely wrong
    // also modified to stop thinking images are on the left side (sort images are right sided in western world)
    // etc.. -cre
    protected void drawText(GC gc, Rectangle rectangle, final String txt, int imageWidth, int topAlign, int col) {
        gc.setClipping(rectangle);

        String originalText = txt;
        String text = txt;
        Point point;
        int alignment = _viewer.getColumn(col).getHorzontalHeaderCellAlignment();
        int takeOff = 0;
        Rectangle mod = new Rectangle(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        if (alignment == SWT.CENTER) {
            takeOff = imageWidth + 6; // buffer it
        }

        mod.width -= takeOff;
        text = GUIHelper.getAvailableTextToDisplay(gc, mod, text);
        if (!text.equals(originalText)) {
            _viewer.getColumn(col).setTruncatedHeaderText(true);
        }
        else {
            _viewer.getColumn(col).setTruncatedHeaderText(false);
        }

        switch (alignment) {
            case SWT.LEFT:
                gc.drawText(text, rectangle.x + SPACE, topAlign, true);
                break;
            case SWT.RIGHT:
                point = gc.textExtent(text);
                gc.drawText(text, rectangle.x + (rectangle.width - point.x - SPACE - imageWidth), topAlign, true);
                break;
            case SWT.CENTER:
                point = gc.textExtent(originalText);
                int rightAlign = rectangle.x + rectangle.width / 2 - point.x / 2;

                if (rectangle.width < (point.x + SPACE)) {
                    gc.drawText(text, rectangle.x + SPACE, topAlign, true);
                } else {
                    gc.drawText(text,
                            rightAlign < (rectangle.x) ? (rectangle.x) : rightAlign,
                            topAlign, true);
                }
                break;
        }
        gc.setClipping((Rectangle) null);
    }
}
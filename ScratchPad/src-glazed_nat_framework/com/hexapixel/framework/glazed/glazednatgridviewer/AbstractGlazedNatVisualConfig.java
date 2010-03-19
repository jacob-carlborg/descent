package com.hexapixel.framework.glazed.glazednatgridviewer;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public abstract class AbstractGlazedNatVisualConfig {

    public static enum HeaderStyle {
        STYLE_XP, STYLE_CLASSIC, STYLE_INTERNAL_GRADIENT, STYLE_AUTO_DETECT;
    }

    private Color           _stripedBackgroundColor               = Display.getDefault().getSystemColor(
                                                                          SWT.COLOR_WIDGET_LIGHT_SHADOW);
    private Color           _stripedForegroundColor               = GlazedNatColorCache.getBlack();

    private Color           _columnHeaderTopGradient              = GlazedNatColorCache.getColor(110, 176, 255);
    private Color           _columnHeaderBottomGradient           = GlazedNatColorCache.getColor(0, 85, 229);
    private Color           _columnHeaderForegroundColor          = GlazedNatColorCache.getBlack();
    private Color           _columnHeaderForegroundSelected       = GlazedNatColorCache.getColor(230, 211, 5);
    private Font            _columnHeaderFont                     = null;

    private Font            _columnHeaderMultisortFont            = null;
    private Color           _columnHeaderMultisortTopGradient     = GlazedNatColorCache.getColor(255, 255, 255);
    private Color           _columnHeaderMultisortBottomGradient  = GlazedNatColorCache.getColor(0, 85, 229);
    private Color           _columnHeaderMultisortForegroundColor = GlazedNatColorCache.getColor(0, 54, 117);

    private Color           _cellBackgroundColor                  = Display.getDefault().getSystemColor(
                                                                          SWT.COLOR_LIST_BACKGROUND);
    // .
    private Color           _cellForegroundColor                  = GlazedNatColorCache.getBlack();
    private Font            _cellFont                             = null;

    private Color           _highlightBackgroundColor             = GlazedNatColorCache.getColor(152, 150, 136);
    private Color           _highlightForegroundColor             = GlazedNatColorCache.getColor(255, 223, 1);

    private Color           _selectionBackgroundColor             = Display.getDefault().getSystemColor(
                                                                          SWT.COLOR_LIST_SELECTION);
    private Color           _selectionForegroundColor             = GlazedNatColorCache.getWhite();

    private Color           _gridLineColor                        = GlazedNatColorCache.getColor(236, 233, 216);

    private Image           _upSortImage;
    private Image           _downSortImage;

    private int             _defaultHeaderHeight                  = 20;
    private int             _defaultRowHeight                     = 18;
    private int             _defaultColumnWidth                   = 100;

    private HeaderStyle     _headerStyle                          = HeaderStyle.STYLE_AUTO_DETECT;

    private boolean         _showGridLinesOnEmptyTable            = true;
    private boolean         _drawGridLinesToBottom                = true;

    private boolean         _drawBorderOnCellSelectionCells       = false;

    private ImageDescriptor _hideColumnActionImage;
    private ImageDescriptor _showAllColumnsActionImage;
    private ImageDescriptor _resetColumnOrderActionImage;
    private ImageDescriptor _packAllColumnsActionImage;
    private ImageDescriptor _resetColumnWidthsActionImage;

    public Color getStripedBackgroundColor() {
        return _stripedBackgroundColor;
    }

    public void setStripedBackgroundColor(Color stripedBackgroundColor) {
        _stripedBackgroundColor = stripedBackgroundColor;
    }

    public Color getStripedForegroundColor() {
        return _stripedForegroundColor;
    }

    public void setStripedForegroundColor(Color stripedForegroundColor) {
        _stripedForegroundColor = stripedForegroundColor;
    }

    public Color getColumnHeaderTopGradient() {
        return _columnHeaderTopGradient;
    }

    public void setColumnHeaderTopGradient(Color columnHeaderTopGradient) {
        _columnHeaderTopGradient = columnHeaderTopGradient;
    }

    public Color getColumnHeaderBottomGradient() {
        return _columnHeaderBottomGradient;
    }

    public void setColumnHeaderBottomGradient(Color columnHeaderBottomGradient) {
        _columnHeaderBottomGradient = columnHeaderBottomGradient;
    }

    public Font getColumnHeaderFont() {
        return _columnHeaderFont;
    }

    public void setColumnHeaderFont(Font columnHeaderFont) {
        _columnHeaderFont = columnHeaderFont;
    }

    public Color getColumnHeaderMultisortTopGradient() {
        return _columnHeaderMultisortTopGradient;
    }

    public void setColumnHeaderMultisortTopGradient(Color columnHeaderMultisortTopGradient) {
        _columnHeaderMultisortTopGradient = columnHeaderMultisortTopGradient;
    }

    public Color getColumnHeaderMultisortBottomGradient() {
        return _columnHeaderMultisortBottomGradient;
    }

    public void setColumnHeaderMultisortBottomGradient(Color columnHeaderMultisortBottomGradient) {
        _columnHeaderMultisortBottomGradient = columnHeaderMultisortBottomGradient;
    }

    public Color getCellBackgroundColor() {
        return _cellBackgroundColor;
    }

    public void setCellBackgroundColor(Color cellBackgroundColor) {
        _cellBackgroundColor = cellBackgroundColor;
    }

    public Color getCellForegroundColor() {
        return _cellForegroundColor;
    }

    public void setCellForegroundColor(Color cellForegroundColor) {
        _cellForegroundColor = cellForegroundColor;
    }

    public Font getCellFont() {
        return _cellFont;
    }

    public void setCellFont(Font cellFont) {
        _cellFont = cellFont;
    }

    public Color getHighlightBackgroundColor() {
        return _highlightBackgroundColor;
    }

    public void setHighlightBackgroundColor(Color highlightBackgroundColor) {
        _highlightBackgroundColor = highlightBackgroundColor;
    }

    public Color getHighlightForegroundColor() {
        return _highlightForegroundColor;
    }

    public void setHighlightForegroundColor(Color highlightForegroundColor) {
        _highlightForegroundColor = highlightForegroundColor;
    }

    public Color getSelectionBackgroundColor() {
        return _selectionBackgroundColor;
    }

    public void setSelectionBackgroundColor(Color selectionBackgroundColor) {
        _selectionBackgroundColor = selectionBackgroundColor;
    }

    public Color getSelectionForegroundColor() {
        return _selectionForegroundColor;
    }

    public void setSelectionForegroundColor(Color selectionForegroundColor) {
        _selectionForegroundColor = selectionForegroundColor;
    }

    public Color getColumnHeaderForegroundColor() {
        return _columnHeaderForegroundColor;
    }

    public void setColumnHeaderForegroundColor(Color columnHeaderForegroundColor) {
        _columnHeaderForegroundColor = columnHeaderForegroundColor;
    }

    public Color getColumnHeaderMultisortForegroundColor() {
        return _columnHeaderMultisortForegroundColor;
    }

    public void setColumnHeaderMultisortForegroundColor(Color columnHeaderMultisortForegroundColor) {
        _columnHeaderMultisortForegroundColor = columnHeaderMultisortForegroundColor;
    }

    public Color getGridLineColor() {
        return _gridLineColor;
    }

    public void setGridLineColor(Color gridLineColor) {
        _gridLineColor = gridLineColor;
    }

    public Image getUpSortImage() {
        return _upSortImage;
    }

    public void setUpSortImage(Image upSortImage) {
        _upSortImage = upSortImage;
    }

    public Image getDownSortImage() {
        return _downSortImage;
    }

    public void setDownSortImage(Image downSortImage) {
        _downSortImage = downSortImage;
    }

    public int getDefaultHeaderHeight() {
        return _defaultHeaderHeight;
    }

    public void setDefaultHeaderHeight(int defaultHeaderHeight) {
        _defaultHeaderHeight = defaultHeaderHeight;
    }

    public int getDefaultRowHeight() {
        return _defaultRowHeight;
    }

    public void setDefaultRowHeight(int defaultRowHeight) {
        _defaultRowHeight = defaultRowHeight;
    }

    public int getDefaultColumnWidth() {
        return _defaultColumnWidth;
    }

    public void setDefaultColumnWidth(int defaultColumnWidth) {
        _defaultColumnWidth = defaultColumnWidth;
    }

    public Color getColumnHeaderForegroundSelected() {
        return _columnHeaderForegroundSelected;
    }

    public void setColumnHeaderForegroundSelected(Color columnHeaderForegroundSelected) {
        _columnHeaderForegroundSelected = columnHeaderForegroundSelected;
    }

    public Font getColumnHeaderMultisortFont() {
        return _columnHeaderMultisortFont;
    }

    public void setColumnHeaderMultisortFont(Font columnHeaderMultisortFont) {
        _columnHeaderMultisortFont = columnHeaderMultisortFont;
    }

    public HeaderStyle getHeaderStyle() {
        return _headerStyle;
    }

    public void setHeaderStyle(HeaderStyle headerStyle) {
        _headerStyle = headerStyle;
    }

    public boolean isShowGridLinesOnEmptyTable() {
        return _showGridLinesOnEmptyTable;
    }

    public void setShowGridLinesOnEmptyTable(boolean showGridLinesOnEmptyTable) {
        _showGridLinesOnEmptyTable = showGridLinesOnEmptyTable;
    }

    public boolean isDrawBorderOnCellSelectionCells() {
        return _drawBorderOnCellSelectionCells;
    }

    public void setDrawBorderOnCellSelectionCells(boolean drawBorderOnCellSelectionCells) {
        _drawBorderOnCellSelectionCells = drawBorderOnCellSelectionCells;
    }

    public ImageDescriptor getHideColumnActionImage() {
        return _hideColumnActionImage;
    }

    public ImageDescriptor getShowAllColumnsActionImage() {
        return _showAllColumnsActionImage;
    }

    public ImageDescriptor getResetColumnOrderActionImage() {
        return _resetColumnOrderActionImage;
    }

    public ImageDescriptor getPackAllColumnsActionImage() {
        return _packAllColumnsActionImage;
    }

    public ImageDescriptor getResetColumnWidthsActionImage() {
        return _resetColumnWidthsActionImage;
    }

    /**
     * This method determines whether grid lines should be drawn to the bottom of the table regardless
     * of where rows end (so if rows don't fill the entire table, gridlines are still drawn in the entire grid)
     *  
     * @return true whether to draw the entire screen with gridlines (as wide as columns). 
     */
    public boolean isDrawGridLinesToBottom() {
        return _drawGridLinesToBottom;
    }

    public void setDrawGridLinesToBottom(boolean drawGridLinesToBottom) {
        _drawGridLinesToBottom = drawGridLinesToBottom;
    }

}

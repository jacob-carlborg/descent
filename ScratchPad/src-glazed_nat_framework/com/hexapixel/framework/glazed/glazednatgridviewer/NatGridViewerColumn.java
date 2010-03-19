package com.hexapixel.framework.glazed.glazednatgridviewer;

import java.util.HashMap;
import java.util.UUID;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;

/**
 * Wrapper class that represents one column in the table.
 */
public class NatGridViewerColumn implements Comparable<NatGridViewerColumn> {

    private String                  _name;
    private HashMap<Object, Object> _dataMap;
    private Object                  _data;
    private int                     _index;
    private boolean                 _frozen;
    private boolean                 _sortable;
    private boolean                 _isSortColumn;
    private boolean                 _moveable                      = true;
    private int                     _sortDirection;
    private GlazedNatGridViewer    _parentTable;
    private String                  _uniqueId;
    private boolean                 _hideable;
    private Font                    _font;
    private Color                   _backgroundColor;
    private Color                   _foregroundColor;
    private Color                   _multiSortBackgroundColor;
    private Color                   _multiSortForegroundColor;
    private boolean                 _isResizable                   = true;
    private boolean                 _hidden;
    private int                     _horizontalHeaderCellAlignment = SWT.CENTER;
    private int                     _horizontalCellAlignment       = SWT.LEFT;
    private boolean                 _hoverColumn;
    private boolean                 _hoverSelectColumn;
    private int                     _defaultWidth;
    private boolean                 _truncatedHeaderText;

    public NatGridViewerColumn(GlazedNatGridViewer parentTable, String name, boolean frozen, boolean moveable,
            boolean sortable, boolean hideable) {
        this._parentTable = parentTable;
        this._name = name;
        this._frozen = frozen;
        this._sortable = sortable;
        this._hideable = hideable;

        init();
    }

    public NatGridViewerColumn(NatGridViewerColumnGroup group, GlazedNatGridViewer parentTable, String name,
            boolean frozen, boolean moveable, boolean sortable, boolean hideable) {
        this._parentTable = parentTable;
        group.addColumn(this);
        this._name = name;
        this._frozen = frozen;
        this._sortable = sortable;
        this._hideable = hideable;

        init();
    }

    private void init() {
        _dataMap = new HashMap<Object, Object>();
        _uniqueId = UUID.randomUUID().toString();
    }

    public boolean isActiveMultiSortColumn() {
        return _parentTable.getViewer().isActiveMultiSortColumn(this);
    }

    public void setName(String name) {
        this._name = name;
    }

    public String getName() {
        return _name;
    }

    public void setData(Object key, Object value) {
        _dataMap.put(key, value);
    }

    public Object getData(Object key) {
        return _dataMap.get(key);
    }

    public int getIndex() {
        return _index;
    }

    public void setIndex(int index) {
        this._index = index;
    }

    public boolean isFrozen() {
        return _frozen;
    }

    public void setFrozen(boolean frozen) {
        this._frozen = frozen;
    }

    public boolean isSortable() {
        return _sortable;
    }

    public void setSortable(boolean sortable) {
        this._sortable = sortable;
    }

    public boolean isSortColumn() {
        return _isSortColumn;
    }

    public void setSortColumn(boolean isSortColumn) {
        this._isSortColumn = isSortColumn;
    }

    public int getSortDirection() {
        return _sortDirection;
    }

    public void setSortDirection(int sortDirection) {
        this._sortDirection = sortDirection;
    }

    public void setMoveable(boolean moveable) {
        _moveable = moveable;
    }

    public boolean isMoveable() {
        return _moveable;
    }

    public String getUniqueId() {
        return _uniqueId;
    }

    public boolean isHideable() {
        return _hideable;
    }

    public void setHideable(boolean hideable) {
        this._hideable = hideable;
    }

    public void setHidden(boolean hidden) {
        try {
            if (!isHideable()) return;
            _hidden = hidden;

            // _gridColumn.setVisible(!hidden);
            // this forces a scrollbar update
            // _gridColumn.setWidth(_gridColumn.getWidth());
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public boolean isHidden() {
        return _hidden;
    }

    public Font getFont() {
        return _font;
    }

    public void setFont(Font font) {
        _font = font;
    }

    public Color getBackgroundColor() {
        return _backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        _backgroundColor = backgroundColor;
    }

    public Color getForegroundColor() {
        return _foregroundColor;
    }

    public void setForegroundColor(Color foregroundColor) {
        _foregroundColor = foregroundColor;
    }

    public Color getMultiSortBackgroundColor() {
        return _multiSortBackgroundColor;
    }

    public void setMultiSortBackgroundColor(Color multiSortBackgroundColor) {
        _multiSortBackgroundColor = multiSortBackgroundColor;
    }

    public Color getMultiSortForegroundColor() {
        return _multiSortForegroundColor;
    }

    public void setMultiSortForegroundColor(Color multiSortForegroundColor) {
        _multiSortForegroundColor = multiSortForegroundColor;
    }

    public boolean isResizable() {
        return _isResizable;
    }

    public void setResizable(boolean resizable) {
        _isResizable = resizable;
    }

    @Override
    public int compareTo(NatGridViewerColumn col) {
        return _name.compareTo(col.getName());
    }

    public int getHorzontalHeaderCellAlignment() {
        return _horizontalHeaderCellAlignment;
    }

    public void setHorzontalHeaderCellAlignment(int horzontalCellAlignment) {
        _horizontalHeaderCellAlignment = horzontalCellAlignment;
    }

    public int getHorizontalCellAlignment() {
        return _horizontalCellAlignment;
    }

    public void setHorizontalCellAlignment(int horizontalCellAlignment) {
        _horizontalCellAlignment = horizontalCellAlignment;
    }

    public boolean isHoverColumn() {
        return _hoverColumn;
    }

    public void setHoverColumn(boolean hoverColumn) {
        _hoverColumn = hoverColumn;
    }

    public boolean isHoverSelectColumn() {
        return _hoverSelectColumn;
    }

    public void setHoverSelectColumn(boolean hoverSelectColumn) {
        _hoverSelectColumn = hoverSelectColumn;
    }

    public Object getData() {
        return _data;
    }

    public void setData(Object data) {
        _data = data;
    }

    public int getDefaultWidth() {
        return _defaultWidth;
    }

    public void setDefaultWidth(int defaultWidth) {
        _defaultWidth = defaultWidth;
        _parentTable.setDefaultWidth(getIndex(), defaultWidth);
    }

    public boolean isTruncatedHeaderText() {
        return _truncatedHeaderText;
    }

    public void setTruncatedHeaderText(boolean truncatedHeaderText) {
        _truncatedHeaderText = truncatedHeaderText;
    }

    @Override
    public String toString() {
        return "[GridViewerColumn: " + _name + "]";
    }

}

package com.hexapixel.framework.glazed.glazednatgridviewer;

import net.sourceforge.nattable.model.DefaultNatTableModel;

/**
 * Model implementation for NAT
 * 
 */
public class GlazedNatGridViewerTableModel extends DefaultNatTableModel {

    private IGlazedNatGridViewer   _parent;

    private int                    _frozenCols;
    private int                    _frozenRows;

    public GlazedNatGridViewerTableModel(IGlazedNatGridViewer parent) {
        super();
        this._parent = parent;
    }

    public void setFrozenRows(int rows) {
        _frozenRows = rows;
    }
    
    public void setFrozenCols(int cols) {
        _frozenCols = cols;
    }
    
    public void clearFrozenColumns() {
        _frozenCols = 0;
    }

    public void clearFrozenRows() {
        _frozenRows = 0;
    }

    @Override
    public int getFreezeColumnCount() {
        return _frozenCols;
    }

    @Override
    public int getFreezeRowCount() {
        return _frozenRows;
    }

    @Override
    public boolean isBodyColumnResizable(int col) {
        // noticed this when mouse moved over a column before we even had column data (very early client startup)
        // so let's dispose check here
        if (_parent.getViewer().getGrid() == null || _parent.getViewer().getGrid().isDisposed())
            return false;
        
        // another weird-o-check that can cause client issues on startup
        if (_parent.getColumnCount() == 0)
            return false;
        
        // index out of bounds?
        if (_parent.getColumnCount() <= col) 
            return false;
        
        // plain weird
        if (col < 0)
            return false;
        
        return _parent.getViewer().getColumn(col).isResizable();
    }

    @Override
    public boolean isGridLineEnabled() {
        return true;
    }

}

package com.hexapixel.framework.glazed.glazednatgridviewer.nat;

import net.sourceforge.nattable.config.IBodyConfig;
import net.sourceforge.nattable.config.SizeConfig;
import net.sourceforge.nattable.data.IDataProvider;
import net.sourceforge.nattable.renderer.ConfigDrivenCellRenderer;
import net.sourceforge.nattable.renderer.ICellRenderer;
import net.sourceforge.nattable.typeconfig.content.ContentConfigRegistry;
import net.sourceforge.nattable.typeconfig.style.StyleConfigRegistry;

import com.hexapixel.framework.glazed.glazednatgridviewer.GlazedNatGridViewer;

public class NatBodyConfig implements IBodyConfig {

    protected ICellRenderer     _cellRenderer;
    private SizeConfig          _columnWidthConfig;
    private SizeConfig          _rowHeightConfig;
    private IDataProvider       _dataProvider;
    private GlazedNatGridViewer _parent;

    public NatBodyConfig(GlazedNatGridViewer parent, IDataProvider dataProvider) {
        this(parent, dataProvider, null, null);
    }

    public NatBodyConfig(GlazedNatGridViewer parent, IDataProvider dataProvider,
            ContentConfigRegistry contentConfigRegistry, StyleConfigRegistry styleConfigRegistry) {
        this._dataProvider = dataProvider;
        _parent = parent;
        if (contentConfigRegistry != null && styleConfigRegistry != null) {
            _cellRenderer = new ConfigDrivenCellRenderer(dataProvider, contentConfigRegistry, styleConfigRegistry);
        } else {
            _cellRenderer = new NatCellRenderer(_parent, dataProvider);// new DataBindingCellRenderer(dataProvider);
        }

        initialize();
    }

    protected final void initialize() {
        _columnWidthConfig = new SizeConfig(_parent.getVisualConfig().getDefaultColumnWidth());

        _rowHeightConfig = new SizeConfig(_parent.getVisualConfig().getDefaultRowHeight());
    }

    public ICellRenderer getCellRenderer() {
        return _cellRenderer;
    }

    public void setCellRenderer(ICellRenderer cellRenderer) {
        this._cellRenderer = cellRenderer;
    }

    public int getColumnCount() {
        return _dataProvider.getColumnCount();
    }

    public SizeConfig getColumnWidthConfig() {
        return _columnWidthConfig;
    }

    public void setColumnWidthConfig(SizeConfig columnWidthConfig) {
        this._columnWidthConfig = columnWidthConfig;
    }

    public int getRowCount() {
        return _dataProvider.getRowCount();
    }

    public SizeConfig getRowHeightConfig() {
        return _rowHeightConfig;
    }

    public void setRowHeightConfig(SizeConfig rowHeightConfig) {
        this._rowHeightConfig = rowHeightConfig;
    }
}
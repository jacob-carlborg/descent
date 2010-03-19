package com.hexapixel.framework.glazed.glazednatgridviewer.nat;

import net.sourceforge.nattable.config.DefaultColumnHeaderConfig;
import net.sourceforge.nattable.config.SizeConfig;
import net.sourceforge.nattable.data.IColumnHeaderLabelProvider;
import net.sourceforge.nattable.renderer.ICellRenderer;

import com.hexapixel.framework.glazed.glazednatgridviewer.GlazedNatGridViewer;

public class NatHeaderConfig extends DefaultColumnHeaderConfig {

    private NatColumnHeightSizeConfig _rowHeightConfig;
    private GlazedNatGridViewer          _parent;

    private NatColumnHeaderRenderer   _renderer;

    public NatHeaderConfig(final GlazedNatGridViewer parent) {
        super(new IColumnHeaderLabelProvider() {
            @Override
            public String getColumnHeaderLabel(int col) {
                return parent.getColumnName(col);
            }
        });
        _parent = parent;
        _rowHeightConfig = new NatColumnHeightSizeConfig(this);
        _renderer = new NatColumnHeaderRenderer(parent, new IColumnHeaderLabelProvider() {
            @Override
            public String getColumnHeaderLabel(int col) {
                return parent.getColumnName(col);
            }
        });
    }
    
    @Override
    public SizeConfig getColumnHeaderRowHeightConfig() {
        return _rowHeightConfig;
    }
    
/*    public int getColumnWidth(int col) {
        return _rowHeightConfig.getSize(col);
    }
    
    public void setColumnWidth(int col, int size) {
        _rowHeightConfig.setSize(col, size);
    }
*/
    public GlazedNatGridViewer getGlazedGridViewer() {
        return _parent;
    }

    @Override
    public ICellRenderer getCellRenderer() {
        return _renderer;
    }
    
    

}

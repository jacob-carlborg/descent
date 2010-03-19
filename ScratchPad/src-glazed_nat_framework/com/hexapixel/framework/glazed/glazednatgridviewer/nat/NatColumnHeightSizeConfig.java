package com.hexapixel.framework.glazed.glazednatgridviewer.nat;

import net.sourceforge.nattable.config.SizeConfig;

public class NatColumnHeightSizeConfig extends SizeConfig {

    private static final long serialVersionUID = -310813894285541958L;

    private NatHeaderConfig _config;
    
    public NatColumnHeightSizeConfig(NatHeaderConfig config) {
        super();
        _config = config;
    }
    
    @Override
    public int getDefaultSize() {
        return _config.getGlazedGridViewer().getVisualConfig().getDefaultHeaderHeight();
    }
    
    @Override
    public boolean getDefaultResizable() {
        return true;
    }

    @Override
    public boolean isIndexResizable(int index) {
        return _config.getGlazedGridViewer().getColumn(index).isResizable();
    }

}

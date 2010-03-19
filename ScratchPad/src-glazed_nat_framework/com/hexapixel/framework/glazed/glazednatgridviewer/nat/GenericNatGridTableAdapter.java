package com.hexapixel.framework.glazed.glazednatgridviewer.nat;

import java.util.Comparator;

import org.eclipse.jface.action.MenuManager;

import com.hexapixel.framework.glazed.glazednatgridviewer.DefaultDataComparator;
import com.hexapixel.framework.glazed.glazednatgridviewer.IGenericNatGridTable;
import com.hexapixel.framework.glazed.griddata.DataFilter;
import com.hexapixel.framework.glazed.griddata.IDataObject;

public class GenericNatGridTableAdapter implements IGenericNatGridTable {

    @Override
    public Comparator<IDataObject> getComparator(int column, int direction) {
        return new DefaultDataComparator(column, direction);
    }

    @Override
    public DataFilter getTableFilter() {
        return null;
    }

    @Override
    public void popuplateColumnMenu(MenuManager menuManager) {
    }

    @Override
    public void tableFilterModified() {
    }

}

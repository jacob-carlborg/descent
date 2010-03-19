package com.hexapixel.framework.glazed.griddata;

import java.util.Comparator;

import org.eclipse.swt.SWT;

/**
 * This class deals with sorting in the table. (It needs to be immutable, hence it's final).
 */
public final class DataObjectComparator implements Comparator<IDataObject> {

    private int _columnToCompare = -1;
    private int _sortOrder       = 0;

    public DataObjectComparator() {
    }

    public DataObjectComparator(int columnToCompare, int sortOrder) {
        _columnToCompare = columnToCompare;
        _sortOrder = sortOrder;
    }

    @Override
    public int compare(IDataObject one, IDataObject two) {
        try {
            if (_columnToCompare == -1) return 0;

            if (_sortOrder == SWT.NONE) return 0;

            String strOne = one.getColumnText(_columnToCompare);
            String strTwo = two.getColumnText(_columnToCompare);

            if (strOne == null || strTwo == null) return 0;

            // TODO: You probably want to check of object types and sort more professionally than a string comparison

            int ret = strOne.compareTo(strTwo);

            if (_sortOrder == SWT.DOWN) return ret;
            else return -ret;
        } catch (Exception err) {
            err.printStackTrace();
        }
        
        return 0;
    }

}

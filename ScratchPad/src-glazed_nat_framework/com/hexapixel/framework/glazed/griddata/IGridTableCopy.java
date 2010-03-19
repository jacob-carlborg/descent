package com.hexapixel.framework.glazed.griddata;

public interface IGridTableCopy {

    /**
     * Should return a string that is the formatedd text to be copied to the clipboard.
     * 
     * @param appendColumnNames If column names should be appended to copied data
     * @return String or null.
     */
    public String getCopyData(boolean appendColumnNames);

}

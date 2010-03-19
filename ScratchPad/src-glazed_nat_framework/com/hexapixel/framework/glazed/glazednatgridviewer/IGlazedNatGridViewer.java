package com.hexapixel.framework.glazed.glazednatgridviewer;

import java.util.List;

import net.sourceforge.nattable.NatTable;

/**
 * Interface containing a few methods we need instead of passing the entire GlazedGridViewer around.
 * 
 * @author cre
 */
public interface IGlazedNatGridViewer {

    /**
     * Data used for storing the column index on a column
     */
    public static final String COLUMN_INDEX_DATA = "_cid_";

    /**
     * Sorts the table on the given column in the next order of sort.
     * 
     * @param column Column to sort.
     */
    public void sortColumn(NatGridViewerColumn column, int index);

    /**
     * Sorts the table on the given columns in the given order of columns.
     * 
     * @param columns Columns to sort.
     */
    public void sortColumns(List<NatGridViewerColumn> columns);

    /**
     * Returns the grid for this sortable table.
     * 
     * @return Grid
     */
    public NatTable getGrid();

    /**
     * Returns the input. This should be the root Glazed event list! Nothing else!
     * 
     * @return Input
     */
    public List<?> getInput();

    /**
     * Returns the number of columns.
     * 
     * @return number of columns
     */
    public int getColumnCount();

    /**
     * Returns the visible name of a column.
     * 
     * @param index Column index
     * @return name of column
     */
    public String getColumnName(int index);

    /**
     * Returns the statemask of the last mouse down/up state.
     * 
     * @return keyboard statemask
     */
    public int getStateMask();

    /**
     * Returns the parent viewer.
     * 
     * @return Viewer
     */
    public GlazedNatGridViewer getViewer();

    /**
     * Returns true if the <code>data</code> is an even row.
     * 
     * @param data data to check
     * @return true if row is even, false if it's an odd row.
     */
    public boolean isEvenRow(Object data);
}

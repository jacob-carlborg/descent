package com.hexapixel.framework.glazed.glazednatgridviewer;

import java.util.Comparator;

import org.eclipse.jface.action.MenuManager;

import com.hexapixel.framework.glazed.griddata.DataFilter;
import com.hexapixel.framework.glazed.griddata.IDataObject;

/**
 * Matcher and sorter interface for implementors to return filter and sort matchers.
 * 
 * @author cre
 *
 */
public interface IGenericNatGridTable {

    /**
     * Returns a filter that may be used to additionally filter things locally in a table outside of the
     * default filter implementations by the framework.
     * 
     * @return Matcher 
     */
    public DataFilter getTableFilter();
    
    /**
     * Returns a new comparator used for sorting. Must be a new object, cannot be a cached object,
     * sorters are immutable.
     * <p>
     * Do note that the sorter type can be set via {@link GlazedNatGridViewer#setSortAlgorithm(com.hexapixel.framework.glazed.glazednatgridviewer.GlazedNatGridViewer.SortAlgorithm)} as defined by {@link SortAlgorithm}.
     * 
     * @param column column index 
     * @param direction one of SWT.UP, SWT.DOWN, SWT.NONE
     * @return Comparator
     */
    public Comparator<IDataObject> getComparator(int column, int direction);

 
    /**
     * This method is called last prior to a table right click menu is opened on the table so that you 
     * can populate it with any items you feel are missing or custom actions.
     * 
     * @param menuManager MenuManager to populate
     */
    public void popuplateColumnMenu(MenuManager menuManager);

    /**
     * This method is called after the Data Filter is modified so that you 
     * can visually reflect this modification, such as update part name.
     * 
     */
    public void tableFilterModified();

}

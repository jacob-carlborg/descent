package com.hexapixel.framework.glazed.griddata;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ViewerFilter;

import ca.odell.glazedlists.matchers.Matcher;

/**
 * This class deals with table filtering. (It needs to be immutable, hence it's final)
 */
public final class DataObjectFilterMatcher implements Matcher<IDataObject> {

    private List<ViewerFilter> _viewerFilters;

    public DataObjectFilterMatcher() {
        init();
    }

    public DataObjectFilterMatcher(List<ViewerFilter> filters) {
        this();
        setFilters(filters);
    }

    public void addFilter(ViewerFilter filter) {
        if (!_viewerFilters.contains(filter)) {
            _viewerFilters.add(filter);
        }
    }
    
    public void removeFilter(ViewerFilter filter) {
        if (_viewerFilters.contains(filter))
            _viewerFilters.remove(filter);
    }

    private void init() {
        _viewerFilters = new ArrayList<ViewerFilter>();
    }

    public void setFilters(List<ViewerFilter> filters) {
        _viewerFilters = filters;
    }

    @Override
    public boolean matches(IDataObject obj) {
        if (_viewerFilters != null && _viewerFilters.size() > 0) {
            for (ViewerFilter vf : _viewerFilters) {
                if (!vf.select(null, null, obj)) { return false; }
            }
        }

        return true;
    }

}

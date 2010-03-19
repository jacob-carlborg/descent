package com.hexapixel.framework.glazed.glazednatgridviewer;

import java.util.ArrayList;
import java.util.List;

//import org.eclipse.nebula.widgets.grid.GridColumnGroup;

/**
 * Wrapper class that represents a column group in the table.
 *
 */
public class NatGridViewerColumnGroup {

	private List<NatGridViewerColumn>	_columns;
	private String					_name;
	private IGlazedNatGridViewer		_parent;
//	private GridColumnGroup			_group;

	public NatGridViewerColumnGroup(String name, IGlazedNatGridViewer parent) {
		this._name = name;
		this._parent = parent;
		//_group = new GridColumnGroup(parent.getGrid(), SWT.NONE);
//		_group.setData(this);
//		_group.setText(name);
		_columns = new ArrayList<NatGridViewerColumn>();
	}
	
	public IGlazedNatGridViewer getParent() {
		return _parent;
	}

//	public GridColumnGroup getGroup() {
//		return _group;
//	}

/*	public List<GridColumn> getGridColumns() {
		List<GridColumn> ret = new ArrayList<GridColumn>();
		for (GridViewerColumn col : _columns)
			ret.add(col.getGridColumn());

		return ret;
	}
*/
	public String getName() {
		return _name;
	}

	void addColumn(NatGridViewerColumn column) {
		_columns.add(column);
	}

	public List<NatGridViewerColumn> getColumns() {
		return _columns;
	}

}

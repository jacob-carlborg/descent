package com.hexapixel.framework.glazed.glazednatgridviewer;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * Just your standard table content provider, there is nothing special here.
 *
 */
public class NatGridViewerContentProvider implements IStructuredContentProvider {

	private IGlazedNatGridViewer _parent;
	
	public NatGridViewerContentProvider(IGlazedNatGridViewer parent) {
		_parent = parent;
	}
	
	@Override
	public void dispose() {		
	}

	@Override
	public void inputChanged(Viewer viewer, Object arg1, Object arg2) {
		
	}

	@Override
	public Object[] getElements(Object parent) {
		if (_parent.getInput() == null)
			return new Object[0];
				
		return _parent.getInput().toArray();
	}

}

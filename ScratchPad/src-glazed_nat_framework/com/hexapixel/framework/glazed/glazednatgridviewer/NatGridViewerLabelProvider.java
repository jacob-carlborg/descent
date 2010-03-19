package com.hexapixel.framework.glazed.glazednatgridviewer;

import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

import com.hexapixel.framework.glazed.griddata.IDataObject;

/**
 * Just your standard table label provider, there is nothing special here.
 *
 */
public class NatGridViewerLabelProvider extends LabelProvider implements ITableLabelProvider, ITableFontProvider, ITableColorProvider {

	private IGlazedNatGridViewer _parent;
	
	public NatGridViewerLabelProvider(IGlazedNatGridViewer parent) {
		_parent = parent;
	}
		
	@Override
	public Color getBackground(Object obj, int index) {
		return ((IDataObject)obj).getBackground(index, _parent.isEvenRow(obj));
	}

	@Override
	public Color getForeground(Object obj, int index) {
	    return ((IDataObject)obj).getForeground(index, _parent.isEvenRow(obj));
	}

	@Override
	public Image getColumnImage(Object obj, int index) {
		return ((IDataObject)obj).getColumnImage(index);
	}

	@Override
	public String getColumnText(Object obj, int index) {
		return ((IDataObject)obj).getColumnText(index);
	}

	@Override
	public Font getFont(Object obj, int index) {
		return ((IDataObject)obj).getFont(index);
	}

}

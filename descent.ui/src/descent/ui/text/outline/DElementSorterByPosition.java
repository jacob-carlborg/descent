package descent.ui.text.outline;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import descent.core.dom.IDElement;

public class DElementSorterByPosition extends ViewerSorter {
	
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		IDElement elem1 = (IDElement) e1;
		IDElement elem2 = (IDElement) e2;
		return elem1.getOffset() < elem2.getOffset() ? -1 :
			elem1.getOffset() > elem2.getOffset() ? 1 : 0;
	}

}

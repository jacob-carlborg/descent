package descent.ui.text.outline;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import descent.core.dom.IElement;

public class DElementSorterByPosition extends ViewerSorter {
	
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		IElement elem1 = (IElement) e1;
		IElement elem2 = (IElement) e2;
		return elem1.getStartPosition() < elem2.getStartPosition() ? -1 :
			elem1.getStartPosition() > elem2.getStartPosition() ? 1 : 0;
	}

}

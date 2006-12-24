package descent.ui.text.outline;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import descent.core.dom.ASTNode;

public class DElementSorterByPosition extends ViewerSorter {
	
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		ASTNode elem1 = (ASTNode) e1;
		ASTNode elem2 = (ASTNode) e2;
		return elem1.getStartPosition() < elem2.getStartPosition() ? -1 :
			elem1.getStartPosition() > elem2.getStartPosition() ? 1 : 0;
	}

}

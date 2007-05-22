package melnorme.util.ui.jface;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import util.tree.IElement;

/**
 * A default content provider for IElement's
 */
public abstract class ElementContentProvider implements ITreeContentProvider {

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

/*	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}
*/
	public boolean hasChildren(Object element) {
		return ((IElement) element).hasChildren();
	}

	public Object getParent(Object element) {
		return ((IElement) element).getParent();
	}

	public Object[] getChildren(Object parentElement) {
		return ((IElement) parentElement).getChildren();
	}

	public void dispose() {
	}
}

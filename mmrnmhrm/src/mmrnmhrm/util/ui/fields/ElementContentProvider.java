package mmrnmhrm.util.ui.fields;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import util.tree.IElement;

/**
 * A default content provider for IElement's
 */
public class ElementContentProvider implements ITreeContentProvider {
	protected IElement input;

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		input = (IElement) newInput;
	}

	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

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

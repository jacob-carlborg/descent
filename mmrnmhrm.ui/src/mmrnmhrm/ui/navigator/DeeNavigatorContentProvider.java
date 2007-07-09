package mmrnmhrm.ui.navigator;

import melnorme.miscutil.ExceptionAdapter;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;


public class DeeNavigatorContentProvider implements ITreeContentProvider {

	public Object[] getChildren(Object element) {
		/*if(element instanceof IContainer) {
			try {
				return ((IContainer) element).members();
			} catch (CoreException e) {
				throw ExceptionAdapter.unchecked(e);
			}
		}
		if(element instanceof DeeProject) {
			return ((DeeProject) element).sourceFolders.toArray();
		}*/
		
		
		return null;
	}

	public Object getParent(Object element) {
		if(element instanceof IResource) {
			return ((IResource) element).getParent();
		}
		return null;
	}

	public boolean hasChildren(Object element) {
		if(element instanceof IContainer) {
			try {
				return ((IContainer) element).members().length != 0;
			} catch (CoreException e) {
				throw ExceptionAdapter.unchecked(e);
			}
		}		
		return false;
	}

	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// Auto-generated method stub
	}

}

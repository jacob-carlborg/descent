package mmrnmhrm.ui.navigator;

import melnorme.miscutil.tree.IElement;
import mmrnmhrm.core.model.DeeModelManager;
import mmrnmhrm.core.model.DeeProject;
import mmrnmhrm.core.model.IDeeElement;
import mmrnmhrm.core.model.lang.ILangElement;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;


public class DeeNavigatorContentProvider implements ITreeContentProvider {

	public Object[] getChildren(Object element) {
		if(element instanceof IProject) {
			return DeeModelManager.getLangProject((IProject)element).getSourceFolders();
		}
		
		if(element instanceof DeeProject) {
			return ((DeeProject) element).getSourceFolders();
		}
		
		if(element instanceof IDeeElement) {
			return ((IDeeElement) element).getChildren();
		}
				
		return null;
	}

	public Object getParent(Object element) {
		if(element instanceof IElement) {
			return ((IElement) element).getParent();
		}
		if(element instanceof IResource) {
			return ((IResource) element).getParent();
		}
		return null;
	}

	public boolean hasChildren(Object element) {
		if(element instanceof ILangElement) {
			return ((IElement) element).hasChildren();
		}
		if(element instanceof IContainer) {
			try {
				return ((IContainer) element).members().length != 0;
			} catch (CoreException e) {
				return false;
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

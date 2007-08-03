package mmrnmhrm.ui.navigator;

import melnorme.miscutil.tree.IElement;
import melnorme.util.ui.swt.SWTUtil2;
import mmrnmhrm.core.ElementChangedEvent;
import mmrnmhrm.core.IElementChangedListener;
import mmrnmhrm.core.model.DeeModel;
import mmrnmhrm.core.model.DeeProject;
import mmrnmhrm.core.model.IDeeElement;
import mmrnmhrm.core.model.lang.ILangElement;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;


public class DeeNavigatorContentProvider implements ITreeContentProvider, IElementChangedListener {

	private Viewer viewer;
	
	public DeeNavigatorContentProvider() {
		DeeModel.addElementChangedListener(this);
	}
	
	public Object[] getChildren(Object element) {
		if(element instanceof IProject) {
			try {
				return DeeModel.getLangProject((IProject)element).getSourceRoots();
			} catch (CoreException e) {
				return null;
			}
		}
		
		if(element instanceof DeeProject) {
			try {
				return ((DeeProject) element).getSourceFolders();
			} catch (CoreException e) {
				return null;
			}
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
		this.viewer = viewer;
	}

	public void elementChanged(ElementChangedEvent event) {
		SWTUtil2.runInSWTThread(new Runnable() {
			public void run() {
				viewer.refresh();
			}
		});
	}

}

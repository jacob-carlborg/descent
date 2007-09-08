package mmrnmhrm.ui.navigator;

import melnorme.miscutil.tree.IElement;
import melnorme.util.ui.swt.SWTUtilExt;
import mmrnmhrm.core.model.DeeModel;
import mmrnmhrm.core.model.DeeProject;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;


public class DeeNavigatorContentProvider implements ITreeContentProvider, org.eclipse.dltk.core.IElementChangedListener {

	private Viewer viewer;
	
	public DeeNavigatorContentProvider() {
		//DLTKCore.addElementChangedListener(this);
	}
	
	public Object[] getChildren(Object element) {
		if(element instanceof IProject) {
			try {
				//dltkProj.open(null);
				return new DeeProject(DLTKCore.create(((IProject)element))).dltkProj.getChildren();
			} catch (CoreException e) {
				return null;
			}
		}
		
		if(element instanceof DeeProject) {
			try {
				//dltkProj.open(null);
				return ((DeeProject) element).dltkProj.getChildren();
			} catch (CoreException e) {
				return null;
			}
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
		if(element instanceof IElement) {
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

	public void elementChanged(org.eclipse.dltk.core.ElementChangedEvent event) {
		SWTUtilExt.runInSWTThread(new Runnable() {
			public void run() {
				viewer.refresh();
			}
		});
	}

}

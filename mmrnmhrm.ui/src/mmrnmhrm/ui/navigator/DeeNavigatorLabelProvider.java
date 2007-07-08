package mmrnmhrm.ui.navigator;

import mmrnmhrm.core.model.DeeModelManager;
import mmrnmhrm.core.model.DeeProject;
import mmrnmhrm.core.model.DeeSourceFolder;
import mmrnmhrm.core.model.IDeeElement;
import mmrnmhrm.core.model.IDeeSourceRoot;
import mmrnmhrm.ui.DeePluginImages;
import mmrnmhrm.ui.views.DeeElementImageProvider;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

public class DeeNavigatorLabelProvider implements ILabelProvider {

	
	public Image getImage(Object element) {
		if(element instanceof IDeeElement) 
			return DeeElementImageProvider.getElementImage((IDeeElement) element);
		
		if(element instanceof IFolder) {
			IFolder folder = (IFolder) element;
			DeeProject deeproj = DeeModelManager.getLangProject(folder.getProject());
			if(deeproj == null)
				return null;
			
			IDeeSourceRoot spentry = deeproj.getSourceRoot(folder);
			
			if(spentry instanceof DeeSourceFolder)
				return getImage(DeePluginImages.ELEM_SOURCEFOLDER);
			else 
				return null;
		} else return null;
	}

	public String getText(Object element) {
		if(element instanceof IResource)
			return ((IResource) element).getName();
		else  
			return "<UNKNOWN>";
	}
	
	public boolean isLabelProperty(Object element, String property) {
		// Auto-generated method stub
		return false;
	}

	public void addListener(ILabelProviderListener listener) {
	}


	public void removeListener(ILabelProviderListener listener) {
	}

	public void dispose() {
	}
}

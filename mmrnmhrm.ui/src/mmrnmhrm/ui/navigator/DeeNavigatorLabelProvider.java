package mmrnmhrm.ui.navigator;

import melnorme.miscutil.tree.IElement;
import mmrnmhrm.core.model.DeeModel;
import mmrnmhrm.core.model.DeeProject;
import mmrnmhrm.core.model.DeeSourceFolder;
import mmrnmhrm.core.model.IDeeElement;
import mmrnmhrm.core.model.IDeeSourceRoot;
import mmrnmhrm.ui.DeePluginImages;
import mmrnmhrm.ui.views.DeeElementImageProvider;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import descent.internal.compiler.parser.ast.IASTNode;

public class DeeNavigatorLabelProvider implements ILabelProvider {

	
	public Image getImage(Object element) {
		if(element instanceof IElement) 
			return DeeElementImageProvider.getElementImage((IElement) element);
		
		if(element instanceof IFolder) {
			IFolder folder = (IFolder) element;
			DeeProject deeproj = DeeModel.getLangProject(folder.getProject());
			if(deeproj == null)
				return null;
			
			IDeeSourceRoot spentry = null;
			try {
				spentry = deeproj.getSourceRoot(folder);
			} catch (CoreException e) {
			}
			
			if(spentry instanceof DeeSourceFolder)
				return getImage(DeePluginImages.ELEM_SOURCEFOLDER);
			else 
				return null;
		} else 
			return null;
	}

	public String getText(Object element) {
		if(element instanceof IDeeElement) {
			return ((IDeeElement) element).getElementName();
		} else if(element instanceof IASTNode) {
			return ((IElement) element).toString();
		} 
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

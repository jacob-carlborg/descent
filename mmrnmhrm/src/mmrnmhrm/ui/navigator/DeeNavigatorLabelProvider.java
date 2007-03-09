package mmrnmhrm.ui.navigator;

import mmrnmhrm.core.DeeUI;
import mmrnmhrm.core.model.DeeModel;
import mmrnmhrm.core.model.DeeProject;
import mmrnmhrm.core.model.DeeSourceFolder;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import dtool.dom.base.DefUnit;

public class DeeNavigatorLabelProvider implements ILabelProvider {

	public void dispose() {
	}
	
	public static final IPath ICONS_PATH= new Path("$nl$/icons/"); //$NON-NLS-1$


	public Image getImage(Object element) {
		// TODO: use image registry
		if(element instanceof DeeSourceFolder)
			return DeeUI.getImageDescriptor("icons/obj16/packagefolder_obj.gif").createImage();
		else if(element instanceof IFolder) {
			IFolder folder = (IFolder) element;
			DeeProject deeproj = DeeModel.getDeeProject(folder.getProject());

			if(deeproj != null && deeproj.containsElement(element))
				return DeeUI.getImageDescriptor("icons/obj16/packagefolder_obj.gif").createImage();
			else 
				return null;
		}
		else
			return null;
	}

	public String getText(Object element) {
		if(element instanceof IResource)
			return ((IResource) element).getName();
		else if(element instanceof DefUnit)
			return ((DefUnit) element).symbol.name;
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

}

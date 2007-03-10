package mmrnmhrm.ui.navigator;

import mmrnmhrm.ui.outline.DeeElementImageProvider;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

public class DeeNavigatorLabelProvider implements ILabelProvider {

	
	public Image getImage(Object element) {
		return DeeElementImageProvider.getLabelImage(element);
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

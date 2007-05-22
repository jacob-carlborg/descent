package melnorme.util.ui.jface;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;

import util.Assert;

public abstract class SimpleLabelProvider implements ILabelProvider {

	public void addListener(ILabelProviderListener listener) {
	}

	public void removeListener(ILabelProviderListener listener) {
	}

	public void dispose() {
	}

	public boolean isLabelProperty(Object element, String property) {
		Assert.fail("This label provider does not support property based updating");
		return true;
	}
	
    public String getText(Object element) {
        return element == null ? "" : element.toString();//$NON-NLS-1$
    }

}

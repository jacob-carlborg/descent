package descent.internal.ui.filters;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * Filters out all files with the given extension.
 */
public abstract class FileExtensionFileFilter extends ViewerFilter {
	
	private final String ending;
	
	public FileExtensionFileFilter(String extension) {
		this.ending = "." + extension.toLowerCase(); //$NON-NLS-1$
	}
	
	/**
	 * Returns the result of this filter, when applied to the
	 * given inputs.
	 *
	 * @return Returns true if element should be included in filtered set
	 */
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		IResource resource = null;
		
		if (element instanceof IResource) {
			 resource = (IResource) element;
		} else if (element instanceof IAdaptable) {
			IAdaptable adaptable = (IAdaptable) element;
			resource = (IResource) adaptable.getAdapter(IResource.class);
		}
		
		if (resource == null) {
			return true;
		}
		
		return !resource.getName().toLowerCase().endsWith(ending);
	}

}

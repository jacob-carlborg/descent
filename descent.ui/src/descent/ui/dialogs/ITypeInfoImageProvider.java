package descent.ui.dialogs;

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * A special image descriptor provider for {@link ITypeInfoRequestor}.
 * <p>
 * The interface should be implemented by clients wishing to provide special
 * images inside the type selection dialog.
 * </p>
 * 
 * @since 3.2
 */
public interface ITypeInfoImageProvider {

	/**
	 * Returns the image descriptor for the type represented by the 
	 * given {@link ITypeInfoRequestor}.
	 * <p>
	 * Note, that this method may be called from non UI threads.
	 * </p>
	 * 
	 * @param typeInfoRequestor the {@link ITypeInfoRequestor} to access
	 *  information for the type under inspection
	 * 
	 * @return the image descriptor or <code>null</code> to use the default
	 *  image
	 */
	public ImageDescriptor getImageDescriptor(ITypeInfoRequestor typeInfoRequestor);
	
}

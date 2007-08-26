package descent.ui.dialogs;

/**
 * A filter to select {@link ITypeInfoRequestor} objects.
 * <p>
 * The interface should be implemented by clients wishing to provide special
 * filtering to the type selection dialog.
 * </p>
 * 
 * @since 3.2
 */
public interface ITypeInfoFilterExtension {
	
	/**
	 * Returns whether the given type makes it into the list or
	 * not.
	 * 
	 * @param typeInfoRequestor the <code>ITypeInfoRequestor</code> to 
	 *  used to access data for the type under inspection
	 * 
	 * @return whether the type is selected or not
	 */
	public boolean select(ITypeInfoRequestor typeInfoRequestor);
	
}

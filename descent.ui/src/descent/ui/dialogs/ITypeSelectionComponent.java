package descent.ui.dialogs;

/**
 * Interface to access the type selection component hosting a 
 * type selection extension.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @since 3.2
 */
public interface ITypeSelectionComponent {
	
	/**
	 * Triggers a search inside the type component with the 
	 * current settings. 
	 */
	public void triggerSearch();
}
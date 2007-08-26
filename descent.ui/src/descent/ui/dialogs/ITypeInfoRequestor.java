package descent.ui.dialogs;

/**
 * An interfaces to give access to the type presented in type
 * selection dialogs like the open type dialog.
 * <p>
 * Please note that <code>ITypeInfoRequestor</code> objects <strong>don't
 * </strong> have value semantic. The state of the object might change over 
 * time especially since objects are reused for different call backs. 
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * @since 3.2
 */
public interface ITypeInfoRequestor {
	
	/**
	 * Returns the type's modifiers. The modifiers can be 
	 * inspected using the class {@link org.eclipse.jdt.core.Flags}.
	 * 
	 * @return the type's modifiers
	 */
	public int getModifiers();
	
	/**
	 * Returns the type name.
	 * 
	 * @return the info's type name.
	 */
	public String getTypeName();
	
	/**
	 * Returns the package name.
	 * 
	 * @return the info's package name.
	 */ 
	public String getPackageName();

	/**
	 * Returns a dot separated string of the enclosing types or an 
	 * empty string if the type is a top level type.
	 * 
	 * @return a dot separated string of the enclosing types
	 */
	public String getEnclosingName();
}
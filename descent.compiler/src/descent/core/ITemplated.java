package descent.core;

/**
 * An element that can possible have template parameters.
 */
public interface ITemplated extends ISourceReference {
	
	/**
	 * Returns whether this type represents a template, or is a templated
	 * element.
	 *
	 * @exception JavaModelException if this element does not exist or if an
	 *		exception occurs while accessing its corresponding resource.
	 * @return true if this type represents a template, or is a templated element,
	 * false otherwise
	 * @since 3.0
	 */
	boolean isTemplate() throws JavaModelException;
	
	/**
	 * Returns the formal type parameters for this type.
	 * Returns an empty array if this type has no formal type parameters.
	 *
	 * @exception JavaModelException if this element does not exist or if an
	 *      exception occurs while accessing its corresponding resource.
	 * @return the formal type parameters of this type,
	 * in the order declared in the source, an empty array if none
	 * @since 3.1
	 */
	ITypeParameter[] getTypeParameters() throws JavaModelException;
	
	/**
	 * Returns the type parameter declared in this type with the given name.
	 * This is a handle-only method. The type parameter may or may not exist.
	 * 
	 * @param name the given simple name
	 * @return the type parameter declared in this type with the given name
	 * @since 3.1
	 */
	ITypeParameter getTypeParameter(String name);

}

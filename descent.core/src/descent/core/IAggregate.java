package descent.core;

/**
 * An aggreagate is a class, interface, struct or union.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 */
public interface IAggregate extends IDeclaration {
	
	/**
	 * Returns the names of interfaces or classes that this type implements or extends,
	 * in the order in which they are listed in the source.
	 *
	 * @exception JavaModelException if this element does not exist or if an
	 *		exception occurs while accessing its corresponding resource.
	 * @return  the names of interfaces that this type implements or extends, in the order in which they are listed in the source, 
	 * an empty collection if none
	 */
	String[] getSuperNames() throws JavaModelException;
	
	/**
	 * Returns the flags of the interfaces or classes that this type implements or extends,
	 * in the order in which they are listed in the source.
	 *
	 * @exception JavaModelException if this element does not exist or if an
	 *		exception occurs while accessing its corresponding resource.
	 * @return  the names of interfaces that this type implements or extends, in the order in which they are listed in the source, 
	 * an empty collection if none
	 */
	int[] getSuperFlags() throws JavaModelException;
	
	/**
	 * Returns whether this aggregate represents a class.
	 *
	 * @exception JavaModelException if this element does not exist or if an
	 *		exception occurs while accessing its corresponding resource.
	 * @return true if this type represents a class, false otherwise
	 */
	boolean isClass() throws JavaModelException;
	
	/**
	 * Returns whether this aggregate represents an interface.
	 *
	 * @exception JavaModelException if this element does not exist or if an
	 *		exception occurs while accessing its corresponding resource.
	 * @return true if this type represents a class, false otherwise
	 */
	boolean isInterface() throws JavaModelException;
	
	/**
	 * Returns whether this aggregate represents a struct.
	 *
	 * @exception JavaModelException if this element does not exist or if an
	 *		exception occurs while accessing its corresponding resource.
	 * @return true if this type represents a class, false otherwise
	 */
	boolean isStruct() throws JavaModelException;
	
	/**
	 * Returns whether this aggregate represents a union.
	 *
	 * @exception JavaModelException if this element does not exist or if an
	 *		exception occurs while accessing its corresponding resource.
	 * @return true if this type represents a class, false otherwise
	 */
	boolean isUnion() throws JavaModelException;

}

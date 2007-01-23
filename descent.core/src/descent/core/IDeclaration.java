package descent.core;

/**
 * Represents a declaration.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 */
public interface IDeclaration extends IJavaElement, IParent, ISourceReference, ISourceManipulation {
	
	/**
	 * Returns the modifier flags for this member. The flags can be examined using class
	 * <code>Flags</code>.
	 * <p>
	 * Note that only flags as indicated in the source are returned. Thus if an interface
	 * defines a method <code>void myMethod();</code> the flags don't include the
	 * 'public' flag.
	 *
	 * @exception JavaModelException if this element does not exist or if an
	 *      exception occurs while accessing its corresponding resource.
	 * @return the modifier flags for this member
	 * @see Flags
	 */
	int getFlags() throws JavaModelException;
	
	/**
	 * Returns the DDoc ranges.
	 *
	 * @exception JavaModelException if this element does not exist or if an
	 *      exception occurs while accessing its corresponding resource.
	 * @return source ranges corresponding to the ddocs sources
	 */
	ISourceRange[] getDDocRanges() throws JavaModelException;
	
	/**
	 * Returns the source range of this member's simple name,
	 * or <code>null</code> if this member does not have a name
	 * (for example, a debug declaration).
	 *
	 * @exception JavaModelException if this element does not exist or if an
	 *      exception occurs while accessing its corresponding resource.
	 * @return the source range of this member's simple name,
	 * or <code>null</code> if this member does not have a name
	 * (for example, a debug declaration)
	 */
	ISourceRange getNameRange() throws JavaModelException;
	
	/**
	 * Returns the position relative to the order this member is defined in the source.
	 * Numbering starts at 1 (thus the first occurrence is occurrence 1, not occurrence 0).
	 * <p>
	 * Two members m1 and m2 that are equal (e.g. 2 fields with the same name in 
	 * the same type) can be distinguished using their occurrence counts. If member 
	 * m1 appears first in the source, it will have an occurrence count of 1. If member 
	 * m2 appears right after member m1, it will have an occurrence count of 2.
	 * </p><p>
	 * This is a handle-only method.  The member may or may not be present.
	 * </p>
	 * 
	 * @return the position relative to the order this member is defined in the source
	 */
	int getOccurrenceCount();

}

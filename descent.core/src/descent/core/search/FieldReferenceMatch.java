package descent.core.search;

import org.eclipse.core.resources.IResource;

import descent.core.IJavaElement;

/**
 * A Java search match that represents a field reference.
 * The element is the inner-most enclosing member that references this field.
 * <p>
 * This class is intended to be instantiated and subclassed by clients.
 * </p>
 * 
 * @since 3.0
 */
public class FieldReferenceMatch extends SearchMatch {

	private boolean isReadAccess;
	private boolean isWriteAccess;

	/**
	 * Creates a new field reference match.
	 * 
	 * @param enclosingElement the inner-most enclosing member that references this field
	 * @param accuracy one of {@link #A_ACCURATE} or {@link #A_INACCURATE}
	 * @param offset the offset the match starts at, or -1 if unknown
	 * @param length the length of the match, or -1 if unknown
	 * @param isReadAccess whether the match represents a read access
	 * @param isWriteAccess whethre the match represents a write access
	 * @param insideDocComment <code>true</code> if this search match is inside a doc
	 * comment, and <code>false</code> otherwise
	 * @param participant the search participant that created the match
	 * @param resource the resource of the element
	 */
	public FieldReferenceMatch(IJavaElement enclosingElement, int accuracy, int offset, int length, boolean isReadAccess, boolean isWriteAccess, boolean insideDocComment, SearchParticipant participant, IResource resource) {
		super(enclosingElement, accuracy, offset, length, participant, resource);
		this.isReadAccess = isReadAccess;
		this.isWriteAccess = isWriteAccess;
		setInsideDocComment(insideDocComment);
	}
	
	/**
	 * Returns whether the field reference is a read access to the field.
	 * Note that a field reference can be read and written at once in case of compound assignments (e.g. i += 0;)
	 * 
	 * @return whether the field reference is a read access to the field.
	 */
	public final boolean isReadAccess() {
		return this.isReadAccess;
	}

	/**
	 * Returns whether the field reference is a write access to the field.
	 * Note that a field reference can be read and written at once in case of compound assignments (e.g. i += 0;)
	 * 
	 * @return whether the field reference is a write access to the field.
	 */
	public final boolean isWriteAccess() {
		return this.isWriteAccess;
	}
	
}

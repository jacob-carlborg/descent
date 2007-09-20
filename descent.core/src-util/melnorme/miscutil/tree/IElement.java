package melnorme.miscutil.tree;

/**
 * Interface for tree elements.
 */
public interface IElement {
	// Empty array for optimization
	IElement[] NO_ELEMENTS = new IElement[0];
	
	/** Returns the parent of this node, or <code>null</code> if none. */
	IElement getParentBruno();

	/**
	 * Returns whether this element has one or more immediate children. This is
	 * a convenience method, and may be more efficient than testing whether
	 * <code>getChildren</code> is an empty array.
	 */
	boolean hasChildren();

	/** Returns the node's children. */
	IElement[] getChildren();

	/** Returns this element's kind encoded as an integer. */
	int getElementType();
}

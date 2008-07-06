package descent.core;

/**
 * An element that can have ddoc attached.
 * 
 * <p>This interface is not intended to be implemented by clients.</p>
 */
public interface IDocumented extends IJavaElement {
	
	/**
	 * Returns the Javadoc range if this element is from source or if this element
	 * is a binary element with an attached source, null otherwise.
	 * 
	 * <p>If this element is from source, the javadoc range is 
	 * extracted from the corresponding source.</p>
	 * <p>If this element is from a binary, the javadoc is extracted from the
	 * attached source if present.</p>
	 * <p>If this element's openable is not consistent, then null is returned.</p>
	 *
	 * @exception JavaModelException if this element does not exist or if an
	 *      exception occurs while accessing its corresponding resource.
	 * @return a source range corresponding to the javadoc source or <code>null</code>
	 * if no source is available, this element has no javadoc comment or
	 * this element's openable is not consistent
	 * @see IOpenable#isConsistent()
	 * @since 3.2
	 */
	ISourceRange[] getJavadocRanges() throws JavaModelException;

}

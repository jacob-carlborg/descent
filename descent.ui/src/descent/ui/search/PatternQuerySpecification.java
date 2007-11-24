package descent.ui.search;

import descent.core.search.IJavaSearchScope;


/**
 * <p>
 * Describes a search query by giving a textual pattern to search for.
 * </p>
 * <p>
 * This class is not intended to be instantiated or subclassed by clients.
 * </p>
 * 
 * @see descent.ui.search.QuerySpecification
 * 
 * @since 3.0
 */
public class PatternQuerySpecification extends QuerySpecification {
	private String fPattern;
	private int fSearchFor;
	private boolean fCaseSensitive;

	/**
	 * @param pattern
	 *            The string that the query should search for.
	 * @param searchFor
	 *            What kind of <code>IJavaElement</code> the query should search for.
	 * @param caseSensitive
	 *            Whether the query should be case sensitive.
	 * @param limitTo
	 *            The kind of occurrence the query should search for.
	 * @param scope
	 *            The scope to search in.
	 * @param scopeDescription
	 *            A human readable description of the search scope.
	 * 
	 * @see descent.core.search.SearchPattern#createPattern(java.lang.String, int, int, int)
	 */
	public PatternQuerySpecification(String pattern, int searchFor, boolean caseSensitive, int limitTo, IJavaSearchScope scope, String scopeDescription) {
		super(limitTo, scope, scopeDescription);
		fPattern= pattern;
		fSearchFor= searchFor;
		fCaseSensitive= caseSensitive;
	}

	/**
	 * Whether the query should be case sensitive.
	 * @return Whether the query should be case sensitive.
	 */
	public boolean isCaseSensitive() {
		return fCaseSensitive;
	}

	/**
	 * Returns the search pattern the query should search for. 
	 * @return the search pattern
	 * @see descent.core.search.SearchPattern#createPattern(java.lang.String, int, int, int)
	 */
	public String getPattern() {
		return fPattern;
	}

	/**
	 * Returns what kind of <code>IJavaElement</code> the query should search for.
	 * 
	 * @return The kind of <code>IJavaElement</code> to search for.
	 * 
	 * @see descent.core.search.IJavaSearchConstants
	 */
	public int getSearchFor() {
		return fSearchFor;
	}
}

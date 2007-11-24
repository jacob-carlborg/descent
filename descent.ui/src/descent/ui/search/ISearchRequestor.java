package descent.ui.search;

import org.eclipse.search.ui.text.Match;

/**
 * A callback interface to report matches against. This class serves as a bottleneck and minimal interface
 * to report matches to the Java search infrastructure. Query participants will be passed an
 * instance of this interface when their <code>search(...)</code> method is called.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 *
 * @since 3.0
 */
public interface ISearchRequestor {
	/**
	 * Adds a match to the search that issued this particular {@link ISearchRequestor}.
	 * @param match The match to be reported.
	 */
	void reportMatch(Match match);
}

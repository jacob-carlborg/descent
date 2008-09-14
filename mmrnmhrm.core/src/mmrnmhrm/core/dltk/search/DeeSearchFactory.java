package mmrnmhrm.core.dltk.search;

import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.dltk.core.ISearchPatternProcessor;
import org.eclipse.dltk.core.search.AbstractSearchFactory;
import org.eclipse.dltk.core.search.IDLTKSearchScope;
import org.eclipse.dltk.core.search.IMatchLocatorParser;
import org.eclipse.dltk.core.search.SearchPattern;
import org.eclipse.dltk.core.search.SearchRequestor;
import org.eclipse.dltk.core.search.matching.MatchLocator;

public class DeeSearchFactory extends AbstractSearchFactory {

	@Override
	public MatchLocator createMatchLocator(SearchPattern pattern, SearchRequestor requestor,
			IDLTKSearchScope scope, SubProgressMonitor monitor) {
		return new DeeNeoMatchLocator(pattern, requestor, scope, monitor);
	}

	public IMatchLocatorParser createMatchParser(MatchLocator locator) {
		return new DeeNeoMatchLocatorParser(locator);
	}

	@Override
	public ISearchPatternProcessor createSearchPatternProcessor() {
		// XXX: DLTK: this does what?
		return super.createSearchPatternProcessor();
	}

}

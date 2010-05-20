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
		return new DeeMatchLocator(pattern, requestor, scope, monitor);
	}
	
	@Override
	public IMatchLocatorParser createMatchParser(MatchLocator locator) {
		return new DeeMatchLocatorParser(locator);
	}
	
	@Override
	public ISearchPatternProcessor createSearchPatternProcessor() {
		return DeeSearchPatterProcessor.instance;
	}
	
}

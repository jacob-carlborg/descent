package mmrnmhrm.core;

import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.dltk.core.ISearchFactory;
import org.eclipse.dltk.core.search.DLTKSearchParticipant;
import org.eclipse.dltk.core.search.IDLTKSearchScope;
import org.eclipse.dltk.core.search.IMatchLocatorParser;
import org.eclipse.dltk.core.search.SearchPattern;
import org.eclipse.dltk.core.search.SearchRequestor;
import org.eclipse.dltk.core.search.indexing.SourceIndexerRequestor;
import org.eclipse.dltk.core.search.matching.MatchLocator;

public class SearchFactory1 implements ISearchFactory {

	@Override
	public MatchLocator createMatchLocator(SearchPattern pattern,
			SearchRequestor requestor, IDLTKSearchScope scope,
			SubProgressMonitor monitor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IMatchLocatorParser createMatchParser(MatchLocator locator) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DLTKSearchParticipant createSearchParticipant() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SourceIndexerRequestor createSourceRequestor() {
		// TODO Auto-generated method stub
		return null;
	}

}

package descent.internal.ui.search;

import org.eclipse.core.runtime.CoreException;
import descent.core.IJavaElement;
import descent.core.search.FieldReferenceMatch;
import descent.core.search.SearchMatch;
import descent.core.search.SearchParticipant;
import descent.core.search.SearchRequestor;

public class NewSearchResultCollector extends SearchRequestor {
	private JavaSearchResult fSearch;
	private boolean fIgnorePotentials;

	public NewSearchResultCollector(JavaSearchResult search, boolean ignorePotentials) {
		super();
		fSearch= search;
		fIgnorePotentials= ignorePotentials;
	}
	
	public void acceptSearchMatch(SearchMatch match) throws CoreException {
		IJavaElement enclosingElement= (IJavaElement) match.getElement();
		if (enclosingElement != null) {
			if (fIgnorePotentials && (match.getAccuracy() == SearchMatch.A_INACCURATE))
				return;
			boolean isWriteAccess= false;
			boolean isReadAccess= false;
			if (match instanceof FieldReferenceMatch) {
				FieldReferenceMatch fieldRef= ((FieldReferenceMatch)match);
				isWriteAccess= fieldRef.isWriteAccess();
				isReadAccess= fieldRef.isReadAccess();
			}
			fSearch.addMatch(new JavaElementMatch(enclosingElement, match.getRule(), match.getOffset(), match.getLength(), match.getAccuracy(), isReadAccess, isWriteAccess, match.isInsideDocComment()));
		}
	}

	public void beginReporting() {
	}

	public void endReporting() {
	}

	public void enterParticipant(SearchParticipant participant) {
	}

	public void exitParticipant(SearchParticipant participant) {
	}


}

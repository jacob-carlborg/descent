package descent.internal.ui.search;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import org.eclipse.core.resources.IFile;

import org.eclipse.jface.resource.ImageDescriptor;

import org.eclipse.ui.IEditorPart;

import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.SearchResultEvent;
import org.eclipse.search.ui.text.AbstractTextSearchResult;
import org.eclipse.search.ui.text.IEditorMatchAdapter;
import org.eclipse.search.ui.text.IFileMatchAdapter;
import org.eclipse.search.ui.text.Match;
import org.eclipse.search.ui.text.MatchEvent;

import descent.core.IClassFile;
import descent.core.ICompilationUnit;
import descent.core.IJavaElement;
import descent.core.IParent;
import descent.core.JavaModelException;

import descent.ui.search.IMatchPresentation;

import descent.internal.ui.JavaPlugin;

public class JavaSearchResult extends AbstractTextSearchResult implements IEditorMatchAdapter, IFileMatchAdapter {
	
	public static class MatchFilterEvent extends SearchResultEvent {
		private static final long serialVersionUID= 1234L;
		
		private final MatchFilter[] fActivatedFilters;

		public MatchFilterEvent(ISearchResult searchResult, MatchFilter[] activatedFilters) {
			super(searchResult);
			fActivatedFilters= activatedFilters;
		}
		
		public MatchFilter[] getActivatedFilters() {
			return fActivatedFilters;
		}
	}
	
	private JavaSearchQuery fQuery;
	private Map fElementsToParticipants;
	private static final Match[] NO_MATCHES= new Match[0];
	
	private MatchFilter[] fActivatedMatchFilters;
	
	public JavaSearchResult(JavaSearchQuery query) {
		fQuery= query;
		fElementsToParticipants= new HashMap();
		fActivatedMatchFilters= MatchFilter.getLastUsedFilters();
	}

	public ImageDescriptor getImageDescriptor() {
		return fQuery.getImageDescriptor();
	}

	public String getLabel() {
		return fQuery.getResultLabel(getMatchCount());
	}

	public String getTooltip() {
		return getLabel();
	}
	
	public void setActivatedFilters(MatchFilter[] matchFilters) {
		fActivatedMatchFilters= matchFilters;
		MatchFilter.setLastUsedFilters(matchFilters);
		updateFilterStateForAllMarkers();
		fireChange(new MatchFilterEvent(this, matchFilters));
	}
	
	public MatchFilter[] getActivatedMatchFilters() {
		return fActivatedMatchFilters;
	}
	
	public boolean hasMatchFilterActivated(MatchFilter filter) {
		String id= filter.getID();
		for (int i= 0; i < fActivatedMatchFilters.length; i++) {
			if (fActivatedMatchFilters[i].getID().equals(id)) {
				return true;
			}
		}
		return false;
	}
		
	protected void fireChange(SearchResultEvent e) {
		if (e instanceof MatchEvent && ((MatchEvent) e).getKind() == MatchEvent.ADDED) {
			// initialize all new markers
			updateFilterState(((MatchEvent) e).getMatches());
		}
		super.fireChange(e);
	}
	
	private void updateFilterStateForAllMarkers() {
		Object[] elements= getElements();
		for (int i= 0; i < elements.length; i++) {
			updateFilterState(getMatches(elements[i]));
		}		
	}
	
	private void updateFilterState(Match[] matches) {
		for (int i= 0; i < matches.length; i++) {
			Object match= matches[i];
			if (match instanceof JavaElementMatch) {
				updateFilterState((JavaElementMatch) match);
			}
		}
	}
		
	private void updateFilterState(JavaElementMatch match) {	
		for (int i= 0; i < fActivatedMatchFilters.length; i++) {
			if (fActivatedMatchFilters[i].filters(match)) {
				match.setFiltered(true);
				return;
			}
		}
		match.setFiltered(false);
	}
	

	public Match[] computeContainedMatches(AbstractTextSearchResult result, IEditorPart editor) {
		return computeContainedMatches(editor.getEditorInput());
	}

	public Match[] computeContainedMatches(AbstractTextSearchResult result, IFile file) {
		return computeContainedMatches(file);
	}
	
	private Match[] computeContainedMatches(IAdaptable adaptable) {
		IJavaElement javaElement= (IJavaElement) adaptable.getAdapter(IJavaElement.class);
		Set matches= new HashSet();
		if (javaElement != null) {
			collectMatches(matches, javaElement);
		}
		IFile file= (IFile) adaptable.getAdapter(IFile.class);
		if (file != null) {
			collectMatches(matches, file);
		}
		if (!matches.isEmpty()) {
			return (Match[]) matches.toArray(new Match[matches.size()]);
		}
		return NO_MATCHES;
	}
	
	
	private void collectMatches(Set matches, IFile element) {
		Match[] m= getMatches(element);
		if (m.length != 0) {
			for (int i= 0; i < m.length; i++) {
				matches.add(m[i]);
			}
		}
	}
	
	private void collectMatches(Set matches, IJavaElement element) {
		Match[] m= getMatches(element);
		if (m.length != 0) {
			for (int i= 0; i < m.length; i++) {
				matches.add(m[i]);
			}
		}
		if (element instanceof IParent) {
			IParent parent= (IParent) element;
			try {
				IJavaElement[] children= parent.getChildren();
				for (int i= 0; i < children.length; i++) {
					collectMatches(matches, children[i]);
				}
			} catch (JavaModelException e) {
				// we will not be tracking these results
			}
		}
	}
	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.ISearchResultCategory#getFile(java.lang.Object)
	 */
	public IFile getFile(Object element) {
		if (element instanceof IJavaElement) {
			IJavaElement javaElement= (IJavaElement) element;
			ICompilationUnit cu= (ICompilationUnit) javaElement.getAncestor(IJavaElement.COMPILATION_UNIT);
			if (cu != null) {
				return (IFile) cu.getResource();
			} else {
				IClassFile cf= (IClassFile) javaElement.getAncestor(IJavaElement.CLASS_FILE);
				if (cf != null)
					return (IFile) cf.getResource();
			}
			return null;
		}
		if (element instanceof IFile)
			return (IFile) element;
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.search2.ui.text.IStructureProvider#isShownInEditor(org.eclipse.search2.ui.text.Match,
	 *      org.eclipse.ui.IEditorPart)
	 */
	public boolean isShownInEditor(Match match, IEditorPart editor) {
		Object element= match.getElement();
		if (element instanceof IJavaElement) {
			element= ((IJavaElement) element).getOpenable(); // class file or compilation unit 
			return element != null && element.equals(editor.getEditorInput().getAdapter(IJavaElement.class));
		} else if (element instanceof IFile) {
			return element.equals(editor.getEditorInput().getAdapter(IFile.class));
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.ISearchResult#getQuery()
	 */
	public ISearchQuery getQuery() {
		return fQuery;
	}
	
	synchronized IMatchPresentation getSearchParticpant(Object element) {
		return (IMatchPresentation) fElementsToParticipants.get(element);
	}

	boolean addMatch(Match match, IMatchPresentation participant) {
		Object element= match.getElement();
		if (fElementsToParticipants.get(element) != null) {
			// TODO must access the participant id / label to properly report the error.
			JavaPlugin.log(new Status(IStatus.WARNING, JavaPlugin.getPluginId(), 0, "A second search participant was found for an element", null)); //$NON-NLS-1$
			return false;
		}
		fElementsToParticipants.put(element, participant);
		addMatch(match);
		return true;
	}
	
	public void removeAll() {
		synchronized(this) {
			fElementsToParticipants.clear();
		}
		super.removeAll();
	}
	
	public void removeMatch(Match match) {
		synchronized(this) {
			if (getMatchCount(match.getElement()) == 1)
				fElementsToParticipants.remove(match.getElement());
		}
		super.removeMatch(match);
	}
	public IFileMatchAdapter getFileMatchAdapter() {
		return this;
	}
	
	public IEditorMatchAdapter getEditorMatchAdapter() {
		return this;
	}

}

package descent.internal.core.search.matching;

import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;

import descent.core.ICompilationUnit;
import descent.core.IJavaElement;
import descent.core.IJavaProject;
import descent.core.search.IJavaSearchScope;
import descent.core.search.SearchDocument;
import descent.core.search.SearchParticipant;
import descent.core.search.SearchPattern;
import descent.core.search.SearchRequestor;
import descent.internal.core.index.Index;
import descent.internal.core.search.IndexQueryRequestor;
import descent.internal.core.search.JavaSearchParticipant;

// TODO JDT search: stub
public class MatchLocator {
	
//	 permanent state
	public SearchPattern pattern;
	//public PatternLocator patternLocator;
	public int matchContainer;
	public SearchRequestor requestor;
	public IJavaSearchScope scope;
	public IProgressMonitor progressMonitor;

	public MatchLocator(SearchPattern pattern, SearchRequestor requestor, IJavaSearchScope scope, SubProgressMonitor progressMonitor) {
		this.pattern = pattern;
		//this.patternLocator = PatternLocator.patternLocator(this.pattern);
		//this.matchContainer = this.patternLocator.matchContainer();
		this.requestor = requestor;
		this.scope = scope;
		this.progressMonitor = progressMonitor;
	}

	public static IJavaElement projectOrJarFocus(InternalSearchPattern pattern) {
		return pattern == null || pattern.focus == null ? null : getProjectOrJar(pattern.focus);
	}

	public static boolean isPolymorphicSearch(InternalSearchPattern pattern) {
		return pattern.isPolymorphicSearch();
	}
	
	public static IJavaElement getProjectOrJar(IJavaElement element) {
		/* TODO JDT jar
		while (!(element instanceof IJavaProject) && !(element instanceof JarPackageFragmentRoot)) {
			element = element.getParent();
		}
		return element;
		*/
		while (!(element instanceof IJavaProject)) {
			element = element.getParent();
		}
		return element;
	}

	public void locateMatches(SearchDocument[] indexMatches) {
		
	}

	public void locatePackageDeclarations(JavaSearchParticipant participant) {
		// TODO Auto-generated method stub
		
	}

	public static SearchPattern createAndPattern(SearchPattern leftPattern, SearchPattern rightPattern) {
		return null;
	}

	public static void setFocus(InternalSearchPattern pattern, IJavaElement focus) {
		pattern.focus = focus;
	}

	public static void findIndexMatches(SearchPattern pattern2, Index index, IndexQueryRequestor requestor2, SearchParticipant participant, IJavaSearchScope scope2, IProgressMonitor progressMonitor2) throws IOException {
		
	}

	public static SearchDocument[] addWorkingCopies(SearchPattern pattern2, SearchDocument[] indexMatches, ICompilationUnit[] workingCopies, SearchParticipant participant) {
		return null;
	}

}

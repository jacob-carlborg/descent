package descent.internal.ui.search;

import org.eclipse.search.ui.ISearchPageScoreComputer;

import descent.core.IJavaElement;

import descent.internal.ui.browsing.LogicalPackage;
import descent.internal.ui.javaeditor.IClassFileEditorInput;

public class JavaSearchPageScoreComputer implements ISearchPageScoreComputer {

	public int computeScore(String id, Object element) {
		if (!JavaSearchPage.EXTENSION_POINT_ID.equals(id))
			// Can't decide
			return ISearchPageScoreComputer.UNKNOWN;
		
		if (element instanceof IJavaElement || element instanceof IClassFileEditorInput || element instanceof LogicalPackage)
			return 90;
		
		return ISearchPageScoreComputer.LOWEST;
	}
}

package descent.internal.ui.search;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public abstract class JavaSearchContentProvider implements IStructuredContentProvider {
	protected final Object[] EMPTY_ARR= new Object[0];
	protected JavaSearchResult fResult;
	private JavaSearchResultPage fPage;

	JavaSearchContentProvider(JavaSearchResultPage page) {
		fPage= page;
	}
	
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		initialize((JavaSearchResult) newInput);
	}
	
	protected void initialize(JavaSearchResult result) {
		fResult= result;
	}
	
	public abstract void elementsChanged(Object[] updatedElements);
	public abstract void clear();

	public void filtersChanged(MatchFilter[] filters) {
	}
	
	
	public void dispose() {
		// nothing to do
	}

	JavaSearchResultPage getPage() {
		return fPage;
	}

}

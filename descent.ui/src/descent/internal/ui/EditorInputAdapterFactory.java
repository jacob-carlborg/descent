package descent.internal.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterFactory;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IStorageEditorInput;

import org.eclipse.search.ui.ISearchPageScoreComputer;

import descent.core.IJavaElement;

import descent.ui.JavaUI;

import descent.internal.ui.search.JavaSearchPageScoreComputer;
import descent.internal.ui.search.SearchUtil;

/**
 * Adapter factory to support basic UI operations for for editor inputs.
 */
public class EditorInputAdapterFactory implements IAdapterFactory {
	
	private static Class[] PROPERTIES= new Class[] {IJavaElement.class};
	
	private Object fSearchPageScoreComputer;

	public Class[] getAdapterList() {
		updateLazyLoadedAdapters();
		return PROPERTIES;
	}

	public Object getAdapter(Object element, Class key) {
		updateLazyLoadedAdapters();
		if (fSearchPageScoreComputer != null && ISearchPageScoreComputer.class.equals(key))
			return fSearchPageScoreComputer;
		
		if (IJavaElement.class.equals(key) && element instanceof IEditorInput) {
			IJavaElement je= JavaUI.getWorkingCopyManager().getWorkingCopy((IEditorInput)element); 
			if (je != null)
				return je;
			if (element instanceof IStorageEditorInput) {
				try {
					return ((IStorageEditorInput)element).getStorage().getAdapter(key);
				} catch (CoreException ex) {
					// Fall through
				}
			}
		}
		return null;
	}

	private void updateLazyLoadedAdapters() {
		if (fSearchPageScoreComputer == null && SearchUtil.isSearchPlugInActivated())
			createSearchPageScoreComputer();
	}
	
	private void createSearchPageScoreComputer() {
		fSearchPageScoreComputer= new JavaSearchPageScoreComputer();
		PROPERTIES= new Class[] {ISearchPageScoreComputer.class, IJavaElement.class};
	}
}

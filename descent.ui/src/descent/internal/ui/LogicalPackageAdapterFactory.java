package descent.internal.ui;

import org.eclipse.core.resources.mapping.ResourceMapping;

import org.eclipse.core.runtime.IAdapterFactory;


import org.eclipse.search.ui.ISearchPageScoreComputer;

import descent.internal.corext.util.JavaElementResourceMapping;

import descent.internal.ui.browsing.LogicalPackage;
import descent.internal.ui.search.JavaSearchPageScoreComputer;
import descent.internal.ui.search.SearchUtil;

/**
 * Implements basic UI support for LogicalPackage.
 */
public class LogicalPackageAdapterFactory implements IAdapterFactory {
	
	private static Class[] PROPERTIES= new Class[] {
		ResourceMapping.class
	};

	// Must be Object to allow lazy loading	
	private Object fSearchPageScoreComputer;
	
	public Class[] getAdapterList() {
		updateLazyLoadedAdapters();
		return PROPERTIES;
	}
	
	public Object getAdapter(Object element, Class key) {
		updateLazyLoadedAdapters();
		
		if (fSearchPageScoreComputer != null && ISearchPageScoreComputer.class.equals(key)) {
			return fSearchPageScoreComputer;
		} else if (ResourceMapping.class.equals(key)) {
			if (!(element instanceof LogicalPackage))
				return null;
			return JavaElementResourceMapping.create((LogicalPackage)element);
		}
		return null; 
	}
	
	private void updateLazyLoadedAdapters() {
		if (fSearchPageScoreComputer == null && SearchUtil.isSearchPlugInActivated())
			createSearchPageScoreComputer();
	}

	private void createSearchPageScoreComputer() {
		fSearchPageScoreComputer= new JavaSearchPageScoreComputer();
		PROPERTIES= new Class[] {
			ISearchPageScoreComputer.class,
			ResourceMapping.class
		};
	}
}

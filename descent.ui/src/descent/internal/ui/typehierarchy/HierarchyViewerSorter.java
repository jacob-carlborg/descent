package descent.internal.ui.typehierarchy;

import descent.core.IType;
import descent.core.ITypeHierarchy;

/**
  */
public class HierarchyViewerSorter extends AbstractHierarchyViewerSorter {
		
	private final TypeHierarchyLifeCycle fHierarchy;
	private boolean fSortByDefiningType;
	
	public HierarchyViewerSorter(TypeHierarchyLifeCycle cycle) {
		fHierarchy= cycle;
	}
	
	public void setSortByDefiningType(boolean sortByDefiningType) {
		fSortByDefiningType= sortByDefiningType;
	}
	
	/* (non-Javadoc)
	 * @see descent.internal.ui.typehierarchy.AbstractHierarchyViewerSorter#getTypeKind(descent.core.IType)
	 */
	protected long getTypeFlags(IType type) {
		ITypeHierarchy hierarchy= getHierarchy(type);
		if (hierarchy != null) {
			return fHierarchy.getHierarchy().getCachedFlags(type);
		}
		return 0;
	}
	
	/*
	 * (non-Javadoc)
	 * @see descent.internal.ui.typehierarchy.AbstractHierarchyViewerSorter#isSortByDefiningType()
	 */
	public boolean isSortByDefiningType() {
		return fSortByDefiningType;
	}

	/* (non-Javadoc)
	 * @see descent.internal.ui.typehierarchy.AbstractHierarchyViewerSorter#isSortAlphabetically()
	 */
	public boolean isSortAlphabetically() {
		return true;
	}

	/* (non-Javadoc)
	 * @see descent.internal.ui.typehierarchy.AbstractHierarchyViewerSorter#getHierarchy(descent.core.IType)
	 */
	protected ITypeHierarchy getHierarchy(IType type) {
		return fHierarchy.getHierarchy(); // hierarchy contains all types shown
	}

}

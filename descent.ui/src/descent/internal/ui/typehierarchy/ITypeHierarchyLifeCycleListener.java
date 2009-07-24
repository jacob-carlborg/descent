package descent.internal.ui.typehierarchy;

import descent.core.IType;


/**
 * Used by the TypeHierarchyLifeCycle to inform listeners about a change in the
 * type hierarchy
 */
public interface ITypeHierarchyLifeCycleListener {
	
	/**
	 * A Java element changed. 
	 */
	void typeHierarchyChanged(TypeHierarchyLifeCycle typeHierarchyProvider, IType[] changedTypes);

}

package descent.internal.ui.typehierarchy;

import java.util.List;

import org.eclipse.swt.widgets.Composite;

import org.eclipse.ui.IWorkbenchPart;

import descent.core.IType;
import descent.core.ITypeHierarchy;

/**
 * A viewer including the content provider for the subtype hierarchy.
 * Used by the TypeHierarchyViewPart which has to provide a TypeHierarchyLifeCycle
 * on construction (shared type hierarchy)
 */
public class SubTypeHierarchyViewer extends TypeHierarchyViewer {
	
	public SubTypeHierarchyViewer(Composite parent, TypeHierarchyLifeCycle lifeCycle, IWorkbenchPart part) {
		super(parent, new SubTypeHierarchyContentProvider(lifeCycle), lifeCycle, part);
	}

	/*
	 * @see TypeHierarchyViewer#getTitle
	 */	
	public String getTitle() {
		if (isMethodFiltering()) {
			return TypeHierarchyMessages.SubTypeHierarchyViewer_filtered_title; 
		} else {
			return TypeHierarchyMessages.SubTypeHierarchyViewer_title; 
		}
	}
	
	/*
	 * @see TypeHierarchyViewer#updateContent
	 */
	public void updateContent(boolean expand) {
		getTree().setRedraw(false);
		refresh();
		
		if (expand) {
			int expandLevel= 2;
			if (isMethodFiltering()) {
				expandLevel++;
			}
			expandToLevel(expandLevel);
		}
		getTree().setRedraw(true);
	}
	
	/**
	 * Content provider for the subtype hierarchy
	 */
	public static class SubTypeHierarchyContentProvider extends TypeHierarchyContentProvider {
		public SubTypeHierarchyContentProvider(TypeHierarchyLifeCycle lifeCycle) {
			super(lifeCycle);
		}
		
		protected final void getTypesInHierarchy(IType type, List res) {
			ITypeHierarchy hierarchy= getHierarchy();
			if (hierarchy != null) {
				IType[] types= hierarchy.getSubtypes(type);
				if (isObject(type)) {
					for (int i= 0; i < types.length; i++) {
						IType curr= types[i];
						if (!isAnonymousFromInterface(curr)) {
							res.add(curr);
						}
					}
				} else {
					for (int i= 0; i < types.length; i++) {
						res.add(types[i]);
					}
				}
			}
			
		}
		
		protected IType getParentType(IType type) {
			ITypeHierarchy hierarchy= getHierarchy();
			if (hierarchy != null) {
				return hierarchy.getSuperclass(type);
				// dont handle interfaces
			}
			return null;
		}

}
	
	
	
}

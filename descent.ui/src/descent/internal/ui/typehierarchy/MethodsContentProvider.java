package descent.internal.ui.typehierarchy;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

import descent.core.IType;
import descent.core.ITypeHierarchy;
import descent.core.JavaModelException;

import descent.ui.IWorkingCopyProvider;

import descent.internal.ui.JavaPlugin;

/**
 * Content provider used for the method view.
 * Allows also seeing methods inherited from base classes.
 */
public class MethodsContentProvider implements IStructuredContentProvider, IWorkingCopyProvider {
	
	private static final Object[] NO_ELEMENTS = new Object[0];
		
	private boolean fShowInheritedMethods;
	private TypeHierarchyLifeCycle fHierarchyLifeCycle;
	private TableViewer fViewer;
	
	public MethodsContentProvider(TypeHierarchyLifeCycle lifecycle) {
		fHierarchyLifeCycle= lifecycle;
		fShowInheritedMethods= false;
		fViewer= null;
	}
	
	/**
	 * Turn on / off showing of inherited methods
	 */
	public void showInheritedMethods(boolean show) {	
		if (show != fShowInheritedMethods) {
			fShowInheritedMethods= show;
			if (fViewer != null) {
				fViewer.refresh();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see IStructuredContentProvider#providesWorkingCopies()
	 */
	public boolean providesWorkingCopies() {
		return true;
	}	
	
	/**
	 * Returns true if inherited methods are shown
	 */
	public boolean isShowInheritedMethods() {
		return fShowInheritedMethods;
	}
	
	
	private void addAll(Object[] arr, List res) {
		if (arr != null) {
			for (int j= 0; j < arr.length; j++) {
				res.add(arr[j]);
			}
		}
	}		

	/*
	 * @see IStructuredContentProvider#getElements
	 */		
	public Object[] getElements(Object element) {
		if (element instanceof IType) {
			IType type= (IType)element;

			List res= new ArrayList();
			try {
				ITypeHierarchy hierarchy= fHierarchyLifeCycle.getHierarchy();
				if (fShowInheritedMethods && hierarchy != null) {
					IType[] allSupertypes= hierarchy.getAllSupertypes(type);
					// sort in from last to first: elements with same name
					// will show up in hierarchy order 
					for (int i= allSupertypes.length - 1; i >= 0; i--) {
						IType superType= allSupertypes[i];
						if (superType.exists()) {
							addAll(superType.getMethods(), res);
							addAll(superType.getInitializers(), res);
							addAll(superType.getFields(), res);
						}
					}
				}
				if (type.exists()) {
					addAll(type.getMethods(), res);
					addAll(type.getInitializers(), res);
					addAll(type.getFields(), res);
				}
			} catch (JavaModelException e) {
				JavaPlugin.log(e);
			}
			return res.toArray();
		}
		return NO_ELEMENTS;
	}		
	
	
	/*
	 * @see IContentProvider#inputChanged
	 */
	public void inputChanged(Viewer input, Object oldInput, Object newInput) {
		Assert.isTrue(input instanceof TableViewer);
	
		fViewer= (TableViewer) input;
	}
	
	/*
	 * @see IContentProvider#dispose
	 */	
	public void dispose() {
	}	

}

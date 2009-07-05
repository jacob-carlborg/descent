package descent.internal.ui.javadocexport;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import descent.core.ICompilationUnit;
import descent.core.IPackageFragment;
import descent.core.JavaModelException;

import descent.internal.ui.JavaPlugin;

public class JavadocMemberContentProvider implements ITreeContentProvider {

	/*
	 * @see ITreeContentProvider#getChildren(Object)
	 */
	public Object[] getChildren(Object parentElement) {
		return new Object[0];
	}

	/*
	 * @see ITreeContentProvider#getParent(Object)
	 */
	public Object getParent(Object element) {
		//@test
		//System.out.println(element.getClass().toString());
		if (element instanceof IPackageFragment)
			return ((IPackageFragment) element).getParent();
		return null;
	}

	/*
	 * @see ITreeContentProvider#hasChildren(Object)
	 */
	public boolean hasChildren(Object element) {
		try {
			if (element instanceof IPackageFragment) {
				IPackageFragment iPackageFragment= (IPackageFragment) element;
				return (iPackageFragment.getChildren().length > 0);
			}
		} catch (JavaModelException e) {
		}
		return false;
	}

	/*
	 * @see IStructuredContentProvider#getElements(Object)
	 */
	public Object[] getElements(Object inputElement) {
		try {
			if (inputElement instanceof IPackageFragment) {
				ICompilationUnit[] cu= ((IPackageFragment) inputElement).getCompilationUnits();
				return cu;
			}
		} catch (JavaModelException e) {
			JavaPlugin.log(e);
		}
		return new Object[0];
	}

	/*
	 * @see IContentProvider#dispose()
	 */
	public void dispose() {
	}

	/*
	 * @see IContentProvider#inputChanged(Viewer, Object, Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

}

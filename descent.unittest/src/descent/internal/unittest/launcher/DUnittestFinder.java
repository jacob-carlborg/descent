package descent.internal.unittest.launcher;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;

import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;

import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;

import descent.core.ICompilationUnit;
import descent.core.IInitializer;
import descent.core.IJavaElement;
import descent.core.IJavaProject;
import descent.core.IPackageFragment;
import descent.core.IPackageFragmentRoot;
import descent.core.IParent;
import descent.core.IType;
import descent.core.JavaCore;
import descent.core.JavaModelException;

public class DUnittestFinder
{
	/**
	 * Expectd number of unit tests.
	 */
	static final int LIST_PREALLOC = 100;
	
	/**
	 * Finds unit tests in a separate runnable. Safe to be called from the
	 * UI thread.
	 * 
	 * @param context  The context in which to run the search.
	 * @param elements The elements to search
	 * @return         An array of modules containing unit tests
	 */
	public static List<TestSpecification> findTests(
			IRunnableContext context, final Object[] elements)
			throws InvocationTargetException, InterruptedException
	{
		final List<TestSpecification> result = 
			new ArrayList<TestSpecification>(LIST_PREALLOC);

		if (elements.length > 0) {
			IRunnableWithProgress runnable = new IRunnableWithProgress()
			{
				public void run(IProgressMonitor pm) throws InterruptedException
				{
					findTestsInContainer(elements, result, pm);
				}
			};
			context.run(true, true, runnable);
		}
		return result;
	}
	
	/**
	 * Finds unit tests in a separate runnable (creating its own
	 * context to run in). Safe to be called in the UI thread.
	 * 
	 * @param elements The elements to search
	 * @return         An array of modules containing unit tests
	 */
	public static List<TestSpecification> findTests(final Object[] elements) 
			throws InvocationTargetException, InterruptedException
	{
		final List<TestSpecification> result = 
			new ArrayList<TestSpecification>(LIST_PREALLOC);

		if (elements.length > 0)
		{
			IRunnableWithProgress runnable = new IRunnableWithProgress()
			{
				public void run(IProgressMonitor pm) throws InterruptedException
				{
					findTestsInContainer(elements, result, pm);
				}
			};
			PlatformUI.getWorkbench().getProgressService().busyCursorWhile(runnable);
		}
		return result;
	}
	
	public static void findTestsInContainer(Object[] elements,
			List<TestSpecification> result, IProgressMonitor pm)
	{
		try {
			for (int i= 0; i < elements.length; i++) {
				Object container= DUnittestFinder.computeScope(elements[i]);
				if (container instanceof IJavaProject) {
					IJavaProject project= (IJavaProject) container;
					findTestsInProject(project, result, pm);
				} else if (container instanceof IPackageFragmentRoot) {
					IPackageFragmentRoot root= (IPackageFragmentRoot) container;
					findTestsInPackageFragmentRoot(root, result, pm);
				} else if (container instanceof IPackageFragment) {
					IPackageFragment fragment= (IPackageFragment) container;
					findTestsInPackageFragment(fragment, result, pm);
				} else if (container instanceof ICompilationUnit) {
					ICompilationUnit module= (ICompilationUnit) container;
					pm.beginTask("Finding unit tests", 1);
					findTestsInCompilationUnit(module, result);
					pm.worked(1);
					pm.done();
				}
			}			
		} catch (JavaModelException e) {
			// do nothing
		}
	}
	
	private static void findTestsInProject(IJavaProject project,
			List<TestSpecification> result,
			IProgressMonitor pm) throws JavaModelException {
		IPackageFragmentRoot[] roots= project.getPackageFragmentRoots();
		pm.beginTask("Finding unit tests", 100 * roots.length);
		for (int i= 0; i < roots.length; i++) {
			IPackageFragmentRoot root= roots[i];
			findTestsInPackageFragmentRoot(root, result,
					new SubProgressMonitor(pm, 100));
		}
		pm.done();
	}

	private static void findTestsInPackageFragmentRoot(IPackageFragmentRoot root, 
			List<TestSpecification> result,
			IProgressMonitor pm) throws JavaModelException {
		IJavaElement[] children= root.getChildren();
		pm.beginTask("Finding unit tests", 100 * children.length);
		for (int j= 0; j < children.length; j++) {
			IPackageFragment fragment= (IPackageFragment) children[j];
			findTestsInPackageFragment(fragment, result,
					new SubProgressMonitor(pm, 100));
		}
		pm.done();
	}

	private static void findTestsInPackageFragment(IPackageFragment fragment,
			List<TestSpecification> result,
			IProgressMonitor pm) throws JavaModelException {
		ICompilationUnit[] compilationUnits= fragment.getCompilationUnits();
		pm.beginTask("Finding unit tests", compilationUnits.length);
		for (int k= 0; k < compilationUnits.length; k++) {
			ICompilationUnit module = compilationUnits[k];
			findTestsInCompilationUnit(module, result);
			pm.worked(1);
		}
		pm.done();
	}
	
	private static void findTestsInCompilationUnit(ICompilationUnit module,
			List<TestSpecification> result) throws JavaModelException
	{
		testSearch(result, module.getFullyQualifiedName(), module);
	}
	
	private static void testSearch(List<TestSpecification> result, String prefix,
			IParent node) throws JavaModelException
	{
		short count = 0;
		IJavaElement[] children = node.getChildren();
		for(IJavaElement child : children)
		{
			if(child instanceof IType)
			{
				IType type = (IType) child;
				testSearch(result, prefix + "." + type.getElementName(), type);
			}
			
			if(child instanceof IInitializer)
			{
				IInitializer init = (IInitializer) child;
				if(init.isUnitTest())
				{
					String id = prefix + "." + count;
					String name = id; // TODO
					result.add(new TestSpecification(id, name, init));
					count++;
				}
			}
		}
	}
	
	/**
	 * Makes sure the element refers to the correct scope.
	 */
	private static Object computeScope(Object element) throws JavaModelException
	{
		if (element instanceof IFileEditorInput)
			element= ((IFileEditorInput) element).getFile();
		if (element instanceof IResource)
			element= JavaCore.create((IResource) element);
		return element;
	}
}

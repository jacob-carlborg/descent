package descent.internal.unittest.launcher;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	 * Finds unit tests in a separate runnable. Safe to be called from the
	 * UI thread.
	 * 
	 * @param context  The context in which to run the search.
	 * @param elements The elements to search
	 * @return         An array of modules containing unit tests
	 */
	public static Map<ICompilationUnit, String[]> findTests(
			IRunnableContext context, final Object[] elements)
			throws InvocationTargetException, InterruptedException
	{
		final Map<ICompilationUnit, String[]> result = 
			new HashMap<ICompilationUnit, String[]>();

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
	public static Map<ICompilationUnit, String[]> findTests(final Object[] elements) 
			throws InvocationTargetException, InterruptedException
	{
		final Map<ICompilationUnit, String[]> result = 
			new HashMap<ICompilationUnit, String[]>();

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
	
	private static void findTestsInContainer(Object[] elements,
			Map<ICompilationUnit, String[]> result, IProgressMonitor pm)
	{
		try {
			for (int i= 0; i < elements.length; i++) {
				Object container= DUnittestFinder.computeScope(elements[i]);
				if (container instanceof IJavaProject) {
					IJavaProject project= (IJavaProject) container;
					findTestsInProject(project, result);
				} else if (container instanceof IPackageFragmentRoot) {
					IPackageFragmentRoot root= (IPackageFragmentRoot) container;
					findTestsInPackageFragmentRoot(root, result);
				} else if (container instanceof IPackageFragment) {
					IPackageFragment fragment= (IPackageFragment) container;
					findTestsInPackageFragment(fragment, result);
				} else if (container instanceof ICompilationUnit) {
					ICompilationUnit module= (ICompilationUnit) container;
					findTestsInCompilationUnit(module, result);
				}
			}			
		} catch (JavaModelException e) {
			// do nothing
		}
	}
	
	private static void findTestsInProject(IJavaProject project,
			Map<ICompilationUnit, String[]> result) throws JavaModelException {
		IPackageFragmentRoot[] roots= project.getPackageFragmentRoots();
		for (int i= 0; i < roots.length; i++) {
			IPackageFragmentRoot root= roots[i];
			findTestsInPackageFragmentRoot(root, result);
		}
	}

	private static void findTestsInPackageFragmentRoot(IPackageFragmentRoot root, 
			Map<ICompilationUnit, String[]> result) throws JavaModelException {
		IJavaElement[] children= root.getChildren();
		for (int j= 0; j < children.length; j++) {
			IPackageFragment fragment= (IPackageFragment) children[j];
			findTestsInPackageFragment(fragment, result);
		}
	}

	private static void findTestsInPackageFragment(IPackageFragment fragment,
			Map<ICompilationUnit, String[]> result) throws JavaModelException {
		ICompilationUnit[] compilationUnits= fragment.getCompilationUnits();
		for (int k= 0; k < compilationUnits.length; k++) {
			ICompilationUnit module = compilationUnits[k];
			findTestsInCompilationUnit(module, result);
		}
	}
	
	private static void findTestsInCompilationUnit(ICompilationUnit module,
			Map<ICompilationUnit, String[]> result) throws JavaModelException
	{
		List<String> tests = new ArrayList<String>(10);
		testSearch(tests, module.getModuleName(), module);
		if(tests.size() > 0)
			result.put(module, tests.toArray(new String[tests.size()]));
	}
	
	private static void testSearch(List<String> tests, String prefix,
			IParent node) throws JavaModelException
	{
		short count = 0;
		IJavaElement[] children = node.getChildren();
		for(IJavaElement child : children)
		{
			if(child instanceof IType)
			{
				IType type = (IType) child;
				testSearch(tests, type.getFullyQualifiedName(), type);
			}
			
			if(child instanceof IInitializer)
			{
				IInitializer init = (IInitializer) child;
				if(init.isUnitTest())
				{
					tests.add(prefix + "." + count);
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

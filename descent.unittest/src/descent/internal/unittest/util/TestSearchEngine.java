/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package descent.internal.unittest.util;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;

import descent.core.ICompilationUnit;
import descent.core.IJavaElement;
import descent.core.JavaCore;
import descent.core.JavaModelException;
import descent.core.search.IJavaSearchScope;
import descent.core.search.SearchEngine;
import descent.core.search.SearchMatch;
import descent.core.search.SearchRequestor;

/**
 * Class for finding D modules that contain unittests and unittests within those
 * modules.
 * 
 * @author Robert Fraser
 */
public class TestSearchEngine {
	
	/**
	 * Searches for unittest declarations within compilation units and collects
	 * the compilation units. Designed to add additional compilation units to a
	 * growing list, which it is instantiated with.
	 * 
	 * @author Robert Fraser
	 */
	private static class UnittestSearchResultCollector extends SearchRequestor
	{
		// List containing all the matching ICompilationUnits
		final List<ICompilationUnit> list;
		
		// Set containing all ICompilationUnits which contain a unittest
		// declaration.
		final Set<ICompilationUnit> matches = new HashSet<ICompilationUnit>();

		public UnittestSearchResultCollector(List<ICompilationUnit> $list)
		{
			list = $list;
		}

		public void acceptSearchMatch(SearchMatch match) throws CoreException
		{
			
			/* TODO Object enclosingElement= match.getElement();
			if (!(enclosingElement instanceof IMethod))
				return;

			IMethod method= (IMethod) enclosingElement;

			IType declaringType= method.getDeclaringType();
			if (fMatches.contains(declaringType) || fFailed.contains(declaringType))
				return;
			if (isTestOrTestSuite(declaringType)) {
				fMatches.add(declaringType);
			} else {
				fFailed.add(declaringType);
			} */
		}

		public void endReporting()
		{
			list.addAll(matches);
		}
	}
	
	/**
	 * Searchs within the given IJavaSearchScope for compilation units containing
	 * unit tests.
	 * 
	 * @param pm    An optional progress monitor that tracks progress of finding
	 *              unittest declarations.
	 * @param scope The scope in which to search
	 * @return      A list of all modules found that contain unittest
	 *              declarations.
	 */
	private List<ICompilationUnit> searchMethod(IProgressMonitor pm,
			IJavaSearchScope scope) throws CoreException
	{
		final List<ICompilationUnit> modulesFound = 
			new ArrayList<ICompilationUnit>(200);
		searchMethod(modulesFound, scope, pm);
		return modulesFound;
	}
	
	/**
	 * Adds modules from the given scope that have unittests to the list modules.
	 * 
	 * @param modules The list of currently found modules to be added to.
	 * @param scope   The scope in which to search for modules
	 * @param pm      An optional progress monitor to track the progress of the
	 *                search.
	 * @return        The module list passed in.
	 */
	private List<ICompilationUnit> searchMethod(List<ICompilationUnit> modules,
			IJavaSearchScope scope, IProgressMonitor pm) throws CoreException
	{
		SearchRequestor requestor = new UnittestSearchResultCollector(modules);
		
		/* TODO int matchRule= SearchPattern.R_EXACT_MATCH | SearchPattern.R_CASE_SENSITIVE | SearchPattern.R_ERASURE_MATCH;
		SearchPattern suitePattern= SearchPattern.createPattern(
				"suite() Test", IJavaSearchConstants.METHOD, IJavaSearchConstants.DECLARATIONS, matchRule); //$NON-NLS-1$
		SearchParticipant[] participants= new SearchParticipant[] { SearchEngine.getDefaultSearchParticipant() };
		new SearchEngine().search(suitePattern, participants, scope, requestor, progressMonitor); */
		
		return modules;
	}
	
	/**
	 * Finds modules with unit tests in a separate runnable. Safe to be called
	 * fom the UI thread, I think.
	 * 
	 * @param context  The context in which to run the search.
	 * @param elements The elements to search
	 * @return         An array of modules containing unit tests
	 */
	public static ICompilationUnit[] findTests(IRunnableContext context,
			Object[] elements) throws InvocationTargetException,
			InterruptedException
	{
		final Set<ICompilationUnit> result= new HashSet<ICompilationUnit>();

		if (elements.length > 0) {
			IRunnableWithProgress runnable= new IRunnableWithProgress()
			{
				public void run(IProgressMonitor pm) throws InterruptedException
				{
					// TODO testKind.createFinder().findTestsInContainer(elements, result, pm);
				}
			};
			context.run(true, true, runnable);
		}
		return result.toArray(new ICompilationUnit[result.size()]);
	}
	
	/**
	 * Finds modules with unit tests in a separate runnable (creating its own
	 * context to run in). Safe to be called in the UI thread.
	 * 
	 * @param elements The elements to search
	 * @return         An array of modules containing unit tests
	 */
	public static ICompilationUnit[] findTests(final Object[] elements) 
			throws InvocationTargetException, InterruptedException
	{
		final Set<ICompilationUnit> result= new HashSet<ICompilationUnit>();

		if (elements.length > 0)
		{
			IRunnableWithProgress runnable= new IRunnableWithProgress()
			{
				public void run(IProgressMonitor pm) throws InterruptedException
				{
					pm.beginTask("", 1);
					// TODO kind.createFinder().findTestsInContainer(elements, result, new SubProgressMonitor(pm, 1));
					pm.done();
				}
			};
			PlatformUI.getWorkbench().getProgressService().busyCursorWhile(runnable);
		}
		return result.toArray(new ICompilationUnit[result.size()]);
	}
	
	/**
	 * WTF I have no idea what this does, but I'll keep it around for the time
	 * being.
	 */
	public static Object computeScope(Object element) throws JavaModelException
	{
		if (element instanceof IFileEditorInput)
			element= ((IFileEditorInput) element).getFile();
		if (element instanceof IResource)
			element= JavaCore.create((IResource) element);
		return element;
	}
	
	/**
	 * Finds all the compilation units in the scope of the given element that
	 * contain unit test declarations.
	 * 
	 * @param pm      A progress monitor monitoring the search progress
	 * @param element The element whose scope to search
	 * @return        A list of all the modules in the element's scope that
	 *                contain unit test declarations
	 */
	private static List<ICompilationUnit> searchElementScope(IProgressMonitor pm,
			IJavaElement element) throws CoreException
	{
		IJavaSearchScope scope= SearchEngine.createJavaSearchScope(new IJavaElement[] { element }, IJavaSearchScope.SOURCES);
		TestSearchEngine searchEngine= new TestSearchEngine();
		return searchEngine.searchMethod(pm, scope);
	}
}

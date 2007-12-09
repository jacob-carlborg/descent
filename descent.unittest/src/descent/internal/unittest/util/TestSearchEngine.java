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
import org.eclipse.core.runtime.SubProgressMonitor;
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
import descent.internal.unittest.launcher.DUnittestFinder;

/**
 * Class for finding D modules that contain unittests and unittests within those
 * modules.
 * 
 * @author Robert Fraser
 */
public class TestSearchEngine {
	
	/**
	 * Finds modules with unit tests in a separate runnable. Safe to be called
	 * fom the UI thread, I think.
	 * 
	 * @param context  The context in which to run the search.
	 * @param elements The elements to search
	 * @return         An array of modules containing unit tests
	 */
	public static ICompilationUnit[] findTests(IRunnableContext context,
			final Object[] elements) throws InvocationTargetException,
			InterruptedException
	{
		final Set<ICompilationUnit> result= new HashSet<ICompilationUnit>();

		if (elements.length > 0) {
			IRunnableWithProgress runnable= new IRunnableWithProgress()
			{
				public void run(IProgressMonitor pm) throws InterruptedException
				{
					DUnittestFinder.getInstance().findTestsInContainer(elements, result, pm);
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
					DUnittestFinder.getInstance().findTestsInContainer(elements, result,
							new SubProgressMonitor(pm, 1));
					pm.done();
				}
			};
			PlatformUI.getWorkbench().getProgressService().busyCursorWhile(runnable);
		}
		return result.toArray(new ICompilationUnit[result.size()]);
	}
	
	/**
	 * Does something important.
	 */
	public static Object computeScope(Object element) throws JavaModelException
	{
		if (element instanceof IFileEditorInput)
			element= ((IFileEditorInput) element).getFile();
		if (element instanceof IResource)
			element= JavaCore.create((IResource) element);
		return element;
	}
}

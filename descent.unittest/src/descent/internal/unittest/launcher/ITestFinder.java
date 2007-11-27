/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     David Saff (saff@mit.edu) - initial API and implementation
 *             (bug 102632: [JUnit] Support for JUnit 4.)
 *******************************************************************************/

package descent.internal.unittest.launcher;

import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;

import descent.core.ICompilationUnit;
import descent.core.JavaModelException;

/**
 * Defines an interface for finding interfaces.
 * 
 * @author Robert Fraser
 */
public interface ITestFinder
{
	/**
	 * Finds modules that have unit tests within the given container and adds
	 * the results to th result set.
	 * 
	 * @param elements The elements to search
	 * @param result   A set to add results to.
	 * @param pm       A progress monitor monitoring the search for eelements.
	 */
	public abstract void findTestsInContainer(Object[] elements, 
			Set<ICompilationUnit> result, IProgressMonitor pm);
	
	/**
	 * Returns true if and only if the given compilation unit has at least one
	 * unit test declaration (either at the module level or within an aggregate).
	 * 
	 * @param module the module to check for unit tests
	 * @return       true if the module has a unit test
	 */
	public abstract boolean hasTests(ICompilationUnit module) 
			throws JavaModelException;
	
	/**
	 * An ItestFinder that won't find anything.
	 */
	ITestFinder NULL= new ITestFinder()
	{
		public void findTestsInContainer(Object[] elements, 
				Set<ICompilationUnit> result, IProgressMonitor pm)
		{
			// do nothing
		}

		public boolean hasTests(ICompilationUnit module) 
				throws JavaModelException
		{
			return false;
		}
	};
}

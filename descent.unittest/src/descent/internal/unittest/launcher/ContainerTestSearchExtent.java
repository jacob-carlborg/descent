/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
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

/**
 * 
 */
package descent.internal.unittest.launcher;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;

import descent.core.ICompilationUnit;
import descent.core.IJavaElement;
import descent.core.JavaCore;


class ContainerTestSearchExtent implements ITestSearchExtent
{
	private final IProgressMonitor pm;
	private final String handle;

	ContainerTestSearchExtent(IProgressMonitor $pm, String $handle) {
		pm = $pm;
		handle = $handle;
	}

	public ICompilationUnit[] find(ITestFinder finder)
	{
		IJavaElement container = JavaCore.create(handle);
		Set<ICompilationUnit> result = new HashSet<ICompilationUnit>();
		finder.findTestsInContainer(new Object[] { container }, result, pm);
		return result.toArray(new ICompilationUnit[result.size()]);
	}
}
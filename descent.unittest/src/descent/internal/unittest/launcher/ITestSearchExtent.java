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

import descent.core.ICompilationUnit;
import descent.core.JavaModelException;

/**
 * Defines an extent in which to search for modules with tests.
 * 
 * @author  Robert Fraser
 */
public interface ITestSearchExtent
{
	/**
	 * Uses the given finder to find modules with unittest declarations within
	 * the serch extent.
	 */
	ICompilationUnit[] find(ITestFinder finder) throws JavaModelException;
}
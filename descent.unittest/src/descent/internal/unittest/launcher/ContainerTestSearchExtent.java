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

import descent.core.IJavaElement;
import descent.core.IType;
import descent.core.JavaCore;


class ContainerTestSearchExtent implements ITestSearchExtent {
	private final IProgressMonitor fPm;

	private final String fHandle;

	ContainerTestSearchExtent(IProgressMonitor pm, String handle) {
		fPm = pm;
		fHandle = handle;
	}

	public IType[] find(ITestFinder finder) {
		IJavaElement container = JavaCore.create(fHandle);
		Set result = new HashSet();
		finder.findTestsInContainer(new Object[] { container }, result, fPm);
		return (IType[]) result.toArray(new IType[result.size()]);
	}
}
/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package descent.internal.corext.refactoring.changes;

import org.eclipse.core.resources.mapping.ResourceMapping;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;


import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.ReorgExecutionLog;

import descent.core.IPackageFragment;
import descent.core.IPackageFragmentRoot;
import descent.core.JavaCore;
import descent.core.JavaModelException;

import descent.internal.corext.refactoring.base.JDTChange;
import descent.internal.corext.refactoring.reorg.INewNameQuery;
import descent.internal.corext.util.JavaElementResourceMapping;

abstract class PackageReorgChange extends JDTChange {

	private String fPackageHandle;
	private String fDestinationHandle;
	private INewNameQuery fNameQuery;

	PackageReorgChange(IPackageFragment pack, IPackageFragmentRoot dest, INewNameQuery nameQuery) {
		fPackageHandle= pack.getHandleIdentifier();
		fDestinationHandle= dest.getHandleIdentifier();
		fNameQuery= nameQuery;
	}

	abstract Change doPerformReorg(IProgressMonitor pm) throws JavaModelException;

	public RefactoringStatus isValid(IProgressMonitor pm) throws CoreException {
		// it is enough to check the package only since package reorg changes
		// are not undoable. Don't check for read only here since
		// we already ask for user confirmation and moving a read
		// only package doesn't go thorugh validate edit (no
		// file content is modified).
		return isValid(pm, NONE);
	}

	public final Change perform(IProgressMonitor pm) throws CoreException {
		pm.beginTask(getName(), 1);
		try {
			IPackageFragment pack= getPackage();
			ResourceMapping mapping= JavaElementResourceMapping.create(pack);
			final Change result= doPerformReorg(pm);
			markAsExecuted(pack, mapping);
			return result;
		} finally {
			pm.done();
		}
	}

	public Object getModifiedElement() {
		return getPackage();
	}

	IPackageFragmentRoot getDestination() {
		return (IPackageFragmentRoot)JavaCore.create(fDestinationHandle);
	}

	IPackageFragment getPackage() {
		return (IPackageFragment)JavaCore.create(fPackageHandle);
	}

	String getNewName() {
		if (fNameQuery == null)
			return null;
		return fNameQuery.getNewName();
	}

	private void markAsExecuted(IPackageFragment pack, ResourceMapping mapping) {
		ReorgExecutionLog log= (ReorgExecutionLog)getAdapter(ReorgExecutionLog.class);
		if (log != null) {
			log.markAsProcessed(pack);
			log.markAsProcessed(mapping);
		}
	}
}
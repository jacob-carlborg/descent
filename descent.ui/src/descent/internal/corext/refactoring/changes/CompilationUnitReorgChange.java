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
import org.eclipse.core.runtime.SubProgressMonitor;


import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.participants.ReorgExecutionLog;

import descent.core.ICompilationUnit;
import descent.core.IPackageFragment;
import descent.core.JavaCore;

import descent.internal.corext.refactoring.RefactoringCoreMessages;
import descent.internal.corext.refactoring.base.JDTChange;
import descent.internal.corext.refactoring.reorg.INewNameQuery;
import descent.internal.corext.util.JavaElementResourceMapping;

abstract class CompilationUnitReorgChange extends JDTChange {

	private String fCuHandle;
	private String fOldPackageHandle;
	private String fNewPackageHandle;

	private INewNameQuery fNewNameQuery;

	CompilationUnitReorgChange(ICompilationUnit cu, IPackageFragment dest, INewNameQuery newNameQuery) {
		fCuHandle= cu.getHandleIdentifier();
		fNewPackageHandle= dest.getHandleIdentifier();
		fNewNameQuery= newNameQuery;
		fOldPackageHandle= cu.getParent().getHandleIdentifier();
	}

	CompilationUnitReorgChange(ICompilationUnit cu, IPackageFragment dest) {
		this(cu, dest, null);
	}

	CompilationUnitReorgChange(String oldPackageHandle, String newPackageHandle, String cuHandle) {
		fOldPackageHandle= oldPackageHandle;
		fNewPackageHandle= newPackageHandle;
		fCuHandle= cuHandle;
	}

	public final Change perform(IProgressMonitor pm) throws CoreException {
		pm.beginTask(getName(), 1);
		try {
			ICompilationUnit unit= getCu();
			ResourceMapping mapping= JavaElementResourceMapping.create(unit);
			Change result= doPerformReorg(new SubProgressMonitor(pm, 1));
			markAsExecuted(unit, mapping);
			return result;
		} finally {
			pm.done();
		}
	}

	abstract Change doPerformReorg(IProgressMonitor pm) throws CoreException;

	public Object getModifiedElement() {
		return getCu();
	}

	ICompilationUnit getCu() {
		return (ICompilationUnit)JavaCore.create(fCuHandle);
	}

	IPackageFragment getOldPackage() {
		return (IPackageFragment)JavaCore.create(fOldPackageHandle);
	}

	IPackageFragment getDestinationPackage() {
		return (IPackageFragment)JavaCore.create(fNewPackageHandle);
	}

	String getNewName() {
		if (fNewNameQuery == null)
			return null;
		return fNewNameQuery.getNewName();
	}

	static String getPackageName(IPackageFragment pack) {
		if (pack.isDefaultPackage())
			return RefactoringCoreMessages.MoveCompilationUnitChange_default_package; 
		else
			return pack.getElementName();
	}

	private void markAsExecuted(ICompilationUnit unit, ResourceMapping mapping) {
		ReorgExecutionLog log= (ReorgExecutionLog)getAdapter(ReorgExecutionLog.class);
		if (log != null) {
			log.markAsProcessed(unit);
			log.markAsProcessed(mapping);
		}
	}
}
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

import org.eclipse.text.edits.UndoEdit;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;

import org.eclipse.core.resources.IFile;

import descent.core.ICompilationUnit;

import descent.internal.corext.refactoring.RefactoringCoreMessages;
import descent.internal.corext.util.Messages;

import descent.internal.ui.JavaPlugin;

import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.ContentStamp;
import org.eclipse.ltk.core.refactoring.UndoTextFileChange;

/* package */ class UndoCompilationUnitChange extends UndoTextFileChange {
	
	private ICompilationUnit fCUnit;

	public UndoCompilationUnitChange(String name, ICompilationUnit unit, UndoEdit undo, ContentStamp stampToRestore, int saveMode) throws CoreException {
		super(name, getFile(unit), undo, stampToRestore, saveMode);
		fCUnit= unit;
	}

	private static IFile getFile(ICompilationUnit cunit) throws CoreException {
		IFile file= (IFile)cunit.getResource();
		if (file == null)
			throw new CoreException(new Status(
				IStatus.ERROR, 
				JavaPlugin.getPluginId(), 
				IStatus.ERROR, 
				Messages.format(
					RefactoringCoreMessages.UndoCompilationUnitChange_no_resource, 
					cunit.getElementName()), 
				null)
			);
		return file;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Object getModifiedElement() {
		return fCUnit;
	}
	
	/**
	 * {@inheritDoc}
	 */
	protected Change createUndoChange(UndoEdit edit, ContentStamp stampToRestore) throws CoreException {
		return new UndoCompilationUnitChange(getName(), fCUnit, edit, stampToRestore, getSaveMode());
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Change perform(IProgressMonitor pm) throws CoreException {
		pm.beginTask("", 2); //$NON-NLS-1$
		fCUnit.becomeWorkingCopy(null, new SubProgressMonitor(pm,1));
		try {
			return super.perform(new SubProgressMonitor(pm,1));
		} finally {
			fCUnit.discardWorkingCopy();
		}
	}
}

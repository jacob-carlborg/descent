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

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.ltk.core.refactoring.Change;

import descent.core.IPackageFragment;
import descent.core.IPackageFragmentRoot;
import descent.core.JavaModelException;

import descent.internal.corext.refactoring.RefactoringCoreMessages;
import descent.internal.corext.refactoring.reorg.INewNameQuery;
import descent.internal.corext.util.Messages;

public class CopyPackageChange extends PackageReorgChange {
	
	public CopyPackageChange(IPackageFragment pack, IPackageFragmentRoot dest, INewNameQuery nameQuery){
		super(pack, dest, nameQuery);
	}
	
	protected Change doPerformReorg(IProgressMonitor pm) throws JavaModelException{
		getPackage().copy(getDestination(), null, getNewName(), true, pm);
		return null;
	}
	
	public String getName() {
		return Messages.format(RefactoringCoreMessages.CopyPackageChange_copy, 
			new String[]{ getPackage().getElementName(), getDestination().getElementName()});
	}
}

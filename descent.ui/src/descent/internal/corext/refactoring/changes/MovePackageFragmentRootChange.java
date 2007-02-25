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

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.core.resources.IProject;

import org.eclipse.ltk.core.refactoring.Change;

import descent.core.IPackageFragmentRoot;
import descent.core.JavaModelException;

import descent.internal.corext.refactoring.RefactoringCoreMessages;
import descent.internal.corext.refactoring.reorg.IPackageFragmentRootManipulationQuery;
import descent.internal.corext.util.Messages;

public class MovePackageFragmentRootChange extends PackageFragmentRootReorgChange {

	public MovePackageFragmentRootChange(IPackageFragmentRoot root, IProject destination, IPackageFragmentRootManipulationQuery updateClasspathQuery) {
		super(root, destination, null, updateClasspathQuery);
	}

	protected Change doPerformReorg(IPath destinationPath, IProgressMonitor pm) throws JavaModelException {
		getRoot().move(destinationPath, getResourceUpdateFlags(), getUpdateModelFlags(false), null, pm);
		return null;
	}
	
	public String getName() {
		String[] keys= {getRoot().getElementName(), getDestinationProject().getName()};
		return Messages.format(RefactoringCoreMessages.MovePackageFragmentRootChange_move, keys); 
	}
}

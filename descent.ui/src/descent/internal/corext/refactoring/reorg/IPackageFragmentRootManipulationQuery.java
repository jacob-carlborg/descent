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
package descent.internal.corext.refactoring.reorg;

import descent.core.IJavaProject;
import descent.core.IPackageFragmentRoot;

/**
 * Query that can be used when manipulating package fragment roots.
 * Depending on the context, <code>confirmManipulation</code> can be used to
 * determine wheter, for example, the package fragment root is to be deleted or
 * not or if the classpath of the referencing projects is to be updated.
 */
public interface IPackageFragmentRootManipulationQuery {
	
	public boolean confirmManipulation(IPackageFragmentRoot root, IJavaProject[] referencingProjects);
}

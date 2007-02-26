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
package descent.internal.ui.filters;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import descent.core.IClassFile;
import descent.core.ICompilationUnit;
import descent.core.IPackageFragment;
import descent.core.JavaModelException;


/**
 * Filters out all compilation units and class files elements.
 */
public class JavaFileFilter  extends ViewerFilter {
	
	/**
	 * Returns the result of this filter, when applied to the
	 * given inputs.
	 *
	 * @return Returns true if element should be included in filtered set
	 */
	public boolean select(Viewer viewer, Object parent, Object element) {
		if (element instanceof ICompilationUnit)
			return false;
		if (element instanceof IClassFile)
			return false;
			
		if (element instanceof IPackageFragment)
			try {
				return ((IPackageFragment)element).getNonJavaResources().length > 0;
			} catch (JavaModelException ex) {
				return true;
			}
		return true;			
	}
}

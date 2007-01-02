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
package descent.internal.ui.javaeditor;



import descent.core.ICompilationUnit;

public interface ISavePolicy {

	/**
	 *
	 */
	void preSave(ICompilationUnit unit);

	/**
	 * Returns the compilation unit in which the argument
	 * has been changed. If the argument is not changed, the
	 * returned result is <code>null</code>.
	 */
	ICompilationUnit postSave(ICompilationUnit unit);
}

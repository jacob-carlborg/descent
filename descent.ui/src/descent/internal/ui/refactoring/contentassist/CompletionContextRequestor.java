/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package descent.internal.ui.refactoring.contentassist;

import descent.core.ICompilationUnit;

import descent.internal.corext.refactoring.StubTypeContext;


public abstract class CompletionContextRequestor {
	
	public abstract StubTypeContext getStubTypeContext();
	
	public ICompilationUnit getOriginalCu() {
		return getStubTypeContext().getCuHandle();
	}

	public String getBeforeString() {
		return getStubTypeContext().getBeforeString();
	}

	public String getAfterString() {
		return getStubTypeContext().getAfterString();
	}
	
}

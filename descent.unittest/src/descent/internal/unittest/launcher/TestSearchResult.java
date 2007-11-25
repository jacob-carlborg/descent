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

package descent.internal.unittest.launcher;

import descent.core.IType;

public class TestSearchResult {

	private final IType[] fTypes;

	public TestSearchResult(IType[] types) {
		fTypes = types;
	}

	public IType[] getTypes() {
		return fTypes;
	}

	boolean isEmpty() {
		return getTypes().length <= 0;
	}
}

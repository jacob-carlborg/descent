/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package descent.internal.core.search.matching;

import descent.core.IJavaElement;
import descent.internal.compiler.util.SimpleSet;

public class DeclarationOfReferencedTypesPattern extends TypeReferencePattern {

protected SimpleSet knownTypes;
protected IJavaElement enclosingElement;

public DeclarationOfReferencedTypesPattern(IJavaElement enclosingElement) {
	super(null, null, R_PATTERN_MATCH);

	this.enclosingElement = enclosingElement;
	this.knownTypes = new SimpleSet();
	((InternalSearchPattern)this).mustResolve = true;
}
}

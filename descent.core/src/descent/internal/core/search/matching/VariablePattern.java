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

import descent.core.compiler.CharOperation;

public abstract class VariablePattern extends FQNPattern {

protected boolean findDeclarations;
protected boolean findReferences;
protected boolean readAccess;
protected boolean writeAccess;

public VariablePattern(int patternKind, boolean findDeclarations, boolean readAccess, boolean writeAccess, char[] name, int matchRule) {
	super(patternKind, matchRule);

	this.findDeclarations = findDeclarations; // set to find declarations & all occurences
	this.readAccess = readAccess; // set to find any reference, read only references & all occurences
	this.writeAccess = writeAccess; // set to find any reference, write only references & all occurences
	this.findReferences = readAccess || writeAccess;

	this.simpleName = (isCaseSensitive() || isCamelCase())  ? name : CharOperation.toLowerCase(name);
}
/*
 * Returns whether a method declaration or message send will need to be resolved to 
 * find out if this method pattern matches it.
 */
protected boolean mustResolve() {
	// would like to change this so that we only do it if generic references are found
	return this.findReferences; // always resolve (in case of a simple name reference being a potential match)
}	
}

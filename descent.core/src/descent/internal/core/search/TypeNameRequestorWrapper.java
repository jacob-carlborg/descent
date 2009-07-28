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
package descent.internal.core.search;

import descent.core.search.TypeNameRequestor;
import descent.internal.compiler.env.AccessRestriction;

/**
 * Wrapper used to link {@link IRestrictedAccessTypeRequestor} with {@link TypeNameRequestor}.
 * This wrapper specifically allows usage of internal method {@link BasicSearchEngine#searchAllTypeNames(
 * 	char[] packageName, 
 * 	char[] typeName,
 * 	int matchRule, 
 * 	int searchFor, 
 * 	descent.core.search.IJavaSearchScope scope, 
 * 	IRestrictedAccessTypeRequestor nameRequestor,
 * 	int waitingPolicy,
 * 	org.eclipse.core.runtime.IProgressMonitor monitor) }.
 * from  API method {@link descent.core.search.SearchEngine#searchAllTypeNames(
 * 	char[] packageName, 
 * 	char[] typeName,
 * 	int matchRule, 
 * 	int searchFor, 
 * 	descent.core.search.IJavaSearchScope scope, 
 * 	TypeNameRequestor nameRequestor,
 * 	int waitingPolicy,
 * 	org.eclipse.core.runtime.IProgressMonitor monitor) }.
 */
public class TypeNameRequestorWrapper implements IRestrictedAccessTypeRequestor {
	TypeNameRequestor requestor;
	public TypeNameRequestorWrapper(TypeNameRequestor requestor) {
		this.requestor = requestor;
	}
	public void acceptType(long modifiers, char[] packageName, char[] simpleTypeName, char[] templateParametersSignature, char[][] enclosingTypeNames, String path, int declarationStart, AccessRestriction access) {
		this.requestor.acceptType(modifiers, TypeNameRequestor.KindType, packageName, simpleTypeName, enclosingTypeNames, path);
	}
}

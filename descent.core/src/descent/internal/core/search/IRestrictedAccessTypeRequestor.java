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
package descent.internal.core.search;

import descent.internal.compiler.env.AccessRestriction;

/**
 * A <code>IRestrictedAccessTypeRequestor</code> collects search results from a <code>searchAllTypeNames</code>
 * query to a <code>SearchBasicEngine</code> providing restricted access information when a type is accepted.
 * @see descent.core.search.TypeNameRequestor
 */
public interface IRestrictedAccessTypeRequestor {
	
	public void acceptType(long modifiers, char[] packageName, char[] simpleTypeName, char[] templateParametersSignature, char[][] enclosingTypeNames, String path, int declarationStart, AccessRestriction access);

}

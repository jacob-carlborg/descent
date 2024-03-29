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
package descent.internal.core;

import descent.core.ICompilationUnit;
import descent.core.IField;
import descent.core.IInitializer;
import descent.core.IMethod;
import descent.core.IPackageFragment;
import descent.core.IType;

/**
 * This interface is used by IRequestorNameLookup. As results
 * are found by IRequestorNameLookup, they are reported to this
 * interface. An IJavaElementRequestor is able to cancel
 * at any time (that is, stop receiving results), by responding
 * <code>true</code> to <code>#isCancelled</code>.
 */
public interface IJavaElementRequestor {
public void acceptField(IField field);
public void acceptInitializer(IInitializer initializer);
public void acceptMemberType(IType type);
public void acceptMethod(IMethod method);
public void acceptPackageFragment(IPackageFragment packageFragment);
public void acceptCompilationUnit(ICompilationUnit compilationUnit);
public void acceptType(IType type);
/**
 * Returns <code>true</code> if this IJavaElementRequestor does
 * not want to receive any more results.
 */
boolean isCanceled();
}

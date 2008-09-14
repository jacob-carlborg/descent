/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     IBM Corporation - added J2SE 1.5 support
 *******************************************************************************/
package descent.core;



/**
 * Represents an entire Java compilation unit (source file with one of the 
 * {@link JavaCore#getJavaLikeExtensions() Java-like extensions}).
 * Compilation unit elements need to be opened before they can be navigated or manipulated.
 * The children are of type {@link IPackageDeclaration},
 * {@link IImportContainer}, and {@link IType},
 * and appear in the order in which they are declared in the source.
 * If a source file cannot be parsed, its structure remains unknown.
 * Use {@link IJavaElement#isStructureKnown} to determine whether this is 
 * the case.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 */
public interface ICompilationUnit extends IJavaElement 
/* TODO JDT code completion
, ICodeAssist 
*/ 
{
/**
 * Constant indicating that a reconcile operation should not return an AST.
 * @since 3.0
 */
public static final int NO_AST = 0;

/** 
 * Finds the elements in this compilation unit that correspond to
 * the given element.
 * An element A corresponds to an element B if:
 * <ul>
 * <li>A has the same element name as B.
 * <li>If A is a method, A must have the same number of arguments as
 *     B and the simple names of the argument types must be equals.
 * <li>The parent of A corresponds to the parent of B recursively up to
 *     their respective compilation units.
 * <li>A exists.
 * </ul>
 * Returns <code>null</code> if no such java elements can be found
 * or if the given element is not included in a compilation unit.
 * 
 * @param element the given element
 * @return the found elements in this compilation unit that correspond to the given element
 * @since 3.0 
 */
IJavaElement[] findElements(IJavaElement element);
/**
 * Returns the primary compilation unit (whose owner is the primary owner)
 * this working copy was created from, or this compilation unit if this a primary
 * compilation unit.
 * <p>
 * Note that the returned primary compilation unit can be in working copy mode.
 * </p>
 * 
 * @return the primary compilation unit this working copy was created from,
 * or this compilation unit if it is primary
 * @since 3.0
 */
ICompilationUnit getPrimary();
/**
 * Returns whether the resource of this working copy has changed since the
 * inception of this working copy.
 * Returns <code>false</code> if this compilation unit is not in working copy mode.
 * 
 * @return whether the resource has changed
 * @since 3.0
 */
public boolean hasResourceChanged();
/**
 * Returns whether this element is a working copy.
 * 
 * @return true if this element is a working copy, false otherwise
 * @since 3.0
 */
boolean isWorkingCopy();
}

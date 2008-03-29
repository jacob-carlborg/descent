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
 * Represents an import declaration in Java compilation unit.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 */
public interface IImportDeclaration extends IJavaElement, ISourceReference, ISourceManipulation {
/**
 * Returns the fully qualified name that has been imported.
 * 
 * @return the fully qualified name that has been imported
 */
String getElementName();
/**
 * Returns the alias to use for this import, if any, ir <code>null</code>.
 * @return the alias to use for this import, if any, ir <code>null</code>
 */
String getAlias();
/**
 * Returns the names of the selective imports. Each element return is not 
 * <code>null</code>.
 * The returned array is never <code>null</code>.  
 * @return the names of the selective imports
 */
String[] getSelectiveImportsNames();
/**
 * Returns the aliases of the selective imports. Some elements may be
 * <code>null</code> if no alias exists for a particular selective import.
 * The returned array is never <code>null</code>. 
 * @return the aliases of the selective imports
 */
String[] getSelectiveImportsAliases();
/**
 * Returns the modifier flags for this import. The flags can be examined using class
 * <code>Flags</code>. Only the static flag is meaningful for import declarations.
 * 
 * @return the modifier flags for this import
 * @exception JavaModelException if this element does not exist or if an
 *      exception occurs while accessing its corresponding resource.
 * @see Flags
 * @since 3.0
 */
long getFlags() throws JavaModelException;
}

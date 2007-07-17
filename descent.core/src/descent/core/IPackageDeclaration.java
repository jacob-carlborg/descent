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
package descent.core;


/**
 * Represents a package declaration in Java compilation unit.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 */
public interface IPackageDeclaration extends IJavaElement, ISourceReference {
/**
 * Returns the name of the package the statement refers to.
 * This is a handle-only method.
 * 
 * @return the name of the package the statement
 */
String getElementName();

/**
 * Returns the Javadoc range if this element is from source or if this element
 * is a binary element with an attached source, null otherwise.
 * 
 * <p>If this element is from source, the javadoc range is 
 * extracted from the corresponding source.</p>
 * <p>If this element is from a binary, the javadoc is extracted from the
 * attached source if present.</p>
 * <p>If this element's openable is not consistent, then null is returned.</p>
 *
 * @exception JavaModelException if this element does not exist or if an
 *      exception occurs while accessing its corresponding resource.
 * @return a source range corresponding to the javadoc source or <code>null</code>
 * if no source is available, this element has no javadoc comment or
 * this element's openable is not consistent
 * @see IOpenable#isConsistent()
 * @since 3.2
 */
ISourceRange[] getJavadocRanges() throws JavaModelException;
}

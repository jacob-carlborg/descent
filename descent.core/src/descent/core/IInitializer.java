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
 * Represents a stand-alone instance or class (static) initializer in a type.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 */
public interface IInitializer extends IMember {
	
	/**
	 * Returns whether this initializer represents a static constructor.
	 * 
	 * @return whether this initializer represents a static constructor
	 * @exception JavaModelException if this element does not exist or if an
	 *      exception occurs while accessing its corresponding resource
	 * @since 3.1
	 */
	boolean isStaticConstructor() throws JavaModelException;
	
	/**
	 * Returns whether this initializer represents a static destructor.
	 * 
	 * @return whether this initializer represents a static destructor
	 * @exception JavaModelException if this element does not exist or if an
	 *      exception occurs while accessing its corresponding resource
	 * @since 3.1
	 */
	boolean isStaticDestructor() throws JavaModelException;
	
	/**
	 * Returns whether this initializer represents an invariant.
	 * 
	 * @return whether this initializer represents an invariant
	 * @exception JavaModelException if this element does not exist or if an
	 *      exception occurs while accessing its corresponding resource
	 * @since 3.1
	 */
	boolean isInvariant() throws JavaModelException;
	
	/**
	 * Returns whether this initializer represents a unit test.
	 * 
	 * @return whether this initializer represents a unit test
	 * @exception JavaModelException if this element does not exist or if an
	 *      exception occurs while accessing its corresponding resource
	 * @since 3.1
	 */
	boolean isUnitTest() throws JavaModelException;
	
	/**
	 * Returns whether this initializer represents a static assert.
	 * 
	 * @return whether this initializer represents a static assert
	 * @exception JavaModelException if this element does not exist or if an
	 *      exception occurs while accessing its corresponding resource
	 * @since 3.1
	 */
	boolean isStaticAssert() throws JavaModelException;
	
	/**
	 * Returns whether this initializer represents a debug assignment.
	 * 
	 * @return whether this initializer represents a debug assignment
	 * @exception JavaModelException if this element does not exist or if an
	 *      exception occurs while accessing its corresponding resource
	 * @since 3.1
	 */
	boolean isDebugAssignment() throws JavaModelException;
	
	/**
	 * Returns whether this initializer represents a version assignment.
	 * 
	 * @return whether this initializer represents a version assignment
	 * @exception JavaModelException if this element does not exist or if an
	 *      exception occurs while accessing its corresponding resource
	 * @since 3.1
	 */
	boolean isVersionAssignment() throws JavaModelException;
	
	
	
}

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

package descent.core.dom;

/**
 * A compilation unit binding represents a compilation unit.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @since 2.0
 */
public interface ICompilationUnitBinding extends IBinding {

	/**
	 * Returns the fully qualified name of the compilation unit
	 * represented by this binding.
	 * 
	 * @return the fully qualified name of the compilation unit
	 * represented by this binding
	 */
	String getName();
	
	/**
	 * Returns the list of name component making up the name of the compilation
	 * unit represented by this binding. For example, for the compilation
	 * unit named "std.c.fenv", this method returns {"std", "c", "fenv"}.
	 * 
	 * @return the name of the compilation unit represented by this binding
	 */
	String[] getNameComponents();
	
	/**
	 * Returns the list of public imports made the compilation unit
	 * respresented by this binding.
	 * 
	 * @return the list of public imports made the compilation unit
	 * respresented by this binding.
	 */
	ICompilationUnitBinding[] getPublicImports();
	
	/**
	 * Returns the list of variables declared by the compilation unit
	 * respresented by this binding.
	 * 
	 * @return the list of variables declared by the compilation unit
	 * respresented by this binding.
	 */
	IVariableBinding[] getDeclaredVariables();
	
	/**
	 * Returns the list of functions declared by the compilation unit
	 * respresented by this binding.
	 * 
	 * @return the list of functions declared by the compilation unit
	 * respresented by this binding.
	 */
	IMethodBinding[] getDeclaredFunctions();
	
	/**
	 * Returns the list of types declared by the compilation unit
	 * respresented by this binding.
	 * 
	 * @return the list of types declared by the compilation unit
	 * respresented by this binding.
	 */
	ITypeBinding[] getDeclaredTypes();
	
}

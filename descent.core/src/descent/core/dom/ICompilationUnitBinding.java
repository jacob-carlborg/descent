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
 * A package binding represents a compilation unit.
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
	public String getName();
	
	/**
	 * Returns the list of name component making up the name of the compilation
	 * unit represented by this binding. For example, for the compilation
	 * unit named "std.c.fenv", this method returns {"std", "c", "fenv"}.
	 * 
	 * @return the name of the compilation unit represented by this binding
	 */
	public String[] getNameComponents();
	
}

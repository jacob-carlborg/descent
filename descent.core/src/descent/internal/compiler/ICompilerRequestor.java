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
package descent.internal.compiler;

/**
 * A callback interface for receiving compilation results.
 */
public interface ICompilerRequestor {
	
	/**
	 * Accept a compilation result.
	 */
	public void acceptResult(CompilationResult result);
}

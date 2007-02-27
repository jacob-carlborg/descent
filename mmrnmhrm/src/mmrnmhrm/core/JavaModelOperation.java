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
package mmrnmhrm.core;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * JAVA STUB: See {@link org.eclipse.jdt.internal.core.JavaModelOperation}
 */
public abstract class JavaModelOperation implements IWorkspaceRunnable {

	/** The progress monitor passed into this operation */
	public IProgressMonitor progressMonitor= null;


	
	/** default constructor used in subclasses */
	protected JavaModelOperation() {
		// default constructor used in subclasses
	}
	
	/** Performs the operation specific behavior. Subclasses must override. */
	protected abstract void executeOperation() throws JavaModelException;
	
	
	/** Runs this operation and registers any deltas created.
	 */
	public void run(IProgressMonitor monitor) throws CoreException {
		executeOperation();
	}
}

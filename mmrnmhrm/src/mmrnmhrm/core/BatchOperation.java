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
package mmrnmhrm.core;

import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;

/**
 * JAVA STUB: @see org.eclipse.jdt.internal.core.BatchOperation
 */
public class BatchOperation extends JavaModelOperation {
	protected IWorkspaceRunnable runnable;
	
	public BatchOperation(IWorkspaceRunnable runnable) {
		this.runnable = runnable;
	}

	protected boolean canModifyRoots() {
		// anything in the workspace runnable can modify the roots
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.core.JavaModelOperation#executeOperation()
	 */
	protected void executeOperation() throws JavaModelException {
		try {
			this.runnable.run(this.progressMonitor);
		} catch (CoreException ce) {
			if (ce instanceof JavaModelException) {
				throw (JavaModelException)ce;
			} else {
				if (ce.getStatus().getCode() == IResourceStatus.OPERATION_FAILED) {
					Throwable e= ce.getStatus().getException();
					if (e instanceof JavaModelException) {
						throw (JavaModelException) e;
					}
				}
				throw new JavaModelException(ce);
			}
		}
	}
	

}

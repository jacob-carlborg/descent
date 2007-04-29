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
 * A model operation that encapsulates a user defined IWorkspaceRunnable.
 */
public class BatchOperation extends LangModelOperation {

	protected IWorkspaceRunnable runnable;

	public BatchOperation(IWorkspaceRunnable runnable) {
		this.runnable = runnable;
	}

	/** Runs the BatchOperation. */
	protected void executeOperation() throws LangModelException {
		try {
			this.runnable.run(this.progressMonitor);
		} catch (CoreException ce) {
			if (ce instanceof LangModelException) {
				throw (LangModelException)ce;
			} else {
				if (ce.getStatus().getCode() == IResourceStatus.OPERATION_FAILED) {
					Throwable e= ce.getStatus().getException();
					if (e instanceof LangModelException) {
						throw (LangModelException) e;
					}
				}
				throw new LangModelException(ce);
			}
		}
	}
	
}

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
package descent.internal.core;

import descent.core.IBuffer;
import descent.core.ICompilationUnit;
import descent.core.WorkingCopyOwner;

/**
 * Wraps an IBufferFactory.
 * TODO remove when removing IBufferFactory
 * @deprecated
 */
public class BufferFactoryWrapper extends WorkingCopyOwner {

	public descent.core.IBufferFactory factory;
		
	private BufferFactoryWrapper(descent.core.IBufferFactory factory) {
		this.factory = factory;
	}
	
	public static WorkingCopyOwner create(descent.core.IBufferFactory factory) {
		return new BufferFactoryWrapper(factory);
	}

	/* (non-Javadoc)
	 * @see descent.core.WorkingCopyOwner#createBuffer(descent.core.ICompilationUnit)
	 */
	public IBuffer createBuffer(ICompilationUnit workingCopy) {
		if (this.factory == null) return super.createBuffer(workingCopy);
		return this.factory.createBuffer(workingCopy);
	}	
	
	public boolean equals(Object obj) {
		if (!(obj instanceof BufferFactoryWrapper)) return false;
		BufferFactoryWrapper other = (BufferFactoryWrapper)obj;
		if (this.factory == null) return other.factory == null;
		return this.factory.equals(other.factory);
	}
	public int hashCode() {
		if (this.factory == null) return 0;
		return this.factory.hashCode();
	}
	public String toString() {
		return "FactoryWrapper for " + this.factory; //$NON-NLS-1$
	}
}

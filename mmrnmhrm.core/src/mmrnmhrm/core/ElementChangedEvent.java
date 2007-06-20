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

import java.util.EventObject;

/**
 * An element changed event describes a change to the structure or contents
 * of a tree of Java elements. The changes to the elements are described by
 * the associated delta object carried by this event.
 * <p>
 * This class is not intended to be instantiated or subclassed by clients.
 * Instances of this class are automatically created by the Java model.
 * </p>
 *
 * @see IElementChangedListener
 */
public class ElementChangedEvent extends EventObject {
	

	private static final long serialVersionUID = 1L;

	/** Event type indicating the nature of this event.  */
	private int type; 
	
	/** Creates an new element changed event 
	 * (based on a <code>IJavaElementDelta</code>).
	 * XXX: No Delta yet
	 */
	public ElementChangedEvent(Object delta, int type) {
		super(delta);
		this.type = type;
	}

	/** Returns the type of event being reported. */
	public int getType() {
		return this.type;
	}
}

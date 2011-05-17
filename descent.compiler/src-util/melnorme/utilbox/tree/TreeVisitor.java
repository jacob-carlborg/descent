/*******************************************************************************
 * Copyright (c) 2007 DSource.org and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial implementation
 *******************************************************************************/
package melnorme.utilbox.tree;

import java.util.List;

/** Abstract visitor for a heterogenous tree with some utility methods. */
public abstract class TreeVisitor {
	
	/** Accepts the visitor on child. If child is null, nothing happens. */
	public static <T> void acceptChild(T visitor, IVisitable<T> child) {
		if (child != null) {
			child.accept(visitor);
		}
	}

	/** Same as {@link #acceptChild(Object, IVisitable) } */
	public static <T> void acceptChildren(T visitor, IVisitable<T> child) {
		TreeVisitor.acceptChild(visitor, child);
	}

	/** Accepts the visitor on the children. If children is null, nothing
	 * happens.	*/
	public static <T> void acceptChildren(T visitor, IVisitable<T>[] children) {
		if (children == null)
			return;
		
		for(int i = 0; i < children.length; i++) {
			TreeVisitor.acceptChild(visitor, children[i]);
		}
	}

	/** Accepts the visitor on the children. If children is null, nothing
	 * happens. */
	public static <T> void acceptChildren(T visitor, List<? extends IVisitable<T>> children) {
		if (children == null)
			return;
		
		for(int i = 0; i < children.size(); i++) {
			TreeVisitor.acceptChild(visitor, children.get(i));
		}
	}

}
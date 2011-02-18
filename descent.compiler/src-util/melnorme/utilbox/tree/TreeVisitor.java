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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import melnorme.utilbox.core.ExceptionAdapter;

/** Abstract visitor for a heterogenous tree with some utility methods. */
public abstract class TreeVisitor {
		
	protected boolean visitingAsSuper = false;
	
	/** Utility method that visits an element as if it were of it's base class. */
	public <T> boolean visitAsSuperType(IElement element, Class<T> elemclass)  {
		Class<? super T> elemsuper = elemclass.getSuperclass();
		Method method;
		try {
			method = this.getClass().getMethod("visit", new Class[]{elemsuper});
			visitingAsSuper = true;
			boolean result = (Boolean) method.invoke(this, element);
			visitingAsSuper = false;
			return result;
		} catch (InvocationTargetException ite) {
			Throwable e = (Throwable)ite.getTargetException();
			throw ExceptionAdapter.unchecked(e);
		} catch (Exception e) {
			throw ExceptionAdapter.unchecked(e);
		} 
	}
	

	
	/** Accepts the visitor on child. If child is null, nothing happens. */
	private static <T> void acceptChild(T visitor, IVisitable<T> child) {
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
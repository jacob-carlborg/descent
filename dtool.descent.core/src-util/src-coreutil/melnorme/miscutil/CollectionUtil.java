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
package melnorme.miscutil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Utils for creation, query, and modification of Collection classes.
 */
public class CollectionUtil {
	
	/** Creates a List copy of orig, with all elements except elements equal
	 * to outElem. */
	public static <T> List<T> copyExcept(T[] orig, T outElem) {
		List<T> rejectedElements = 
			new ArrayList<T>(orig.length);

		for (int i= 0; i < orig.length; i++) {
			if (!orig[i].equals(outElem)) {
				rejectedElements.add(orig[i]);
			}
		}
		return rejectedElements;
	}
	
	/** @return a new collection with all elements that match given predicate removed. */
	public static <T> List<T> filter(Collection<? extends T> coll, IPredicate<T> predicate) {
		ArrayList<T> newColl = new ArrayList<T>();
		for (T elem : coll) {
			if(!predicate.evaluate(elem)) {
				newColl.add(elem);
			}
		}
		return newColl;
	}
			
}

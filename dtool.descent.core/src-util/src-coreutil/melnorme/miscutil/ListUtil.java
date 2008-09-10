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
import java.util.List;

public class ListUtil {

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

}

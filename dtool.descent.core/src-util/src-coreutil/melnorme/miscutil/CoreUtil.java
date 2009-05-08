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


import java.util.Arrays;

/**
 * Utils for miscellaneous core language functionality. 
 */
public class CoreUtil extends Assert {

	/** @return whether the two given objects are the same (including null) or equal. */
	public static boolean areEqual(Object o1, Object o2) {
		return (o1 == o2) || (o1 != null && o2 != null && o1.equals(o2));
	}
	
	/** @return whether the two given arrays are the same (including null) or equal 
	 * according to {@link Arrays#equals(Object[], Object[])}. */
	public static boolean areArrayEqual(Object[] a1, Object[] a2) {
		return (a1 == a2) || (a1 != null && a2 != null && Arrays.equals(a1, a2));
	}

	/** @return whether the two given arrays are the same (including null) or equal. 
	 * according to {@link Arrays#deepEquals(Object[], Object[])}.*/
	public static boolean areArrayDeepEqual(Object[] a1, Object[] a2) {
		return (a1 == a2) || (a1 != null && a2 != null && Arrays.deepEquals(a1, a2));
	}

	/** Returns the first element of objs array that is not null.
	 * At least one element must be not null. */
	public static <T> T firstNonNull(T... objs) {
		for (int i = 0; i < objs.length; i++) {
			if(objs[i] != null)
				return objs[i];
		}
		assertFail();
		return null;
	}
	
	/** Marker method for signaling a feature that is not yet implemented. Causes an assertion failure. 
	 * Uses the Deprecated annotation solely to cause a warning. */
	@Deprecated
	public static void assertTODO() {
		Assert.fail("TODO");
	}

}

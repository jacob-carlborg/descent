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

/**
 * Util class for checking for certain conditions, and throwing IllegalArgumentException
 * if those conditions are not met.
 */
/* This class is not intended to be instantiated. */
public final class Check {

    
	/** If obj is null throws IllegalArgumentException with option added message. */
	public static void isNotNull(Object obj, String message) {
		if (obj == null)
			throw new IllegalArgumentException("null argument:" + message); //$NON-NLS-1$
	}
	
	/** Like {@link #isNotNull(Object object, String message)} , with empty message. */
	public static void isNotNull(Object object) {
		isNotNull(object, ""); //$NON-NLS-1$
	}


	/** If expression is not true throws IllegalArgumentException with option added message. 
	 * @return true. */
	public static boolean isTrue(boolean expression, String message) {
		if (!expression)
			throw new IllegalArgumentException("assertion failed: " + message); //$NON-NLS-1$
		return expression;
	}
	
	/** Like {@link #isTrue(boolean, String)} with empty message */
	public static boolean isTrue(boolean expression) {
		return isTrue(expression, ""); //$NON-NLS-1$
	}

}

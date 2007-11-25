/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package descent.unittest;

import descent.internal.unittest.DescentUnittestPlugin;

/**
 * Class for accessing JUnit support; all functionality is provided by 
 * static methods. 
 * <p>
 * This class is not intended to be subclassed or instantiated by clients.
 * </p>
 * 
 * @since 2.1
 */
public class JUnitCore {

	/**
	 * Adds a listener for test runs.
	 * 
	 * @param listener listener to be added
	 */
	public static void addTestRunListener(ITestRunListener listener) {
		DescentUnittestPlugin.getDefault().addTestRunListener(listener);
	}

	/**
	 * Removes a listener for test runs.
	 * 
	 * @param listener listener to be removed 
	 */
	public static void removeTestRunListener(ITestRunListener listener) {
		DescentUnittestPlugin.getDefault().removeTestRunListener(listener);
	}
}

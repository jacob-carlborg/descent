/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     David Saff (saff@mit.edu) - bug 102632: [JUnit] Support for JUnit 4.
 *******************************************************************************/
package descent.internal.unittest.ui;

import java.util.Arrays;
import java.util.List;

import descent.internal.unittest.DescentUnittestPlugin;

/**
 * Defines constants which are used to refer to values in the plugin's preference store.
 */
public class JUnitPreferencesConstants {

	/**
	 * Boolean preference controlling whether the JUnit view should be shown on
	 * errors only.
	 */	
	public final static String SHOW_ON_ERROR_ONLY= DescentUnittestPlugin.PLUGIN_ID + ".show_on_error"; //$NON-NLS-1$

	/**
	 * Maximum number of remembered test runs.
	 */
	public static final String MAX_TEST_RUNS= DescentUnittestPlugin.PLUGIN_ID + ".max_test_runs"; //$NON-NLS-1$
	
	private JUnitPreferencesConstants() {
		// no instance
	}

}

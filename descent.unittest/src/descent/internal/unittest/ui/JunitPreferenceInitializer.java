/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sebastian Davids <sdavids@gmx.de> - initial API and implementation
 *******************************************************************************/
package descent.internal.unittest.ui;

import java.util.List;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

import descent.internal.unittest.DescentUnittestPlugin;

/**
 * Default preference value initialization for the
 * <code>descent.unittest</code> plug-in.
 */
public class JunitPreferenceInitializer extends AbstractPreferenceInitializer {

	/** {@inheritDoc} */
	public void initializeDefaultPreferences() {
		Preferences prefs= DescentUnittestPlugin.getDefault().getPluginPreferences();
		prefs.setDefault(JUnitPreferencesConstants.SHOW_ON_ERROR_ONLY, false);
		prefs.setDefault(JUnitPreferencesConstants.MAX_TEST_RUNS, 10);
	}
}

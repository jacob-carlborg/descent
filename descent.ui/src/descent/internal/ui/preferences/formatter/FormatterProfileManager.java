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
package descent.internal.ui.preferences.formatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;

import descent.core.JavaCore;
import descent.core.formatter.DefaultCodeFormatterConstants;

import descent.ui.JavaUI;
import descent.ui.PreferenceConstants;

import descent.internal.ui.preferences.PreferencesAccess;

public class FormatterProfileManager extends ProfileManager {
	
	private final static KeySet[] KEY_SETS= new KeySet[] {
		new KeySet(JavaCore.PLUGIN_ID, new ArrayList(DefaultCodeFormatterConstants.getDefaultSettings().keySet())),
		new KeySet(JavaUI.ID_PLUGIN, Collections.EMPTY_LIST)	
	};
	
	private final static String PROFILE_KEY= PreferenceConstants.FORMATTER_PROFILE;
	private final static String FORMATTER_SETTINGS_VERSION= "formatter_settings_version";  //$NON-NLS-1$

	public FormatterProfileManager(List profiles, IScopeContext context, PreferencesAccess preferencesAccess, IProfileVersioner profileVersioner) {
	    super(addBuiltinProfiles(profiles, profileVersioner), context, preferencesAccess, profileVersioner, KEY_SETS, PROFILE_KEY, FORMATTER_SETTINGS_VERSION);
    }
	
	private static List addBuiltinProfiles(List profiles, IProfileVersioner profileVersioner) {
		profiles.add(new BuiltInProfile(DefaultCodeFormatterConstants.PROFILE_DESCENT_DEFAULTS,
				FormatterMessages.ProfileManager_descent_defaults_profile_name,
				getBuiltInProfile(DefaultCodeFormatterConstants.PROFILE_DESCENT_DEFAULTS),
				1,
				profileVersioner.getCurrentVersion(),
				profileVersioner.getProfileKind())); 
		return profiles;
	}
	
	
	/**
	 * @return Returns the settings for the default profile.
	 */	
	public static Map getBuiltInProfile(String name) {
		final Map options= DefaultCodeFormatterConstants.getBuiltInProfile(name);

		ProfileVersioner.setLatestCompliance(options);
		return options;
	}
	
	/** 
	 * @return Returns the default settings.
	 */
	public static Map getDefaultSettings() {
		return getBuiltInProfile(DefaultCodeFormatterConstants.DEFAULT_PROFILE);
	}


	/* (non-Javadoc)
     * @see descent.internal.ui.preferences.formatter.ProfileManager#getSelectedProfileId(org.eclipse.core.runtime.preferences.IScopeContext)
     */
	protected String getSelectedProfileId(IScopeContext instanceScope) { 
		String profileId= instanceScope.getNode(JavaUI.ID_PLUGIN).get(PROFILE_KEY, null);
		if (profileId == null) {
			// request from bug 129427
			profileId= new DefaultScope().getNode(JavaUI.ID_PLUGIN).get(PROFILE_KEY, null);
			// fix for bug 89739
			if (DefaultCodeFormatterConstants.DEFAULT_PROFILE.equals(profileId)) { // default default: 
				IEclipsePreferences node= instanceScope.getNode(JavaCore.PLUGIN_ID);
				if (node != null) {
					String tabSetting= node.get(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR, null);
					if (JavaCore.SPACE.equals(tabSetting)) {
						profileId= DefaultCodeFormatterConstants.PROFILE_DESCENT_DEFAULTS;
					}
				}
			}
		}
	    return profileId;
    }


	/* (non-Javadoc)
     * @see descent.internal.ui.preferences.formatter.ProfileManager#getDefaultProfile()
     */
    public Profile getDefaultProfile() {
	    return getProfile(DefaultCodeFormatterConstants.DEFAULT_PROFILE);
    }
    
}

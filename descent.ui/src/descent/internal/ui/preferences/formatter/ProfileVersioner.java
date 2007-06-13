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

import java.util.Iterator;
import java.util.Map;

import descent.core.JavaCore;
import descent.core.formatter.DefaultCodeFormatterConstants;

import descent.internal.corext.util.JavaModelUtil;

import descent.internal.ui.preferences.formatter.ProfileManager.CustomProfile;


public class ProfileVersioner implements IProfileVersioner {
	
	public static final String CODE_FORMATTER_PROFILE_KIND= "CodeFormatterProfile"; //$NON-NLS-1$
	
	private static final int VERSION_1= 1; // Initial checkin ofdescent formatter
	
	private static final int CURRENT_VERSION= VERSION_1;
	
	public int getFirstVersion() {
	    return VERSION_1;
    }

	public int getCurrentVersion() {
	    return CURRENT_VERSION;
    }
	
	/**
     * {@inheritDoc}
     */
    public String getProfileKind() {
	    return CODE_FORMATTER_PROFILE_KIND;
    }

	public void update(CustomProfile profile) {
		final Map oldSettings= profile.getSettings();
		Map newSettings= updateAndComplete(oldSettings, profile.getVersion());
		profile.setVersion(CURRENT_VERSION);
		profile.setSettings(newSettings);
	}
	
	private static Map updateAndComplete(Map oldSettings, int version) {
		final Map newSettings= FormatterProfileManager.getDefaultSettings();
		
		switch (version) {
		    
		default:
		    for (final Iterator iter= oldSettings.keySet().iterator(); iter.hasNext(); ) {
		        final String key= (String)iter.next();
		        if (!newSettings.containsKey(key)) 
		            continue;
		        
		        final String value= (String)oldSettings.get(key);
		        if (value != null) {
		            newSettings.put(key, value);
		        }
		    }

		}
		setLatestCompliance(newSettings);
		return newSettings;
	}
	
	/**
	 * Updates the map to use the latest the source compliance
	 * @param map The map to update
	 */
	public static void setLatestCompliance(Map map) {
		JavaModelUtil.set50CompilanceOptions(map);
	}

	public static int getVersionStatus(CustomProfile profile) {
		final int version= profile.getVersion();
		if (version < CURRENT_VERSION) 
			return -1;
		else if (version > CURRENT_VERSION)
			return 1;
		else 
			return 0;
	}
	
	
	private static void mapOldValueRangeToNew(Map settings, String oldKey, String [] oldValues,
		String newKey, String [] newValues) {

		if (!settings.containsKey(oldKey)) 
			return;
		
		final String value= ((String)settings.get(oldKey));

		if (value == null) 
			return;
		
		for (int i = 0; i < oldValues.length; i++) {
			if (value.equals(oldValues[i])) {
				settings.put(newKey, newValues[i]);
			}
		}

	}
	
	private static void duplicate(Map settings, String existingKey, String newKey) {
		checkAndReplace(settings, existingKey, new String [] {newKey});
	}
	
	private static void checkAndReplace(Map settings, String oldKey, String newKey) {
		checkAndReplace(settings, oldKey, new String [] {newKey});
	}
	
	private static void checkAndReplace(Map settings, String oldKey, String newKey1, String newKey2) {
		checkAndReplace(settings, oldKey, new String [] {newKey1, newKey2});
	}

	private static void checkAndReplace(Map settings, String oldKey, String [] newKeys) {
		if (!settings.containsKey(oldKey)) 
			return;
		
		final String value= (String)settings.get(oldKey);

		if (value == null) 
			return;
		
		for (int i = 0; i < newKeys.length; i++) {
			settings.put(newKeys[i], value);
		}
	}
	
	private static void checkAndReplaceBooleanWithINSERT(Map settings, String oldKey, String newKey) {
		if (!settings.containsKey(oldKey)) 
			return;
		
		String value= (String)settings.get(oldKey);

		if (value == null) 
			return;
		
		if (DefaultCodeFormatterConstants.TRUE.equals(value))
			value= JavaCore.INSERT;
		else
			value= JavaCore.DO_NOT_INSERT;
		
		settings.put(newKey, value);
	}

   
 }

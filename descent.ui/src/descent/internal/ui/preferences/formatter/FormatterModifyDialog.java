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
package descent.internal.ui.preferences.formatter;

import java.util.Map;

import org.eclipse.swt.widgets.Shell;

import descent.internal.ui.preferences.formatter.ProfileManager.Profile;

public class FormatterModifyDialog extends ModifyDialog {
    
    public FormatterModifyDialog(Shell parentShell, Profile profile, ProfileManager profileManager, ProfileStore profileStore, boolean newProfile, String dialogPreferencesKey, String lastSavePathKey) {
		super(parentShell, profile, profileManager, profileStore, newProfile, dialogPreferencesKey, lastSavePathKey);
	}
	
	protected void addPages(Map values) {
	    addTabPage(FormatterMessages.ModifyDialog_tabpage_indentation_title, new IndentationTabPage(this, values)); 
		addTabPage(FormatterMessages.ModifyDialog_tabpage_braces_title, new BracesTabPage(this, values)); 
		addTabPage(FormatterMessages.ModifyDialog_tabpage_succinctness_title, new SuccinctnessTabPage(this, values)); 
		addTabPage(FormatterMessages.ModifyDialog_tabpage_white_space_title, new WhiteSpaceTabPage(this, values)); 
    }
	
}

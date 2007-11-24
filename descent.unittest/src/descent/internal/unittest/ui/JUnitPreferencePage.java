/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Sebastian Davids: sdavids@gmx.de
 *******************************************************************************/
package descent.internal.unittest.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.PreferencePage;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

/**
 * Preference page for D Unittest settings. I assume there _will_ be some
 * preferences at some point, so I'll keep the support around, but having
 * stack trace filters doesn't seem that useful (even if we get stack traces,
 * user's probably won't want to filter out anything since all the current
 * filters apply to JUnit specifically). As for the "enable assertions" thing,
 * I honestly have no idea what that does, and I'm not sure if assertions can
 * be disabled in a remote process. So the test page is blank for now.
 */
public class JUnitPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
		
	public JUnitPreferencePage() {
		super();
		setDescription(JUnitMessages.JUnitPreferencePage_description); 
		setPreferenceStore(DescentUnittestPlugin.getDefault().getPreferenceStore());
	}

	protected Control createContents(Composite parent) {
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, IJUnitHelpContextIds.JUNIT_PREFERENCE_PAGE);

		Composite composite= new Composite(parent, SWT.NULL);
		GridLayout layout= new GridLayout();
		layout.numColumns= 1;
		layout.marginHeight= 0;
		layout.marginWidth= 0;
		composite.setLayout(layout);
		GridData data= new GridData();
		data.verticalAlignment= GridData.FILL;
		data.horizontalAlignment= GridData.FILL;
		composite.setLayoutData(data);

		Dialog.applyDialogFont(composite);
		return composite;
	}

	public void init(IWorkbench workbench) {}
	
	public boolean performOk() {
		return true;
	}
}

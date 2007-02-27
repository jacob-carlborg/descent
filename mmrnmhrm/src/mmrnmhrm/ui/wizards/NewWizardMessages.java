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
package mmrnmhrm.ui.wizards;

import org.eclipse.osgi.util.NLS;

public final class NewWizardMessages extends NLS {

	private static final String BUNDLE_NAME= 
		"mmrnmhrm.ui.wizards.NewWizardMessages";//$NON-NLS-1$

	private NewWizardMessages() {
		// Do not instantiate
	}

	public static String JavaProjectWizardFirstPage_NameGroup_label_text;
	public static String JavaProjectWizardFirstPage_LocationGroup_title;
	public static String JavaProjectWizardFirstPage_LocationGroup_external_desc;
	public static String JavaProjectWizardFirstPage_LocationGroup_workspace_desc;
	public static String JavaProjectWizardFirstPage_LocationGroup_locationLabel_desc;
	public static String JavaProjectWizardFirstPage_LocationGroup_browseButton_desc;
	public static String JavaProjectWizardFirstPage_LayoutGroup_title;
	public static String JavaProjectWizardFirstPage_LayoutGroup_option_separateFolders;
	public static String JavaProjectWizardFirstPage_LayoutGroup_option_oneFolder;
	public static String JavaProjectWizardFirstPage_LayoutGroup_configure;
	public static String JavaProjectWizardFirstPage_DetectGroup_message;
	public static String JavaProjectWizardFirstPage_Message_enterProjectName;
	public static String JavaProjectWizardFirstPage_Message_projectAlreadyExists;
	public static String JavaProjectWizardFirstPage_Message_enterLocation;
	public static String JavaProjectWizardFirstPage_Message_invalidDirectory;
	public static String JavaProjectWizardFirstPage_Message_cannotCreateInWorkspace;
	public static String JavaProjectWizardFirstPage_page_pageName;
	public static String JavaProjectWizardFirstPage_page_title;
	public static String JavaProjectWizardFirstPage_page_description;
	
	public static String JavaProjectWizardFirstPage_directory_message;
	
	public static String JavaProjectWizardFirstPage_LayoutGroup_link_description;
	public static String JavaProjectWizardFirstPage_JREGroup_title;
	public static String JavaProjectWizardFirstPage_JREGroup_default_compliance;
	public static String JavaProjectWizardFirstPage_JREGroup_link_description;
	public static String JavaProjectWizardFirstPage_JREGroup_specific_compliance;

	public static String NewElementWizard_op_error_title;
	public static String NewElementWizard_op_error_message;

	static {
		NLS.initializeMessages(BUNDLE_NAME, NewWizardMessages.class);
	}
}

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
package descent.internal.ui.preferences;

import java.util.StringTokenizer;

import org.eclipse.jface.preference.IPreferenceStore;

import descent.core.IJavaProject;

import descent.ui.PreferenceConstants;

import descent.internal.corext.codemanipulation.CodeGenerationSettings;
import descent.internal.corext.util.CodeFormatterUtil;

public class JavaPreferencesSettings  {
	
	
	public static CodeGenerationSettings getCodeGenerationSettings(IJavaProject project) {
		CodeGenerationSettings res= new CodeGenerationSettings();
		res.createComments= Boolean.valueOf(PreferenceConstants.getPreference(PreferenceConstants.CODEGEN_ADD_COMMENTS, project)).booleanValue();
		res.useKeywordThis= Boolean.valueOf(PreferenceConstants.getPreference(PreferenceConstants.CODEGEN_KEYWORD_THIS, project)).booleanValue();
		res.overrideAnnotation= Boolean.valueOf(PreferenceConstants.getPreference(PreferenceConstants.CODEGEN_USE_OVERRIDE_ANNOTATION, project)).booleanValue();
		res.importOrder= getImportOrderPreference(project);
		res.importThreshold= getImportNumberThreshold(project);
		res.staticImportThreshold= getStaticImportNumberThreshold(project);
		res.importIgnoreLowercase= Boolean.valueOf(PreferenceConstants.getPreference(PreferenceConstants.ORGIMPORTS_IGNORELOWERCASE, project)).booleanValue();
		res.tabWidth= CodeFormatterUtil.getTabWidth(project);
		res.indentWidth= CodeFormatterUtil.getIndentWidth(project);
		return res;
	}
	
	/**
	 * @deprecated Use getCodeGenerationSettings(IJavaProject) instead
	 */
	public static CodeGenerationSettings getCodeGenerationSettings() {
		return getCodeGenerationSettings(null);
	}

	public static int getImportNumberThreshold(IJavaProject project) {
		String thresholdStr= PreferenceConstants.getPreference(PreferenceConstants.ORGIMPORTS_ONDEMANDTHRESHOLD, project);
		try {
			int threshold= Integer.parseInt(thresholdStr);
			if (threshold < 0) {
				threshold= Integer.MAX_VALUE;
			}
			return threshold;
		} catch (NumberFormatException e) {
			return Integer.MAX_VALUE;
		}
	}
	
	public static int getStaticImportNumberThreshold(IJavaProject project) {
		String thresholdStr= PreferenceConstants.getPreference(PreferenceConstants.ORGIMPORTS_ONDEMANDTHRESHOLD, project);
		try {
			int threshold= Integer.parseInt(thresholdStr);
			if (threshold < 0) {
				threshold= Integer.MAX_VALUE;
			}
			return threshold;
		} catch (NumberFormatException e) {
			return Integer.MAX_VALUE;
		}
	}

	public static String[] getImportOrderPreference(IJavaProject project) {
		String str= PreferenceConstants.getPreference(PreferenceConstants.ORGIMPORTS_IMPORTORDER, project);
		if (str != null) {
			return unpackList(str, ";"); //$NON-NLS-1$
		}
		return new String[0];
	}
	
	/**
	 * @deprecated Use getImportNumberThreshold(IJavaProject) instead
	 */
	public static int getImportNumberThreshold(IPreferenceStore prefs) {
		int threshold= prefs.getInt(PreferenceConstants.ORGIMPORTS_ONDEMANDTHRESHOLD);
		if (threshold < 0) {
			threshold= Integer.MAX_VALUE;
		}
		return threshold;
	}

	/**
	 * @deprecated Use getImportOrderPreference(IJavaProject) instead
	 */
	public static String[] getImportOrderPreference(IPreferenceStore prefs) {
		String str= prefs.getString(PreferenceConstants.ORGIMPORTS_IMPORTORDER);
		if (str != null) {
			return unpackList(str, ";"); //$NON-NLS-1$
		}
		return new String[0];
	}
		
	private static String[] unpackList(String str, String separator) {
		StringTokenizer tok= new StringTokenizer(str, separator); 
		int nTokens= tok.countTokens();
		String[] res= new String[nTokens];
		for (int i= 0; i < nTokens; i++) {
			res[i]= tok.nextToken().trim();
		}
		return res;
	}
	
		
}


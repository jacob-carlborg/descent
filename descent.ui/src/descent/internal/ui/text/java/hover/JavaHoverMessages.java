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
package descent.internal.ui.text.java.hover;

import org.eclipse.osgi.util.NLS;

/**
 * Helper class to get NLSed messages.
 */
final class JavaHoverMessages extends NLS {

	private static final String BUNDLE_NAME= JavaHoverMessages.class.getName();

	private JavaHoverMessages() {
		// Do not instantiate
	}

	public static String JavadocHover_back;
	public static String JavadocHover_back_toElement_toolTip;
	public static String JavadocHover_back_toolTip;
	public static String JavadocHover_forward;
	public static String JavadocHover_forward_toElement_toolTip;
	public static String JavadocHover_forward_toolTip;
	public static String JavadocHover_showInJavadoc;
	public static String JavadocHover_openDeclaration;
	public static String JavadocHover_noAttachedInformation;
	public static String JavaTextHover_createTextHover;
	public static String JavaTextHover_makeStickyHint;
	public static String NoBreakpointAnnotation_addBreakpoint;
	public static String NLSStringHover_NLSStringHover_missingKeyWarning;
	public static String AbstractAnnotationHover_action_configureAnnotationPreferences;
	public static String AbstractAnnotationHover_message_singleQuickFix;
	public static String AbstractAnnotationHover_message_multipleQuickFix;

	static {
		NLS.initializeMessages(BUNDLE_NAME, JavaHoverMessages.class);
	}
}

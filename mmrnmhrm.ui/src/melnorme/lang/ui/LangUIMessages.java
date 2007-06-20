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
package melnorme.lang.ui;

import org.eclipse.osgi.util.NLS;

public final class LangUIMessages extends NLS {

	private static final String BUNDLE_NAME= "mmrnmhrm.ui.LangUIMessages"; //$NON-NLS-1$

	private LangUIMessages() {
		// Do not instantiate
	}

	public static String LangPlugin_internal_error;
	
	public static String ExceptionDialog_seeErrorLogMessage;
	
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, LangUIMessages.class);
	}

}

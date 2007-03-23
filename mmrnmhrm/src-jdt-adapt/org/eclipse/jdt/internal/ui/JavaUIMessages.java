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
package org.eclipse.jdt.internal.ui;

import org.eclipse.osgi.util.NLS;

public final class JavaUIMessages extends NLS {

	private static final String BUNDLE_NAME= "org.eclipse.jdt.internal.ui.JavaUIMessages";//$NON-NLS-1$

	private JavaUIMessages() {
		// Do not instantiate
	}

	public static String JavaPlugin_internal_error;
	

	public static String ExceptionDialog_seeErrorLogMessage;
	
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, JavaUIMessages.class);
	}

}

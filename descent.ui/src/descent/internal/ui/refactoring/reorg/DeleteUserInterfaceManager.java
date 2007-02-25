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
package descent.internal.ui.refactoring.reorg;

import descent.internal.corext.refactoring.reorg.JavaDeleteProcessor;

import descent.internal.ui.refactoring.UserInterfaceManager;
import descent.internal.ui.refactoring.UserInterfaceStarter;

public class DeleteUserInterfaceManager extends UserInterfaceManager {
	private static final UserInterfaceManager fgInstance= new DeleteUserInterfaceManager();
	
	public static UserInterfaceManager getDefault() {
		return fgInstance;
	}
	
	private DeleteUserInterfaceManager() {
		put(JavaDeleteProcessor.class, UserInterfaceStarter.class, DeleteWizard.class);
	}
}

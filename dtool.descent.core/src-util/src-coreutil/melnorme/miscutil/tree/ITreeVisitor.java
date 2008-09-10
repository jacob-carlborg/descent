/*******************************************************************************
 * Copyright (c) 2007 DSource.org and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial implementation
 *******************************************************************************/
package melnorme.miscutil.tree;

public interface ITreeVisitor<NODE extends IElement> {

	/** Generic visit start. */
	void preVisit(NODE elem);

	/** Generic visit end. */
	void postVisit(NODE elem);

}
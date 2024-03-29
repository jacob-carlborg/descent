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
package descent.internal.core.dom.rewrite;

import org.eclipse.jface.text.IRegion;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.text.edits.TextEditGroup;

import descent.core.dom.ASTNode;
import descent.core.dom.rewrite.ITrackedNodePosition;

/**
 *
 */
public class TrackedNodePosition implements ITrackedNodePosition {

	private final TextEditGroup group;
	private final ASTNode node;
	
	public TrackedNodePosition(TextEditGroup group, ASTNode node) {
		this.group= group;
		this.node= node;
	}

	/* (non-Javadoc)
	 * @see descent.internal.corext.dom.ITrackedNodePosition#getStartPosition()
	 */
	public int getStartPosition() {
		if (this.group.isEmpty()) {
			return this.node.getStartPosition();
		}
		IRegion coverage= TextEdit.getCoverage(this.group.getTextEdits());
		if (coverage == null) {
			return this.node.getStartPosition();
		}
		return coverage.getOffset();
	}

	/* (non-Javadoc)
	 * @see descent.internal.corext.dom.ITrackedNodePosition#getLength()
	 */
	public int getLength() {
		if (this.group.isEmpty()) {
			return this.node.getLength();
		}
		IRegion coverage= TextEdit.getCoverage(this.group.getTextEdits());
		if (coverage == null) {
			return this.node.getLength();
		}
		return coverage.getLength();
	}
}

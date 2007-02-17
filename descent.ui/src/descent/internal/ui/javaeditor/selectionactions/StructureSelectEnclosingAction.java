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
package descent.internal.ui.javaeditor.selectionactions;

import org.eclipse.ui.PlatformUI;

import descent.core.ISourceRange;
import descent.core.ISourceReference;
import descent.core.JavaModelException;
import descent.core.dom.ASTNode;

import descent.internal.corext.dom.SelectionAnalyzer;

import descent.internal.ui.IJavaHelpContextIds;
import descent.internal.ui.javaeditor.JavaEditor;

public class StructureSelectEnclosingAction extends StructureSelectionAction {

	public StructureSelectEnclosingAction(JavaEditor editor, SelectionHistory history) {
		super(SelectionActionMessages.StructureSelectEnclosing_label, editor, history);
		setToolTipText(SelectionActionMessages.StructureSelectEnclosing_tooltip);
		setDescription(SelectionActionMessages.StructureSelectEnclosing_description);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.STRUCTURED_SELECT_ENCLOSING_ACTION);
	}

	/*
	 * This constructor is for testing purpose only.
	 */
	public StructureSelectEnclosingAction() {
	}

    /*
     * @see StructureSelectionAction#internalGetNewSelectionRange(ISourceRange, ICompilationUnit, SelectionAnalyzer)
     */
	ISourceRange internalGetNewSelectionRange(ISourceRange oldSourceRange, ISourceReference sr, SelectionAnalyzer selAnalyzer) throws JavaModelException{
		ASTNode first= selAnalyzer.getFirstSelectedNode();
		if (first == null || first.getParent() == null)
			return getLastCoveringNodeRange(oldSourceRange, sr, selAnalyzer);

		return getSelectedNodeSourceRange(sr, first.getParent());
	}
}

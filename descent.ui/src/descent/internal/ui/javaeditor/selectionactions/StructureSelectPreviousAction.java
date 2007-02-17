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

import descent.core.ISourceRange;
import descent.core.ISourceReference;
import descent.core.JavaModelException;
import descent.core.dom.ASTNode;
import descent.core.dom.GenericVisitor;

import org.eclipse.ui.PlatformUI;

import descent.internal.corext.dom.SelectionAnalyzer;

import descent.internal.ui.IJavaHelpContextIds;
import descent.internal.ui.javaeditor.JavaEditor;

public class StructureSelectPreviousAction extends StructureSelectionAction {

	private static class PreviousNodeAnalyzer extends GenericVisitor {
		private final int fOffset;
		private ASTNode fPreviousNode;
		private PreviousNodeAnalyzer(int offset) {
			super();
			fOffset= offset;
		}
		public static ASTNode perform(int offset, ASTNode lastCoveringNode) {
			PreviousNodeAnalyzer analyzer= new PreviousNodeAnalyzer(offset);
			lastCoveringNode.accept(analyzer);
			return analyzer.fPreviousNode;
		}
		protected boolean visitNode(ASTNode node) {
			int start= node.getStartPosition();
			int end= start + node.getLength();
			if (end == fOffset) {
				fPreviousNode= node;
				return true;
			} else {
				return (start < fOffset && fOffset < end);
			}
		}
	}

	public StructureSelectPreviousAction(JavaEditor editor, SelectionHistory history) {
		super(SelectionActionMessages.StructureSelectPrevious_label, editor, history);
		setToolTipText(SelectionActionMessages.StructureSelectPrevious_tooltip);
		setDescription(SelectionActionMessages.StructureSelectPrevious_description);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.STRUCTURED_SELECT_PREVIOUS_ACTION);
	}

	/*
	 * This constructor is for testing purpose only.
	 */
	public StructureSelectPreviousAction() {
	}

	/* non java doc
	 * @see StructureSelectionAction#internalGetNewSelectionRange(ISourceRange, ICompilationUnit, SelectionAnalyzer)
	 */
	ISourceRange internalGetNewSelectionRange(ISourceRange oldSourceRange, ISourceReference sr, SelectionAnalyzer selAnalyzer) throws JavaModelException{
		if (oldSourceRange.getLength() == 0 && selAnalyzer.getLastCoveringNode() != null) {
			ASTNode previousNode= PreviousNodeAnalyzer.perform(oldSourceRange.getOffset(), selAnalyzer.getLastCoveringNode());
			if (previousNode != null)
				return getSelectedNodeSourceRange(sr, previousNode);
		}
		ASTNode first= selAnalyzer.getFirstSelectedNode();
		if (first == null)
			return getLastCoveringNodeRange(oldSourceRange, sr, selAnalyzer);

		ASTNode parent= first.getParent();
		if (parent == null)
			return getLastCoveringNodeRange(oldSourceRange, sr, selAnalyzer);

		ASTNode previousNode= getPreviousNode(parent, selAnalyzer.getSelectedNodes()[0]);
		if (previousNode == parent)
			return getSelectedNodeSourceRange(sr, parent);

		int offset= previousNode.getStartPosition();
		int end= oldSourceRange.getOffset() + oldSourceRange.getLength() - 1;
		return StructureSelectionAction.createSourceRange(offset, end);
	}

	private static ASTNode getPreviousNode(ASTNode parent, ASTNode node){
		ASTNode[] siblingNodes= StructureSelectionAction.getSiblingNodes(node);
		if (siblingNodes == null || siblingNodes.length == 0)
			return parent;
		if (node == siblingNodes[0]) {
			return parent;
		} else {
			int index= StructureSelectionAction.findIndex(siblingNodes, node);
			if (index < 1)
				return parent;
			return siblingNodes[index - 1];
		}
	}
}


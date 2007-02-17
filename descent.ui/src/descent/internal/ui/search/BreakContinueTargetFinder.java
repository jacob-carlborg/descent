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
package descent.internal.ui.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import descent.core.IJavaElement;
import descent.core.ISourceReference;
import descent.core.JavaModelException;
import descent.core.ToolFactory;
import descent.core.compiler.IScanner;
import descent.core.compiler.InvalidInputException;
import descent.core.dom.AST;
import descent.core.dom.ASTNode;
import descent.core.dom.ASTVisitor;
import descent.core.dom.Block;
import descent.core.dom.BreakStatement;
import descent.core.dom.CompilationUnit;
import descent.core.dom.ContinueStatement;
import descent.core.dom.DoStatement;
import descent.core.dom.ForeachStatement;
import descent.core.dom.ForStatement;
import descent.core.dom.Initializer;
import descent.core.dom.LabeledStatement;
import descent.core.dom.FunctionDeclaration;
import descent.core.dom.SimpleName;
import descent.core.dom.SwitchStatement;
import descent.core.dom.WhileStatement;

import descent.internal.corext.dom.ASTNodes;
import descent.internal.corext.dom.NodeFinder;

import descent.internal.ui.JavaPlugin;
import descent.internal.ui.javaeditor.ASTProvider;

/**
 * Class used to find the target for a break or continue statement according 
 * to the language specification.
 * <p> 
 * The target statement is a while, do, switch, for or a labeled statement.
 * Break is described in section 14.15 of the JLS3 and continue in section 14.16.</p>
 * 
 * @since 3.2
 */
public class BreakContinueTargetFinder extends ASTVisitor {
	private ASTNode fSelected;
	private boolean fIsBreak;
	private SimpleName fLabel;
	private String fContents;//contents are used for scanning to select the right extent of the keyword
	private static final Class[] STOPPERS=        {FunctionDeclaration.class, Initializer.class};
	private static final Class[] BREAKTARGETS=    {ForStatement.class, ForeachStatement.class, WhileStatement.class, DoStatement.class, SwitchStatement.class};
	private static final Class[] CONTINUETARGETS= {ForStatement.class, ForeachStatement.class, WhileStatement.class, DoStatement.class};
	private static final int BRACE_LENGTH= 1;

	/*
	 * Initializes the finder. Returns error message or <code>null</code> if everything is OK.
	 */
	public String initialize(CompilationUnit root, int offset, int length) {
		return initialize(root, NodeFinder.perform(root, offset, length));
	}
	
	/*
	 * Initializes the finder. Returns error message or <code>null</code> if everything is OK.
	 */
	public String initialize(CompilationUnit root, ASTNode node) {
		ASTNode controlNode= getBreakOrContinueNode(node);
		if (controlNode != null){
			fContents= getContents(root);
			if (fContents == null)
				return SearchMessages.BreakContinueTargetFinder_cannot_highlight;

			fSelected= controlNode;
			fIsBreak= fSelected instanceof BreakStatement;
			fLabel= getLabel();
			return null;
		} else {
			return SearchMessages.BreakContinueTargetFinder_no_break_or_continue_selected;
		}
	}

	/* Returns contents or <code>null</code> if there's trouble. */
	private String getContents(CompilationUnit root) {
		try {
			IJavaElement rootElem= root.getJavaElement();
			if ((rootElem instanceof ISourceReference))
				return ((ISourceReference)rootElem).getSource();
			else
				return null;
		} catch (JavaModelException e) {
			//We must handle it here because JavaEditor does not expect an exception
			
			/* showing a dialog here would be too heavy but we cannot just 
             * swallow the exception */
			JavaPlugin.log(e); 
			return null;
		}
	}

	//extract the control node: handle labels
	private ASTNode getBreakOrContinueNode(ASTNode selectedNode) {
		if (selectedNode instanceof BreakStatement)
			return selectedNode;
		if (selectedNode instanceof ContinueStatement)
			return selectedNode;
		if (selectedNode instanceof SimpleName && selectedNode.getParent() instanceof BreakStatement)
			return selectedNode.getParent();
		if (selectedNode instanceof SimpleName && selectedNode.getParent() instanceof ContinueStatement)
			return selectedNode.getParent();
		return null;
	}

	public List perform() {
		return getNodesToHighlight();
	}

	private SimpleName getLabel() {
		if (fIsBreak){
			BreakStatement bs= (BreakStatement) fSelected;
			return bs.getLabel();
		} else {
			ContinueStatement cs= (ContinueStatement) fSelected;
			return cs.getLabel();
		} 
	}
	
	private List getNodesToHighlight() {
		ASTNode targetNode= findTargetNode(fSelected);
		if (!isEnclosingStatement(targetNode))
			return Collections.EMPTY_LIST;
		
		List list= new ArrayList();
		ASTNode node= makeFakeNodeForFirstToken(targetNode);
		if (node != null)
			list.add(node);
		
		if (fIsBreak) {
			node= makeFakeNodeForClosingBrace(targetNode);
			if (node != null)
				list.add(node);
		}
		
		return list;
			
	}

	private boolean isEnclosingStatement(ASTNode targetNode) {
		return (targetNode != null) && !(targetNode instanceof FunctionDeclaration) && !(targetNode instanceof Initializer);
	}

	private ASTNode findTargetNode(ASTNode node) {
		do {
			node= node.getParent();
		} while (keepWalkingUp(node));
		return node;
	}

	private ASTNode makeFakeNodeForFirstToken(ASTNode node) {
		try {
			int length= getLengthOfFirstTokenOf(node);
			if (length < 1)
				return node;//fallback
			return makeFakeNode(node.getStartPosition(), length, node.getAST());
		} catch (InvalidInputException e) {
			return node;//fallback
		}
	}

	private SimpleName makeFakeNode(int start, int length, AST ast) {
		String fakeName= makeStringOfLength(length);
		SimpleName name= ast.newSimpleName(fakeName);
		name.setSourceRange(start, length);
		return name;
	}

	private ASTNode makeFakeNodeForClosingBrace(ASTNode targetNode) {
		ASTNode maybeBlock= getOptionalBlock(targetNode);
		if (maybeBlock == null)
			return null;
		
		/* Ideally, we'd scan backwards to find the '}' token, but it may be an overkill
		 * so I'll just assume the closing brace token has a fixed length. */
		return makeFakeNode(ASTNodes.getExclusiveEnd(maybeBlock)-BRACE_LENGTH, BRACE_LENGTH, targetNode.getAST());
	}

	/*
	 * Block cannot be return type here because SwitchStatement has no block 
	 * and yet it does have a closing brace. 
	 */
	private ASTNode getOptionalBlock(ASTNode targetNode) {
		final ASTNode[] maybeBlock= new ASTNode[1];
		targetNode.accept(new ASTVisitor(){
			public boolean visit(ForStatement node) {
				if (node.getBody() instanceof Block)
					maybeBlock[0]= node.getBody(); 
				return false;
			}
			public boolean visit(ForeachStatement node) {
				if (node.getBody() instanceof Block)
					maybeBlock[0]= node.getBody(); 
				return false;
			}
			public boolean visit(WhileStatement node) {
				if (node.getBody() instanceof Block)
					maybeBlock[0]= node.getBody(); 
				return false;
			}
			public boolean visit(DoStatement node) {
				if (node.getBody() instanceof Block)
					maybeBlock[0]= node.getBody(); 
				return false;
			}
			public boolean visit(SwitchStatement node) {
				maybeBlock[0]= node; 
				return false;
			}
		});
		return maybeBlock[0];
	}

	private static String makeStringOfLength(int length) {
		char[] chars= new char[length];
		Arrays.fill(chars, 'x');
		return new String(chars);
	}

	//must scan because of unicode
	private int getLengthOfFirstTokenOf(ASTNode node) throws InvalidInputException {
		IScanner scanner= ToolFactory.createScanner(true, true, true, true, ASTProvider.SHARED_AST_LEVEL);
		scanner.setSource(getSource(node).toCharArray());
		scanner.getNextToken();
		return scanner.getRawTokenSource().length;
	}

	private String getSource(ASTNode node) {
		return fContents.substring(node.getStartPosition(), ASTNodes.getInclusiveEnd(node));
	}

	private boolean keepWalkingUp(ASTNode node) {
		if (node == null)
			return false;
		if (isAnyInstanceOf(STOPPERS, node))
			return false;
		if (fLabel != null && LabeledStatement.class.isInstance(node)){
			LabeledStatement ls= (LabeledStatement)node;
			return ! areEqualLabels(ls.getLabel(), fLabel);
		}
		if (fLabel == null &&  fIsBreak && isAnyInstanceOf(BREAKTARGETS, node))
			return false;
		if (fLabel == null && !fIsBreak && isAnyInstanceOf(CONTINUETARGETS, node))
			return false;
		return true;
	}

	//TODO see bug 33739 - resolveBinding always returns null
	//so we just compare names
	private static boolean areEqualLabels(SimpleName labelToMatch, SimpleName labelSelected) {
		return labelSelected.getIdentifier().equals(labelToMatch.getIdentifier());
	}

	private static boolean isAnyInstanceOf(Class[] continueTargets, ASTNode node) {
		for (int i= 0; i < continueTargets.length; i++) {
			if (continueTargets[i].isInstance(node))
				return true;
		}
		return false;
	}
}

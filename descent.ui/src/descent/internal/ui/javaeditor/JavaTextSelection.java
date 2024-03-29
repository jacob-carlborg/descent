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
package descent.internal.ui.javaeditor;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextSelection;

import descent.core.ICompilationUnit;
import descent.core.IJavaElement;
import descent.core.JavaModelException;
import descent.core.dom.ASTNode;
import descent.core.dom.AggregateDeclaration;
import descent.core.dom.CompilationUnit;
import descent.core.dom.Declaration;
import descent.core.dom.VariableDeclarationFragment;
import descent.internal.corext.dom.Selection;
import descent.internal.corext.dom.SelectionAnalyzer;
import descent.internal.ui.JavaPlugin;
import descent.internal.ui.actions.SelectionConverter;

/**
 * A special text selection that gives access to the resolved and
 * enclosing element.
 */
public class JavaTextSelection extends TextSelection {

	private IJavaElement fElement;
	private IJavaElement[] fResolvedElements;

	private boolean fEnclosingElementRequested;
	private IJavaElement fEnclosingElement;

	private boolean fPartialASTRequested;
	private CompilationUnit fPartialAST;

	private boolean fNodesRequested;
	private ASTNode[] fSelectedNodes;
	private ASTNode fCoveringNode;

	private boolean fInMethodBodyRequested;
	private boolean fInMethodBody;

	private boolean fInClassInitializerRequested;
	private boolean fInClassInitializer;

	private boolean fInVariableInitializerRequested;
	private boolean fInVariableInitializer;

	/**
	 * Creates a new text selection at the given offset and length.
	 */
	public JavaTextSelection(IJavaElement element, IDocument document, int offset, int length) {
		super(document, offset, length);
		fElement= element;
	}

	/**
	 * Resolves the <code>IJavaElement</code>s at the current offset. Returns
	 * an empty array if the string under the offset doesn't resolve to a
	 * <code>IJavaElement</code>.
	 *
	 * @return the resolved java elements at the current offset
	 * @throws JavaModelException passed from the underlying code resolve API
	 */
	public IJavaElement[] resolveElementAtOffset() throws JavaModelException {
		if (fResolvedElements != null)
			return fResolvedElements;
		// long start= System.currentTimeMillis();
		fResolvedElements= SelectionConverter.codeResolve(fElement, this);
		// System.out.println("Time resolving element: " + (System.currentTimeMillis() - start));
		return fResolvedElements;
	}

	public IJavaElement resolveEnclosingElement() throws JavaModelException {
		if (fEnclosingElementRequested)
			return fEnclosingElement;
		fEnclosingElementRequested= true;
		fEnclosingElement= SelectionConverter.resolveEnclosingElement(fElement, this);
		return fEnclosingElement;
	}

	public CompilationUnit resolvePartialAstAtOffset() {
		if (fPartialASTRequested)
			return fPartialAST;
		fPartialASTRequested= true;
		if (! (fElement instanceof ICompilationUnit))
			return null;
		// long start= System.currentTimeMillis();
		fPartialAST= JavaPlugin.getDefault().getASTProvider().getAST(fElement, ASTProvider.WAIT_YES, null);
		// System.out.println("Time requesting partial AST: " + (System.currentTimeMillis() - start));
		return fPartialAST;
	}

	public ASTNode[] resolveSelectedNodes() {
		if (fNodesRequested)
			return fSelectedNodes;
		fNodesRequested= true;
		CompilationUnit root= resolvePartialAstAtOffset();
		if (root == null)
			return null;
		Selection ds= Selection.createFromStartLength(getOffset(), getLength());
		SelectionAnalyzer analyzer= new SelectionAnalyzer(ds, false);
		root.accept(analyzer);
		fSelectedNodes= analyzer.getSelectedNodes();
		fCoveringNode= analyzer.getLastCoveringNode();
		return fSelectedNodes;
	}

	public ASTNode resolveCoveringNode() {
		if (fNodesRequested)
			return fCoveringNode;
		resolveSelectedNodes();
		return fCoveringNode;
	}

	public boolean resolveInMethodBody() {
		if (fInMethodBodyRequested)
			return fInMethodBody;
		fInMethodBodyRequested= true;
		resolveSelectedNodes();
		ASTNode node= getStartNode();
		if (node == null) {
			fInMethodBody= true;
		} else {
			while (node != null) {
				int nodeType= node.getNodeType();
				if (nodeType == ASTNode.BLOCK && node.getParent() instanceof Declaration) {
					fInMethodBody= node.getParent().getNodeType() == ASTNode.FUNCTION_DECLARATION;
					break;
				} else if (nodeType == ASTNode.NEW_ANONYMOUS_CLASS_EXPRESSION) {
					fInMethodBody= false;
					break;
				}
				node= node.getParent();
			}
		}
		return fInMethodBody;
	}

	public boolean resolveInClassInitializer() {
		if (fInClassInitializerRequested)
			return fInClassInitializer;
		fInClassInitializerRequested= true;
		resolveSelectedNodes();
		ASTNode node= getStartNode();
		if (node == null) {
			fInClassInitializer= true;
		} else {
			while (node != null) {
				int nodeType= node.getNodeType();
				if (node instanceof AggregateDeclaration) {
					fInClassInitializer= false;
					break;
				} else if (nodeType == ASTNode.NEW_ANONYMOUS_CLASS_EXPRESSION) {
					fInClassInitializer= false;
					break;
				} else if (nodeType == ASTNode.CONSTRUCTOR_DECLARATION) {
					fInClassInitializer= true;
					break;
				}
				node= node.getParent();
			}
		}
		return fInClassInitializer;
	}

	public boolean resolveInVariableInitializer() {
		if (fInVariableInitializerRequested)
			return fInVariableInitializer;
		fInVariableInitializerRequested= true;
		resolveSelectedNodes();
		ASTNode node= getStartNode();
		ASTNode last= null;
		while (node != null) {
			int nodeType= node.getNodeType();
			if (node instanceof AggregateDeclaration) {
				fInVariableInitializer= false;
				break;
			} else if (nodeType == ASTNode.NEW_ANONYMOUS_CLASS_EXPRESSION) {
				fInVariableInitializer= false;
				break;
			} else if (nodeType == ASTNode.VARIABLE_DECLARATION_FRAGMENT &&
					   ((VariableDeclarationFragment)node).getInitializer() == last) {
				fInVariableInitializer= true;
				break;
				/*
			} else if (nodeType == ASTNode.SINGLE_VARIABLE_DECLARATION &&
				       ((SingleVariableDeclaration)node).getInitializer() == last) {
				fInVariableInitializer= true;
				break;
			} else if (nodeType == ASTNode.ANNOTATION_TYPE_MEMBER_DECLARATION &&
				       ((AnnotationTypeMemberDeclaration)node).getDefault() == last) {
				fInVariableInitializer= true;
				break;
			*/
			}			
			last= node;
			node= node.getParent();
		}
		return fInVariableInitializer;
	}

	private ASTNode getStartNode() {
		if (fSelectedNodes != null && fSelectedNodes.length > 0)
			return fSelectedNodes[0];
		else
			return fCoveringNode;
	}
}

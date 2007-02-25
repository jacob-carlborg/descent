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
package descent.internal.corext.refactoring.reorg;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.text.edits.TextEditGroup;

import descent.core.IField;
import descent.core.IJavaElement;
import descent.core.ISourceRange;
import descent.core.IType;
import descent.core.JavaModelException;
import descent.core.dom.ASTNode;
import descent.core.dom.CompilationUnit;
import descent.core.dom.GenericVisitor;
import descent.core.dom.NewAnonymousClassExpression;
import descent.core.dom.VariableDeclaration;
import descent.core.dom.VariableDeclarationFragment;
import descent.internal.corext.refactoring.structure.ASTNodeSearchUtil;
import descent.internal.corext.refactoring.structure.CompilationUnitRewrite;
import descent.internal.corext.util.JdtFlags;

public class ASTNodeDeleteUtil {

	private static ASTNode[] getNodesToDelete(IJavaElement element, CompilationUnit cuNode) throws JavaModelException {
		// fields are different because you don't delete the whole declaration but only a fragment of it
		if (element.getElementType() == IJavaElement.FIELD) {
			if (JdtFlags.isEnum((IField) element))
				return new ASTNode[] { ASTNodeSearchUtil.getEnumConstantDeclaration((IField) element, cuNode)};
			else
				return new ASTNode[] { ASTNodeSearchUtil.getFieldDeclarationFragmentNode((IField) element, cuNode)};
		}
		if (element.getElementType() == IJavaElement.TYPE && ((IType) element).isLocal()) {
			IType type= (IType) element;
			if (type.isAnonymous()) {
				if (type.getParent().getElementType() == IJavaElement.FIELD) {
					final ISourceRange range= type.getSourceRange();
					if (range != null) {
						final ASTNode node= ASTNodeSearchUtil.getAstNode(cuNode, range.getOffset(), range.getLength());
						if (node instanceof NewAnonymousClassExpression)
							return new ASTNode[] { node};
					}
				}
				/* TODO JDT UI delete
				return new ASTNode[] { ASTNodeSearchUtil.getClassInstanceCreationNode(type, cuNode)};
				*/
			} else {
				ASTNode[] nodes= ASTNodeSearchUtil.getDeclarationNodes(element, cuNode);
				// we have to delete the TypeDeclarationStatement
				nodes[0]= nodes[0].getParent();
				return nodes;
			}
		}
		return ASTNodeSearchUtil.getDeclarationNodes(element, cuNode);
	}

	private static Set getRemovedNodes(final List removed, final CompilationUnitRewrite rewrite) {
		final Set result= new HashSet();
		rewrite.getRoot().accept(new GenericVisitor() {

			protected boolean visitNode(ASTNode node) {
				if (removed.contains(node))
					result.add(node);
				return true;
			}
		});
		return result;
	}

	public static void markAsDeleted(IJavaElement[] javaElements, CompilationUnitRewrite rewrite, TextEditGroup group) throws JavaModelException {
		final List removed= new ArrayList();
		for (int i= 0; i < javaElements.length; i++) {
			markAsDeleted(removed, javaElements[i], rewrite, group);
		}
		propagateFieldDeclarationNodeDeletions(removed, rewrite, group);
	}

	private static void markAsDeleted(List list, IJavaElement element, CompilationUnitRewrite rewrite, TextEditGroup group) throws JavaModelException {
		ASTNode[] declarationNodes= getNodesToDelete(element, rewrite.getRoot());
		for (int i= 0; i < declarationNodes.length; i++) {
			ASTNode node= declarationNodes[i];
			if (node != null) {
				list.add(node);
				rewrite.getASTRewrite().remove(node, group);
				rewrite.getImportRemover().registerRemovedNode(node);
			}
		}
	}

	private static void propagateFieldDeclarationNodeDeletions(final List removed, final CompilationUnitRewrite rewrite, final TextEditGroup group) {
		Set removedNodes= getRemovedNodes(removed, rewrite);
		for (Iterator iter= removedNodes.iterator(); iter.hasNext();) {
			ASTNode node= (ASTNode) iter.next();
			if (node instanceof VariableDeclarationFragment) {
				if (node.getParent() instanceof VariableDeclaration) {
					VariableDeclaration fd= (VariableDeclaration) node.getParent();
					if (!removed.contains(fd) && removedNodes.containsAll(fd.fragments()))
						rewrite.getASTRewrite().remove(fd, group);
					rewrite.getImportRemover().registerRemovedNode(fd);
				}
			}
		}
	}

	private ASTNodeDeleteUtil() {
	}
}

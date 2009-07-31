/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Dmitry Stalnov (dstalnov@fusionone.com) - contributed fix for
 *       bug "inline method - doesn't handle implicit cast" (see
 *       https://bugs.eclipse.org/bugs/show_bug.cgi?id=24941).
 *     Dmitry Stalnov (dstalnov@fusionone.com) - contributed fix for
 *       bug Encapsulate field can fail when two variables in one variable declaration (see
 *       https://bugs.eclipse.org/bugs/show_bug.cgi?id=51540).
 *******************************************************************************/
package descent.internal.corext.dom;

import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;

import descent.core.compiler.IProblem;
import descent.core.dom.ASTNode;
import descent.core.dom.GenericVisitor;
import descent.core.dom.Modifier;
import descent.core.dom.Name;
import descent.core.dom.QualifiedName;
import descent.core.dom.QualifiedType;
import descent.core.dom.SimpleName;
import descent.core.dom.SimpleType;
import descent.internal.corext.util.CodeFormatterUtil;
import descent.internal.ui.JavaPlugin;

// TODO JDT UI stub
public class ASTNodes {

	public static final int NODE_ONLY=				0;
	public static final int INCLUDE_FIRST_PARENT= 	1;
	public static final int INCLUDE_ALL_PARENTS= 	2;
	
	public static final int WARNING=				1 << 0;
	public static final int ERROR=					1 << 1;
	public static final int PROBLEMS=				WARNING | ERROR;

	private static final IProblem[] EMPTY_PROBLEMS= new IProblem[0];
	
	private static final int CLEAR_VISIBILITY= ~(Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE);
	
	
	private ASTNodes() {
		// no instance;
	}
	
	public static String asString(ASTNode node) {
		return node.toString();
		/* TODO implement again ASTFlattener ?? 
		ASTFlattener flattener= new ASTFlattener();
		node.accept(flattener);
		return flattener.getResult();
		*/		
	}

	
	/**
	 * Adds flags to the given node and all its descendants.
	 * @param root The root node
	 * @param flags The flags to set
	 */
	public static void setFlagsToAST(ASTNode root, final int flags) {
		root.accept(new GenericVisitor() {
			protected boolean visitNode(ASTNode node) {
				node.setFlags(node.getFlags() | flags);
				return true;
			}
		});
	}
	
	public static int getExclusiveEnd(ASTNode node){
		return node.getStartPosition() + node.getLength();
	}
	
	public static int getInclusiveEnd(ASTNode node){
		return node.getStartPosition() + node.getLength() - 1;
	}
	
	public static ASTNode getParent(ASTNode node, Class parentClass) {
		do {
			node= node.getParent();
		} while (node != null && !parentClass.isInstance(node));
		return node;
	}
	
	public static ASTNode getParent(ASTNode node, Class ... parentClasses) {
		do {
			node= node.getParent();
			for(Class parentClass : parentClasses) {
				if (parentClass.isInstance(node)) {
					break;
				}
			}
		} while (node != null);
		return node;
	}
	
	public static ASTNode getParent(ASTNode node, int nodeType) {
		do {
			node= node.getParent();
		} while (node != null && node.getNodeType() != nodeType);
		return node;
	}
	
	public static boolean isParent(ASTNode node, ASTNode parent) {
		Assert.isNotNull(parent);
		do {
			node= node.getParent();
			if (node == parent)
				return true;
		} while (node != null);
		return false;
	}
	
	public static ASTNode getNormalizedNode(ASTNode node) {
		ASTNode current= node;
		// normalize name
		if (QualifiedName.NAME_PROPERTY.equals(current.getLocationInParent())) {
			current= current.getParent();
		}
		// normalize type
		if (QualifiedType.TYPE_PROPERTY.equals(current.getLocationInParent()) || 
			SimpleType.NAME_PROPERTY.equals(current.getLocationInParent())) {
			current= current.getParent();
		}
		// normalize parameterized types
		/* TODO JDT UI ast nodes
		if (ParameterizedType.TYPE_PROPERTY.equals(current.getLocationInParent())) {
			current= current.getParent();
		}
		*/
		return current;
	}
	
	public static Name getTopMostName(Name name) {
		Name result= name;
		while(result.getParent() instanceof Name) {
			result= (Name)result.getParent();
		}
		return result;
	}
	
	public static String asFormattedString(ASTNode node, int indent, String lineDelim, Map options) {
		String unformatted= asString(node);
		TextEdit edit= CodeFormatterUtil.format2(node, unformatted, indent, lineDelim, options);
		if (edit != null) {
			Document document= new Document(unformatted);
			try {
				edit.apply(document, TextEdit.NONE);
			} catch (BadLocationException e) {
				JavaPlugin.log(e);
			}
			return document.get();
		}
		return unformatted; // unknown node
	}
	
	public static String getSimpleNameIdentifier(Name name) {
		if (name.isQualifiedName()) {
			return ((QualifiedName) name).getName().getIdentifier();
		} else {
			return ((SimpleName) name).getIdentifier();
		}
	}
	
}

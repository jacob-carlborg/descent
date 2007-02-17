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

import descent.core.compiler.IProblem;
import descent.core.dom.ASTNode;
import descent.core.dom.GenericVisitor;
import descent.core.dom.Modifier;

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
	
}

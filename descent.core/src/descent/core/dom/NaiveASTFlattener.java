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
package descent.core.dom;

import java.util.Iterator;
import java.util.List;

/**
 * Internal AST visitor for serializing an AST in a quick and dirty fashion.
 * For various reasons the resulting string is not necessarily legal
 * Java code; and even if it is legal Java code, it is not necessarily the string
 * that corresponds to the given AST. Although useless for most purposes, it's
 * fine for generating debug print strings.
 * <p>
 * Example usage:
 * <code>
 * <pre>
 *    NaiveASTFlattener p = new NaiveASTFlattener();
 *    node.accept(p);
 *    String result = p.getResult();
 * </pre>
 * </code>
 * Call the <code>reset</code> method to clear the previous result before reusing an
 * existing instance.
 * </p>
 * 
 * TODO implement it
 */
class NaiveASTFlattener extends ASTVisitor {
	
	/**
	 * The string buffer into which the serialized representation of the AST is
	 * written.
	 */
	private StringBuffer buffer;
	
	private int indent = 0;
	
	/**
	 * Creates a new AST printer.
	 */
	NaiveASTFlattener() {
		this.buffer = new StringBuffer();
	}
	
	/**
	 * Returns the string accumulated in the visit.
	 *
	 * @return the serialized 
	 */
	public String getResult() {
		return this.buffer.toString();
	}
	
	/**
	 * Resets this printer so that it can be used again.
	 */
	public void reset() {
		this.buffer.setLength(0);
	}
	
	void printIndent() {
		for (int i = 0; i < this.indent; i++) 
			this.buffer.append("  "); //$NON-NLS-1$
	}
	
	/**
	 * Appends the text representation of the given modifier flags, followed by a single space.
	 * 
	 * @param ext the list of modifier and annotation nodes
	 * (element type: <code>IExtendedModifiers</code>)
	 */
	void printModifiers(List ext) {
		for (Iterator it = ext.iterator(); it.hasNext(); ) {
			ASTNode p = (ASTNode) it.next();
			p.accept(this);
			this.buffer.append(" ");//$NON-NLS-1$
		}
	}
	
	void printPreDDocss(ASTNode node, List ext) {
		for (Iterator it = ext.iterator(); it.hasNext(); ) {
			ASTNode p = (ASTNode) it.next();
			if (p.getStartPosition() < node.getStartPosition()) {
				p.accept(this);
				this.buffer.append("\n");//$NON-NLS-1$
			}
		}
	}
	
	void printPostDDocss(ASTNode node, List ext) {
		for (Iterator it = ext.iterator(); it.hasNext(); ) {
			ASTNode p = (ASTNode) it.next();
			if (p.getStartPosition() > node.getStartPosition()) {
				p.accept(this);
				this.buffer.append("\n");//$NON-NLS-1$
			}
		}
	}
	
	@Override
	public boolean visit(AggregateDeclaration node) {
		printPreDDocss(node, node.dDocs());
		printIndent();
		printModifiers(node.modifiers());
		buffer.append(node.getKind().getToken());
		buffer.append(" ");
		printPostDDocss(node, node.dDocs());
		// TODO finish it
		return false;
	}

}

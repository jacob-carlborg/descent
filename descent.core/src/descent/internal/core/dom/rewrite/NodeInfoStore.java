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

import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

import descent.core.dom.AST;
import descent.core.dom.ASTNode;
import descent.core.dom.Block;
import descent.core.dom.Modifier;
import descent.core.dom.TryStatement;
import descent.core.dom.VariableDeclaration;
import descent.internal.core.dom.rewrite.RewriteEventStore.CopySourceInfo;

/**
 *
 */
public final class NodeInfoStore {	
	private AST ast;
	
	private Map placeholderNodes;
	private Set collapsedNodes;

	public NodeInfoStore(AST ast) {
		super();
		this.ast= ast;
		this.placeholderNodes= null;
		this.collapsedNodes= null;
	}

	/**
	 * Marks a node as a placehoder for a plain string content. The type of the node should correspond to the
	 * code's code content.
	 * @param placeholder The placeholder node that acts for the string content.
	 * @param code The string content.
	 */
	public final void markAsStringPlaceholder(ASTNode placeholder, String code) {
		StringPlaceholderData data= new StringPlaceholderData();
		data.code= code;
		setPlaceholderData(placeholder, data);
	}
	
	/**
	 * Marks a node as a copy or move target. The copy target represents a copied node at the target (copied) site.
	 * @param target The node at the target site. Can be a placeholder node but also the source node itself.
	 * @param copySource The info at the source site.
	 */
	public final void markAsCopyTarget(ASTNode target, CopySourceInfo copySource) {
		CopyPlaceholderData data= new CopyPlaceholderData();
		data.copySource= copySource;
		setPlaceholderData(target, data);
	}
	
	/**
	 * Creates a placeholder node of the given type. <code>null</code> if the type is not supported
	 * @param nodeType Type of the node to create. Use the type constants in {@link NodeInfoStore}.
	 * @return Returns a place holder node.
	 */
	// TODO rewrite placeholders finish
	public final ASTNode newPlaceholderNode(int nodeType) {
	    try {
		    ASTNode node= this.ast.createInstance(nodeType);
		    switch (node.getNodeType()) {
				case ASTNode.VARIABLE_DECLARATION:
				    ((VariableDeclaration) node).fragments().add(this.ast.newVariableDeclarationFragment());
				    break;
				case ASTNode.MODIFIER:
				    ((Modifier) node).setModifierKeyword(Modifier.ModifierKeyword.ABSTRACT_KEYWORD);
					break;
				case ASTNode.TRY_STATEMENT :
					((TryStatement) node).setFinally(this.ast.newBlock()); // have to set at least a finally block to be legal code
					break;
			}
		    return node;
	    } catch (IllegalArgumentException e) {
	        return null;
	    }
 	}
	
	
	// collapsed nodes: in source: use one node that represents many; to be used as
	// copy/move source or to replace at once.
	// in the target: one block node that is not flattened.
	
	public Block createCollapsePlaceholder() {
		Block placeHolder= this.ast.newBlock();
		if (this.collapsedNodes == null) {
			this.collapsedNodes= new HashSet();
		}
		this.collapsedNodes.add(placeHolder);
		return placeHolder;
	}
	
	public boolean isCollapsed(ASTNode node) {
		if (this.collapsedNodes != null) {
			return this.collapsedNodes.contains(node);
		}
		return false;	
	}
	
	public Object getPlaceholderData(ASTNode node) {
		if (this.placeholderNodes != null) {
			return this.placeholderNodes.get(node);
		}
		return null;	
	}
	
	private void setPlaceholderData(ASTNode node, PlaceholderData data) {
		if (this.placeholderNodes == null) {
			this.placeholderNodes= new IdentityHashMap();
		}
		this.placeholderNodes.put(node, data);		
	}
	
	static class PlaceholderData {
		// base class
	}
			
	protected static final class CopyPlaceholderData extends PlaceholderData {
		public CopySourceInfo copySource;
		public String toString() {
			return "[placeholder " + this.copySource +"]";  //$NON-NLS-1$//$NON-NLS-2$
		}
	}	
	
	protected static final class StringPlaceholderData extends PlaceholderData {
		public String code;
		public String toString() {
			return "[placeholder string: " + this.code +"]"; //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * 
	 */
	public void clear() {
		this.placeholderNodes= null;
		this.collapsedNodes= null;
	}
}

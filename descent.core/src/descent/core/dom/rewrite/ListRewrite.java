/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package descent.core.dom.rewrite;

import java.util.Collections;
import java.util.List;

import org.eclipse.text.edits.TextEditGroup;

import descent.core.dom.ASTNode;
import descent.core.dom.AliasDeclaration;
import descent.core.dom.Block;
import descent.core.dom.ChildListPropertyDescriptor;
import descent.core.dom.ImportDeclaration;
import descent.core.dom.Statement;
import descent.core.dom.StructuralPropertyDescriptor;
import descent.core.dom.TemplateMixinDeclaration;
import descent.core.dom.TypedefDeclaration;
import descent.core.dom.VariableDeclaration;
import descent.internal.core.dom.rewrite.ListRewriteEvent;
import descent.internal.core.dom.rewrite.NodeInfoStore;
import descent.internal.core.dom.rewrite.RewriteEvent;
import descent.internal.core.dom.rewrite.RewriteEventStore;
import descent.internal.core.dom.rewrite.RewriteEventStore.CopySourceInfo;

/**
 * For describing manipulations to a child list property of an AST node.
 * <p>
 * This class is not intended to be subclassed.
 * </p>
 * @see ASTRewrite#getListRewrite(ASTNode, ChildListPropertyDescriptor)
 * @since 3.0
 */
public final class ListRewrite {
	
	private ASTNode parent;
	private StructuralPropertyDescriptor childProperty;
	private ASTRewrite rewriter;


	/* package*/ ListRewrite(ASTRewrite rewriter, ASTNode parent, StructuralPropertyDescriptor childProperty) {
		this.rewriter= rewriter;
		this.parent= parent;
		this.childProperty= childProperty;
	}
	
	private RewriteEventStore getRewriteStore() {
		return this.rewriter.getRewriteEventStore();
	}
	
	private ListRewriteEvent getEvent() {
		return getRewriteStore().getListEvent(this.parent, this.childProperty, true);
	}
	
	/**
	 * Returns the parent of the list for which this list rewriter was created.

	 * @return the node that contains the list for which this list rewriter was created
	 * @see #getLocationInParent()
	 * @since 3.1
	 */
	public ASTNode getParent() {
		return this.parent;
	}
	
	/**
	 * Returns the property of the parent node for which this list rewriter was created. 
	 * 
	 * @return the property of the parent node for which this list rewriter was created
	 * @see #getParent()
	 * @since 3.1
	 */
	public StructuralPropertyDescriptor getLocationInParent() {
		return this.childProperty;
	}
	
	/**
	 * Removes the given node from its parent's list property in the rewriter.
	 * The node must be contained in the list.
	 * The AST itself is not actually modified in any way; rather, the rewriter
	 * just records a note that this node has been removed from this list.
	 * 
	 * @param node the node being removed
	 * @param editGroup the edit group in which to collect the corresponding
	 * text edits, or <code>null</code> if ungrouped
	 * @throws IllegalArgumentException if the node is null, or if the node is not
	 * part of this rewriter's AST, or if the described modification is invalid
	 * (not a member of this node's original list)
	 */
	public void remove(ASTNode node, TextEditGroup editGroup) {
		if (node == null) {
			throw new IllegalArgumentException();
		}
		RewriteEvent event= getEvent().removeEntry(node);
		if (editGroup != null) {
			getRewriteStore().setEventEditGroup(event, editGroup);
		}
	}
	
	/**
	 * Returns the ASTRewrite instance from which this ListRewriter has been created from.
	 * @return the parent AST Rewriter instance.
	 * @since 3.1
	 */
	public ASTRewrite getASTRewrite() {
		return this.rewriter;
	}
	

	/**
	 * Replaces the given node from its parent's list property in the rewriter.
	 * The node must be contained in the list.
	 * The replacement node must either be brand new (not part of the original AST)
	 * or a placeholder node (for example, one created by
	 * {@link ASTRewrite#createCopyTarget(ASTNode)},
	 * {@link ASTRewrite#createMoveTarget(ASTNode)}, 
	 * or {@link ASTRewrite#createStringPlaceholder(String, int)}). The AST itself
     * is not actually modified in any way; rather, the rewriter just records
     * a note that this node has been replaced in this list.
	 * 
	 * @param node the node being replaced
	 * @param replacement the replacement node, or <code>null</code> if no
	 * replacement
	 * @param editGroup the edit group in which to collect the corresponding
	 * text edits, or <code>null</code> if ungrouped
	 * @throws IllegalArgumentException if the node is null, or if the node is not part
	 * of this rewriter's AST, or if the replacement node is not a new node (or
     * placeholder), or if the described modification is otherwise invalid
     * (not a member of this node's original list)
	 */
	public void replace(ASTNode node, ASTNode replacement, TextEditGroup editGroup) {
		if (node == null) { 
			throw new IllegalArgumentException();
		}
		RewriteEvent event= getEvent().replaceEntry(node, replacement);
		if (editGroup != null) {
			getRewriteStore().setEventEditGroup(event, editGroup);
		}
	}

	/**
	 * Inserts the given node into the list after the given element. 
	 * The existing node must be in the list, either as an original or as a new
	 * node that has been inserted.
	 * The inserted node must either be brand new (not part of the original AST)
	 * or a placeholder node (for example, one created by
	 * {@link ASTRewrite#createCopyTarget(ASTNode)}, 
	 * {@link ASTRewrite#createMoveTarget(ASTNode)}, 
	 * or {@link ASTRewrite#createStringPlaceholder(String, int)}). The AST itself
     * is not actually modified in any way; rather, the rewriter just records
     * a note that this node has been inserted into the list.
	 * 
	 * @param node the node to insert
	 * @param element the element after which the given node is to be inserted
	 * @param editGroup the edit group in which to collect the corresponding
	 * text edits, or <code>null</code> if ungrouped
	 * @throws IllegalArgumentException if the node or element is null, 
	 * or if the node is not part of this rewriter's AST, or if the inserted node
	 * is not a new node (or placeholder), or if <code>element</code> is not a member
	 * of the list (original or new), or if the described modification is
	 * otherwise invalid
	 */
	public void insertAfter(ASTNode node, ASTNode element, TextEditGroup editGroup) {
		if (node == null || element == null) { 
			throw new IllegalArgumentException();
		}
		int index= getEvent().getIndex(element, ListRewriteEvent.BOTH);
		if (index == -1) {
			throw new IllegalArgumentException("Node does not exist"); //$NON-NLS-1$
		}
		internalInsertAt(node, index + 1, true, editGroup);
	}
	
	/**
	 * Inserts the given node into the list before the given element. 
	 * The existing node must be in the list, either as an original or as a new
	 * node that has been inserted.
	 * The inserted node must either be brand new (not part of the original AST)
	 * or a placeholder node (for example, one created by
	 * {@link ASTRewrite#createCopyTarget(ASTNode)}, 
	 * {@link ASTRewrite#createMoveTarget(ASTNode)}, 
	 * or {@link ASTRewrite#createStringPlaceholder(String, int)}). The AST itself
     * is not actually modified in any way; rather, the rewriter just records
     * a note that this node has been inserted into the list.
	 * 
	 * @param node the node to insert
	 * @param element the element before which the given node is to be inserted
	 * @param editGroup the edit group in which to collect the corresponding
	 * text edits, or <code>null</code> if ungrouped
	 * @throws IllegalArgumentException if the node or element is null, 
	 * or if the node is not part of this rewriter's AST, or if the inserted node
	 * is not a new node (or placeholder), or if <code>element</code> is not a member
	 * of the list (original or new), or if the described modification is
	 * otherwise invalid
	 */
	public void insertBefore(ASTNode node, ASTNode element, TextEditGroup editGroup) {
		if (node == null || element == null) { 
			throw new IllegalArgumentException();
		}
		int index= getEvent().getIndex(element, ListRewriteEvent.BOTH);
		if (index == -1) {
			throw new IllegalArgumentException("Node does not exist"); //$NON-NLS-1$
		}
		internalInsertAt(node, index, false, editGroup);
	}
	
	/**
	 * Inserts the given node into the list at the start of the list.
	 * Equivalent to <code>insertAt(node, 0, editGroup)</code>. 
	 * 
	 * @param node the node to insert
	 * @param editGroup the edit group in which to collect the corresponding
	 * text edits, or <code>null</code> if ungrouped
	 * @throws IllegalArgumentException if the node is null, or if the node is not part
	 * of this rewriter's AST, or if the inserted node is not a new node (or
     * placeholder), or if the described modification is otherwise invalid
     * (not a member of this node's original list)
     * @see #insertAt(ASTNode, int, TextEditGroup)
	 */
	public void insertFirst(ASTNode node, TextEditGroup editGroup) {
		if (node == null) { 
			throw new IllegalArgumentException();
		}
		internalInsertAt(node, 0, false, editGroup);
	}
	
	/**
	 * Inserts the given node into the list at the end of the list.
	 * Equivalent to <code>insertAt(node, -1, editGroup)</code>. 
	 * 
	 * @param node the node to insert
	 * @param editGroup the edit group in which to collect the corresponding
	 * text edits, or <code>null</code> if ungrouped
	 * @throws IllegalArgumentException if the node is null, or if the node is not part
	 * of this rewriter's AST, or if the inserted node is not a new node (or
     * placeholder), or if the described modification is otherwise invalid
     * (not a member of this node's original list)
     * @see #insertAt(ASTNode, int, TextEditGroup)
	 */
	public void insertLast(ASTNode node, TextEditGroup editGroup) {
		if (node == null) { 
			throw new IllegalArgumentException();
		}
		internalInsertAt(node, -1, true, editGroup);
	}

	/**
	 * Inserts the given node into the list at the given index. 
	 * The index corresponds to a combined list of original and new nodes;
	 * removed or replaced nodes are still in the combined list.
	 * The inserted node must either be brand new (not part of the original AST)
	 * or a placeholder node (for example, one created by
	 * {@link ASTRewrite#createCopyTarget(ASTNode)}, 
	 * {@link ASTRewrite#createMoveTarget(ASTNode)}, 
	 * or {@link ASTRewrite#createStringPlaceholder(String, int)}). The AST itself
     * is not actually modified in any way; rather, the rewriter just records
     * a note that this node has been inserted into the list.
	 * 
	 * @param node the node to insert
	 * @param index insertion index in the combined list of original and
	 * inserted nodes; <code>-1</code> indicates insertion as the last element
	 * @param editGroup the edit group in which to collect the corresponding
	 * text edits, or <code>null</code> if ungrouped
	 * @throws IllegalArgumentException if the node is null, or if the node is not part
	 * of this rewriter's AST, or if the inserted node is not a new node (or
     * placeholder), or if the described modification is otherwise invalid
     * (not a member of this node's original list)
	 * @throws IndexOutOfBoundsException if the index is negative and not -1, 
	 * or if it is larger than the size of the combined list
	 */
	public void insertAt(ASTNode node, int index, TextEditGroup editGroup) {
		if (node == null) { 
			throw new IllegalArgumentException();
		}
		internalInsertAt(node, index, isInsertBoundToPreviousByDefault(node), editGroup);
	}
	
	private void internalInsertAt(ASTNode node, int index, boolean boundToPrevious, TextEditGroup editGroup) {
		RewriteEvent event= getEvent().insert(node, index);
		if (boundToPrevious) {
			getRewriteStore().setInsertBoundToPrevious(node);
		}
		if (editGroup != null) {
			getRewriteStore().setEventEditGroup(event, editGroup);
		}		
	}
	
	
	private ASTNode createTargetNode(ASTNode first, ASTNode last, boolean isMove, ASTNode replacingNode, TextEditGroup editGroup) {
		if (first == null || last == null) {
			throw new IllegalArgumentException();
		}

		NodeInfoStore nodeStore= this.rewriter.getNodeStore();
		ASTNode placeholder= nodeStore.newPlaceholderNode(first.getNodeType()); // revisit: could use list type
		if (placeholder == null) {
			throw new IllegalArgumentException("Creating a target node is not supported for nodes of type" + first.getClass().getName()); //$NON-NLS-1$
		}
		
		Block internalPlaceHolder= nodeStore.createCollapsePlaceholder();
		CopySourceInfo info= getRewriteStore().createRangeCopy(this.parent, this.childProperty, first, last, isMove, internalPlaceHolder, replacingNode, editGroup);
		nodeStore.markAsCopyTarget(placeholder, info);
		
		return placeholder;		
	}
	
	/**
	 * Creates and returns a placeholder node for a true copy of a range of nodes of the
	 * current list.
	 * The placeholder node can either be inserted as new or used to replace an
	 * existing node. When the document is rewritten, a copy of the source code 
	 * for the given node range is inserted into the output document at the position
	 * corresponding to the placeholder (indentation is adjusted).
	 * 
	 * @param first the node that starts the range
	 * @param last the node that ends the range
	 * @return the new placeholder node
	 * @throws IllegalArgumentException An exception is thrown if the first or last node
	 * are <code>null</code>, if a node is not a child of the current list or if the first node
	 * is not before the last node. An <code>IllegalArgumentException</code> is
	 * also thrown if the copied range is overlapping with an other moved or copied range. 
	 */
	public final ASTNode createCopyTarget(ASTNode first, ASTNode last) {
		if (first == last) {
			return this.rewriter.createCopyTarget(first);
		} else {
			return createTargetNode(first, last, false, null, null);
		}
	}
	
	/**
	 * Creates and returns a placeholder node for a move of a range of nodes of the
	 * current list.
	 * The placeholder node can either be inserted as new or used to replace an
	 * existing node. When the document is rewritten, a copy of the source code 
	 * for the given node range is inserted into the output document at the position
	 * corresponding to the placeholder (indentation is adjusted).
	 * 
	 * @param first the node that starts the range
	 * @param last the node that ends the range
	 * @return the new placeholder node
	 * @throws IllegalArgumentException An exception is thrown if the first or last node
	 * are <code>null</code>, if a node is not a child of the current list or if the first node
	 * is not before the last node. An <code>IllegalArgumentException</code> is
	 * also thrown if the moved range is overlapping with an other moved or copied range. 
	 * 
	 * @since 3.1
	 */
	public final ASTNode createMoveTarget(ASTNode first, ASTNode last) {
		return createMoveTarget(first, last, null, null);
	}
	
	/**
	 * Creates and returns a placeholder node for a move of a range of nodes of the
	 * current list. The moved nodes can optionally be replaced by a specified node.
	 * 
	 * The placeholder node can either be inserted as new or used to replace an
	 * existing node. When the document is rewritten, a copy of the source code 
	 * for the given node range is inserted into the output document at the position
	 * corresponding to the placeholder (indentation is adjusted).
	 * 
	 * @param first the node that starts the range
	 * @param last the node that ends the range
	 * @param replacingNode a node that is set at the location of the moved nodes
	 * or <code>null</code> to remove all nodes
	 * @param editGroup the edit group in which to collect the corresponding
	 * text edits fro a replace, or <code>null</code> if ungrouped
	 * @return the new placeholder node
	 * @throws IllegalArgumentException An exception is thrown if the first or
	 * last node are <code>null</code>, if a node is not a child of the current list or
	 * if the first node is not before the last node. An <code>IllegalArgumentException
	 * </code> is also thrown if the moved range is overlapping with an other moved
	 * or copied range. 
	 * 
	 * @since 3.1
	 */
	public final ASTNode createMoveTarget(ASTNode first, ASTNode last, ASTNode replacingNode, TextEditGroup editGroup) {
		if (first == last) {
			replace(first, replacingNode, editGroup);
			return this.rewriter.createMoveTarget(first);
		} else {
			return createTargetNode(first, last, true, replacingNode, editGroup);
		}
	}
	
	/*
	 * Heuristic to decide if a inserted node is bound to previous or the next sibling. 
	 */
	private boolean isInsertBoundToPreviousByDefault(ASTNode node) {
		/* TODO (Ary) see if the implementation I provided works
		return (node instanceof Statement || node instanceof FieldDeclaration);
		*/
		return node instanceof Statement || node instanceof AliasDeclaration ||
			node instanceof TypedefDeclaration || node instanceof TemplateMixinDeclaration ||
			node instanceof VariableDeclaration || node instanceof ImportDeclaration;
	}
	
	/**
	 * Returns the original nodes in the list property managed by this
	 * rewriter. The returned list is unmodifiable.
	 * 
	 * @return a list of all original nodes in the list
	 */
	public List getOriginalList() {
		List list= (List) getEvent().getOriginalValue();
		return Collections.unmodifiableList(list);
	}
	
	/**
	 * Returns the nodes in the revised list property managed by this
	 * rewriter. The returned list is unmodifiable.
	 * 
	 * @return a list of all nodes in the list taking into account 
	 * all the described changes
	 */
	public List getRewrittenList() {
		List list= (List) getEvent().getNewValue();
		return Collections.unmodifiableList(list);
	}

}

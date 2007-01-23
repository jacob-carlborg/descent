/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package descent.core.dom;

/**
 * A visitor for abstract syntax trees.
 * <p>
 * For each different concrete AST node type <i>T</i> there are
 * a pair of methods:
 * <ul>
 * <li><code>public boolean visit(<i>T</i> node)</code> - Visits
 * the given node to perform some arbitrary operation. If <code>true</code>
 * is returned, the given node's child nodes will be visited next; however,
 * if <code>false</code> is returned, the given node's child nodes will 
 * not be visited. The default implementation provided by this class does
 * nothing and returns <code>true</code>.
 * Subclasses may reimplement this method as needed.</li>
 * <li><code>public void endVisit(<i>T</i> node)</code> - Visits
 * the given node to perform some arbitrary operation. When used in the
 * conventional way, this method is called after all of the given node's
 * children have been visited (or immediately, if <code>visit</code> returned
 * <code>false</code>). The default implementation provided by this class does
 * nothing. Subclasses may reimplement this method as needed.</li>
 * </ul>
 * </p>
 * In addition, there are a pair of methods for visiting AST nodes in the 
 * abstract, regardless of node type:
 * <ul>
 * <li><code>public void preVisit(IElement node)</code> - Visits
 * the given node to perform some arbitrary operation. 
 * This method is invoked prior to the appropriate type-specific
 * <code>visit</code> method.
 * The default implementation of this method does nothing.
 * Subclasses may reimplement this method as needed.</li>
 * <li><code>public void postVisit(IElement node)</code> - Visits
 * the given node to perform some arbitrary operation. 
 * This method is invoked after the appropriate type-specific
 * <code>endVisit</code> method.
 * The default implementation of this method does nothing.
 * Subclasses may reimplement this method as needed.</li>
 * </ul>
 * 
 * @see descent.core.dom.ASTNode#accept(ASTVisitor)
 */
public abstract class ASTVisitor {

/**
 * Indicates whether doc tags should be visited by default.
 */
private boolean visitDocTags;

/**
 * Creates a new AST visitor instance.
 * <p>
 * For backwards compatibility, the visitor does not visit tag
 * elements below doc comments by default. Use 
 * {@link #ASTVisitor(boolean) ASTVisitor(true)}
 * for an visitor that includes doc comments by default.
 * </p>
 */
public ASTVisitor() {
	this(false);
}

/**
 * Creates a new AST visitor instance. 
 * 
 * @param visitDocTags <code>true</code> if doc comment tags are
 * to be visited by default, and <code>false</code> otherwise
 * @see Javadoc#tags()
 * @see #visit(Javadoc)
 */
public ASTVisitor(boolean visitDocTags) {
	this.visitDocTags = visitDocTags;
}
	/**
	 * Visits the given AST node prior to the type-specific visit.
	 * (before <code>visit</code>).
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void preVisit(ASTNode node) {
		// default implementation: do nothing
	}
	/**
	 * Visits the given AST node following the type-specific visit
	 * (after <code>endVisit</code>).
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void postVisit(ASTNode node) {
		// default implementation: do nothing
	}
	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(AggregateDeclaration node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(AggregateDeclaration node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(AliasDeclaration node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(AliasDeclaration node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(AliasDeclarationFragment node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(AliasDeclarationFragment node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(AliasTemplateParameter node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(AliasTemplateParameter node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(AlignDeclaration node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(AlignDeclaration node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(Argument node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(Argument node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(ArrayAccess node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(ArrayAccess node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(ArrayInitializer node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(ArrayInitializer node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(ArrayInitializerFragment node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(ArrayInitializerFragment node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(ArrayLiteral node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(ArrayLiteral node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(AsmStatement node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(AsmStatement node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(AssertExpression node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(AssertExpression node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(Assignment node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(Assignment node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(AssociativeArrayType node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(AssociativeArrayType node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(BaseClass node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(BaseClass node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(Block node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(Block node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(BooleanLiteral node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(BooleanLiteral node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(BreakStatement node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(BreakStatement node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(CallExpression node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(CallExpression node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(CaseStatement node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(CaseStatement node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(CastExpression node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(CastExpression node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(CatchClause node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(CatchClause node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(CharacterLiteral node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(CharacterLiteral node) {

	}

	/**
	 * Visits the given AST node.
	 * <p>
	 * Unlike other node types, the boolean returned by the default
	 * implementation is controlled by a constructor-supplied
	 * parameter  {@link #ASTVisitor(boolean) ASTVisitor(boolean)} 
	 * which is <code>false</code> by default.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 * @see #ASTVisitor()
	 * @see #ASTVisitor(boolean)
	 */
	public boolean visit(Comment node) {
		return this.visitDocTags;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(Comment node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(CompilationUnit node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(CompilationUnit node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(ConditionalExpression node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(ConditionalExpression node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(ContinueStatement node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(ContinueStatement node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(DebugAssignment node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(DebugAssignment node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(DebugDeclaration node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(DebugDeclaration node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(DebugStatement node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(DebugStatement node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(DeclarationStatement node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(DeclarationStatement node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(DefaultStatement node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(DefaultStatement node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(DelegateType node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(DelegateType node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(DeleteExpression node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(DeleteExpression node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(DollarLiteral node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(DollarLiteral node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(DoStatement node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(DoStatement node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(DotIdentifierExpression node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(DotIdentifierExpression node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(DotTemplateTypeExpression node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(DotTemplateTypeExpression node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(DynamicArrayType node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(DynamicArrayType node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(EnumDeclaration node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(EnumDeclaration node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(EnumMember node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(EnumMember node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(ExpressionInitializer node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(ExpressionInitializer node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(ExpressionStatement node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(ExpressionStatement node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(ExternDeclaration node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(ExternDeclaration node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(ForeachStatement node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(ForeachStatement node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(ForStatement node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(ForStatement node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(FunctionDeclaration node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(FunctionDeclaration node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(FunctionLiteralDeclarationExpression node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(FunctionLiteralDeclarationExpression node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(GotoCaseStatement node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(GotoCaseStatement node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(GotoDefaultStatement node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(GotoDefaultStatement node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(GotoStatement node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(GotoStatement node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(IfStatement node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(IfStatement node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(IftypeDeclaration node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(IftypeDeclaration node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(IftypeStatement node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(IftypeStatement node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(Import node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(Import node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(ImportDeclaration node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(ImportDeclaration node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(InfixExpression node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(InfixExpression node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(InvariantDeclaration node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(InvariantDeclaration node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(IsTypeExpression node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(IsTypeExpression node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(IsTypeSpecializationExpression node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(IsTypeSpecializationExpression node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(LabelStatement node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(LabelStatement node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(MixinDeclaration node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(MixinDeclaration node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(Modifier node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(Modifier node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(ModifierDeclaration node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(ModifierDeclaration node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(ModuleDeclaration node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(ModuleDeclaration node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(NewAnonymousClassExpression node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(NewAnonymousClassExpression node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(NewExpression node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(NewExpression node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(NullLiteral node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(NullLiteral node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(NumberLiteral node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(NumberLiteral node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(ParenthesizedExpression node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(ParenthesizedExpression node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(PointerType node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(PointerType node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(PostfixExpression node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(PostfixExpression node) {

	}
	
	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(Pragma node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(Pragma node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(PragmaDeclaration node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(PragmaDeclaration node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(PragmaStatement node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(PragmaStatement node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(PrefixExpression node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(PrefixExpression node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(PrimitiveType node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(PrimitiveType node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(QualifiedName node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(QualifiedName node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(QualifiedType node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(QualifiedType node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(ReturnStatement node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(ReturnStatement node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(ScopeStatement node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(ScopeStatement node) {

	}
	
	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(ScriptLine node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(ScriptLine node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(SelectiveImport node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(SelectiveImport node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(SimpleName node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(SimpleName node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(SimpleType node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(SimpleType node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(SliceExpression node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(SliceExpression node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(SliceType node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(SliceType node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(StaticArrayType node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(StaticArrayType node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(StaticAssert node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(StaticAssert node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(StaticAssertStatement node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(StaticAssertStatement node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(StaticIfDeclaration node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(StaticIfDeclaration node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(StaticIfStatement node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(StaticIfStatement node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(StringLiteral node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(StringLiteral node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(StringsExpression node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(StringsExpression node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(StructInitializer node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(StructInitializer node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(StructInitializerFragment node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(StructInitializerFragment node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(SuperLiteral node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(SuperLiteral node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(SwitchStatement node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(SwitchStatement node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(SynchronizedStatement node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(SynchronizedStatement node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(TemplateDeclaration node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(TemplateDeclaration node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(TemplateType node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(TemplateType node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(ThisLiteral node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(ThisLiteral node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(ThrowStatement node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(ThrowStatement node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(TryStatement node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(TryStatement node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(TupleTemplateParameter node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(TupleTemplateParameter node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(TypedefDeclaration node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(TypedefDeclaration node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(TypedefDeclarationFragment node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(TypedefDeclarationFragment node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(TypeDotIdentifierExpression node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(TypeDotIdentifierExpression node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(TypeExpression node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(TypeExpression node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(TypeidExpression node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(TypeidExpression node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(TypeofType node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(TypeofType node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(TypeTemplateParameter node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(TypeTemplateParameter node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(UnitTestDeclaration node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(UnitTestDeclaration node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(ValueTemplateParameter node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(ValueTemplateParameter node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(VariableDeclaration node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(VariableDeclaration node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(VariableDeclarationFragment node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(VariableDeclarationFragment node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(Version node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(Version node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(VersionAssignment node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(VersionAssignment node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(VersionDeclaration node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(VersionDeclaration node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(VersionStatement node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(VersionStatement node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(VoidInitializer node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(VoidInitializer node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(VolatileStatement node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(VolatileStatement node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(WhileStatement node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(WhileStatement node) {

	}

	/**
	 * Visits the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing and return true.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 * @return <code>true</code> if the children of this node should be
	 * visited, and <code>false</code> if the children of this node should
	 * be skipped
	 */
	public boolean visit(WithStatement node) {
		return true;
	}

	/**
	 * End of visit the given type-specific AST node.
	 * <p>
	 * The default implementation does nothing. Subclasses may reimplement.
	 * </p>
	 * 
	 * @param node the node to visit
	 */
	public void endVisit(WithStatement node) {

	}

}
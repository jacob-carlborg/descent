package descent.internal.core.dom;

import java.util.Iterator;
import java.util.List;

/**
 * Concrete superclass and default implementation of an AST subtree matcher.
 * <p>
 * For example, to compute whether two ASTs subtrees are structurally 
 * isomorphic, use <code>n1.subtreeMatch(new ASTMatcher(), n2)</code> where 
 * <code>n1</code> and <code>n2</code> are the AST root nodes of the subtrees.
 * </p>
 * <p>
 * For each different concrete AST node type <i>T</i> there is a
 * <code>public boolean match(<i>T</i> node, Object other)</code> method
 * that matches the given node against another object (typically another
 * AST node, although this is not essential). The default implementations
 * provided by this class tests whether the other object is a node of the
 * same type with structurally isomorphic child subtrees. For nodes with 
 * list-valued properties, the child nodes within the list are compared in
 * order. For nodes with multiple properties, the child nodes are compared
 * in the order that most closely corresponds to the lexical reading order
 * of the source program. For instance, for a type declaration node, the 
 * child ordering is: name, superclass, superinterfaces, and body 
 * declarations.
 * </p>
 * <p>
 * Subclasses may override (extend or reimplement) some or all of the 
 * <code>match</code> methods in order to define more specialized subtree
 * matchers.
 * </p>
 * 
 * @see descent.dom.ASTNode#subtreeMatch(ASTMatcher, Object)
 */
public class ASTMatcher {
	
	/**
	 * Indicates whether doc tags should be matched.
	 */
	private boolean matchDocTags;
	
	/**
	 * Creates a new AST matcher instance.
	 * <p>
	 * For backwards compatibility, the matcher ignores tag
	 * elements below doc comments by default. Use 
	 * {@link #ASTMatcher(boolean) ASTMatcher(true)}
	 * for a matcher that compares doc tags by default.
	 * </p>
	 */
	public ASTMatcher() {
		this(false);
	}

	/**
	 * Creates a new AST matcher instance.
	 * 
	 * @param matchDocTags <code>true</code> if doc comment tags are
	 * to be compared by default, and <code>false</code> otherwise
	 * @see #match(Javadoc,Object)
	 */
	public ASTMatcher(boolean matchDocTags) {
		this.matchDocTags = matchDocTags;
	}

	/**
	 * Returns whether the given lists of AST nodes match pair wise according
	 * to <code>ASTNode.subtreeMatch</code>.
	 * <p>
	 * Note that this is a convenience method, useful for writing recursive
	 * subtree matchers.
	 * </p>
	 * 
	 * @param list1 the first list of AST nodes
	 *    (element type: <code>ASTNode</code>)
	 * @param list2 the second list of AST nodes
	 *    (element type: <code>ASTNode</code>)
	 * @return <code>true</code> if the lists have the same number of elements
	 *    and match pair-wise according to <code>ASTNode.subtreeMatch</code> 
	 * @see ASTNode#subtreeMatch(ASTMatcher matcher, Object other)
	 */
	public final boolean safeSubtreeListMatch(List list1, List list2) {
		int size1 = list1.size();
		int size2 = list2.size();
		if (size1 != size2) {
			return false;
		}
		for (Iterator it1 = list1.iterator(), it2 = list2.iterator(); it1.hasNext();) {
			ASTNode n1 = (ASTNode) it1.next();
			ASTNode n2 = (ASTNode) it2.next();
			if (!n1.subtreeMatch(this, n2)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns whether the given nodes match according to
	 * <code>AST.subtreeMatch</code>. Returns <code>false</code> if one or
	 * the other of the nodes are <code>null</code>. Returns <code>true</code>
	 * if both nodes are <code>null</code>.
	 * <p>
	 * Note that this is a convenience method, useful for writing recursive
	 * subtree matchers.
	 * </p>
	 * 
	 * @param node1 the first AST node, or <code>null</code>; must be an
	 *    instance of <code>ASTNode</code>
	 * @param node2 the second AST node, or <code>null</code>; must be an
	 *    instance of <code>ASTNode</code>
	 * @return <code>true</code> if the nodes match according
	 *    to <code>AST.subtreeMatch</code> or both are <code>null</code>, and 
	 *    <code>false</code> otherwise
	 * @see ASTNode#subtreeMatch(ASTMatcher, Object)
	 */
	public final boolean safeSubtreeMatch(Object node1, Object node2) {
		if (node1 == null && node2 == null) {
			return true;
		}
		if (node1 == null || node2 == null) {
			return false;
		}
		// N.B. call subtreeMatch even node1==node2!=null
		return ((ASTNode) node1).subtreeMatch(this, node2);
	}

	/**
	 * Returns whether the given objects are equal according to
	 * <code>equals</code>. Returns <code>false</code> if either
	 * node is <code>null</code>.
	 * 
	 * @param o1 the first object, or <code>null</code>
	 * @param o2 the second object, or <code>null</code>
	 * @return <code>true</code> if the nodes are equal according to
	 *    <code>equals</code> or both <code>null</code>, and 
	 *    <code>false</code> otherwise
	 */
	public static boolean safeEquals(Object o1, Object o2) {
		if (o1 == o2) {
			return true;
		}
		if (o1 == null || o2 == null) {
			return false;
		}
		return o1.equals(o2);
	}

	/**
	 * Returns whether the given node and the other object match.
	 * <p>
	 * The default implementation provided by this class tests whether the
	 * other object is a node of the same type with structurally isomorphic
	 * child subtrees. Subclasses may override this method as needed.
	 * </p>
	 * 
	 * @param node the node
	 * @param other the other object, or <code>null</code>
	 * @return <code>true</code> if the subtree matches, or 
	 *   <code>false</code> if they do not match or the other object has a
	 *   different node type or is <code>null</code>
	 */
	public boolean match(InfixExpression node, Object other) {
		if (!(other instanceof InfixExpression)) {
			return false;
		}
		InfixExpression o = (InfixExpression) other;
		return (
			safeSubtreeMatch(node.getLeftOperand(), o.getLeftOperand())
			&& node.getOperator() == o.getOperator()
			&& safeSubtreeMatch(node.getRightOperand(), o.getRightOperand())
			);
	}

	/**
	 * Returns whether the given node and the other object match.
	 * <p>
	 * The default implementation provided by this class tests whether the
	 * other object is a node of the same type with structurally isomorphic
	 * child subtrees. Subclasses may override this method as needed.
	 * </p>
	 * 
	 * @param node the node
	 * @param other the other object, or <code>null</code>
	 * @return <code>true</code> if the subtree matches, or 
	 *   <code>false</code> if they do not match or the other object has a
	 *   different node type or is <code>null</code>
	 */
	public boolean match(WithStatement node, Object other) {
		if (!(other instanceof WithStatement)) {
			return false;
		}
		WithStatement o = (WithStatement) other;
		return (
			safeSubtreeMatch(node.getExpression(), o.getExpression())
			&& safeSubtreeMatch(node.getBody(), o.getBody())
			);
	}

	/**
	 * Returns whether the given node and the other object match.
	 * <p>
	 * The default implementation provided by this class tests whether the
	 * other object is a node of the same type with structurally isomorphic
	 * child subtrees. Subclasses may override this method as needed.
	 * </p>
	 * 
	 * @param node the node
	 * @param other the other object, or <code>null</code>
	 * @return <code>true</code> if the subtree matches, or 
	 *   <code>false</code> if they do not match or the other object has a
	 *   different node type or is <code>null</code>
	 */
	public boolean match(WhileStatement node, Object other) {
		if (!(other instanceof WhileStatement)) {
			return false;
		}
		WhileStatement o = (WhileStatement) other;
		return (
			safeSubtreeMatch(node.getExpression(), o.getExpression())
			&& safeSubtreeMatch(node.getBody(), o.getBody())
			);
	}

	/**
	 * Returns whether the given node and the other object match.
	 * <p>
	 * The default implementation provided by this class tests whether the
	 * other object is a node of the same type with structurally isomorphic
	 * child subtrees. Subclasses may override this method as needed.
	 * </p>
	 * 
	 * @param node the node
	 * @param other the other object, or <code>null</code>
	 * @return <code>true</code> if the subtree matches, or 
	 *   <code>false</code> if they do not match or the other object has a
	 *   different node type or is <code>null</code>
	 */
	public boolean match(VolatileStatement node, Object other) {
		if (!(other instanceof VolatileStatement)) {
			return false;
		}
		VolatileStatement o = (VolatileStatement) other;
		return (
			safeSubtreeMatch(node.getBody(), o.getBody())
			);
	}

	/**
	 * Returns whether the given node and the other object match.
	 * <p>
	 * The default implementation provided by this class tests whether the
	 * other object is a node of the same type with structurally isomorphic
	 * child subtrees. Subclasses may override this method as needed.
	 * </p>
	 * 
	 * @param node the node
	 * @param other the other object, or <code>null</code>
	 * @return <code>true</code> if the subtree matches, or 
	 *   <code>false</code> if they do not match or the other object has a
	 *   different node type or is <code>null</code>
	 */
	public boolean match(SimpleName node, Object other) {
		if (!(other instanceof SimpleName)) {
			return false;
		}
		SimpleName o = (SimpleName) other;
		return node.getIdentifier().equals(o.getIdentifier());
	}

	/**
	 * Returns whether the given node and the other object match.
	 * <p>
	 * The default implementation provided by this class tests whether the
	 * other object is a node of the same type with structurally isomorphic
	 * child subtrees. Subclasses may override this method as needed.
	 * </p>
	 * 
	 * @param node the node
	 * @param other the other object, or <code>null</code>
	 * @return <code>true</code> if the subtree matches, or 
	 *   <code>false</code> if they do not match or the other object has a
	 *   different node type or is <code>null</code>
	 */
	public boolean match(Argument node, Object other) {
		if (!(other instanceof Argument)) {
			return false;
		}
		Argument o = (Argument) other;
		return (
			node.getPassageMode() == o.getPassageMode()
			&& safeSubtreeMatch(node.getType(), o.getType())
			&& safeSubtreeMatch(node.getName(), o.getName())
			&& safeSubtreeMatch(node.getDefaultValue(), o.getDefaultValue())
			);
	}

	/**
	 * Returns whether the given node and the other object match.
	 * <p>
	 * The default implementation provided by this class tests whether the
	 * other object is a node of the same type with structurally isomorphic
	 * child subtrees. Subclasses may override this method as needed.
	 * </p>
	 * 
	 * @param node the node
	 * @param other the other object, or <code>null</code>
	 * @return <code>true</code> if the subtree matches, or 
	 *   <code>false</code> if they do not match or the other object has a
	 *   different node type or is <code>null</code>
	 */
	public boolean match(ArrayAccess node, Object other) {
		if (!(other instanceof ArrayAccess)) {
			return false;
		}
		ArrayAccess o = (ArrayAccess) other;
		return (
			safeSubtreeMatch(node.getArray(), o.getArray())
			&& safeSubtreeListMatch(node.indexes(), o.indexes())
			);
	}

	/**
	 * Returns whether the given node and the other object match.
	 * <p>
	 * The default implementation provided by this class tests whether the
	 * other object is a node of the same type with structurally isomorphic
	 * child subtrees. Subclasses may override this method as needed.
	 * </p>
	 * 
	 * @param node the node
	 * @param other the other object, or <code>null</code>
	 * @return <code>true</code> if the subtree matches, or 
	 *   <code>false</code> if they do not match or the other object has a
	 *   different node type or is <code>null</code>
	 */
	public boolean match(AssertExpression node, Object other) {
		if (!(other instanceof AssertExpression)) {
			return false;
		}
		AssertExpression o = (AssertExpression) other;
		return (
			safeSubtreeMatch(node.getExpression(), o.getExpression())
			&& safeSubtreeMatch(node.getMessage(), o.getMessage())
			);
	}

	/**
	 * Returns whether the given node and the other object match.
	 * <p>
	 * The default implementation provided by this class tests whether the
	 * other object is a node of the same type with structurally isomorphic
	 * child subtrees. Subclasses may override this method as needed.
	 * </p>
	 * 
	 * @param node the node
	 * @param other the other object, or <code>null</code>
	 * @return <code>true</code> if the subtree matches, or 
	 *   <code>false</code> if they do not match or the other object has a
	 *   different node type or is <code>null</code>
	 */
	public boolean match(ContinueStatement node, Object other) {
		if (!(other instanceof ContinueStatement)) {
			return false;
		}
		ContinueStatement o = (ContinueStatement) other;
		return (
			safeSubtreeMatch(node.getLabel(), o.getLabel())
			);
	}

	/**
	 * Returns whether the given node and the other object match.
	 * <p>
	 * The default implementation provided by this class tests whether the
	 * other object is a node of the same type with structurally isomorphic
	 * child subtrees. Subclasses may override this method as needed.
	 * </p>
	 * 
	 * @param node the node
	 * @param other the other object, or <code>null</code>
	 * @return <code>true</code> if the subtree matches, or 
	 *   <code>false</code> if they do not match or the other object has a
	 *   different node type or is <code>null</code>
	 */
	public boolean match(BreakStatement node, Object other) {
		if (!(other instanceof BreakStatement)) {
			return false;
		}
		BreakStatement o = (BreakStatement) other;
		return (
			safeSubtreeMatch(node.getLabel(), o.getLabel())
			);
	}

	/**
	 * Returns whether the given node and the other object match.
	 * <p>
	 * The default implementation provided by this class tests whether the
	 * other object is a node of the same type with structurally isomorphic
	 * child subtrees. Subclasses may override this method as needed.
	 * </p>
	 * 
	 * @param node the node
	 * @param other the other object, or <code>null</code>
	 * @return <code>true</code> if the subtree matches, or 
	 *   <code>false</code> if they do not match or the other object has a
	 *   different node type or is <code>null</code>
	 */
	public boolean match(NullLiteral literal, Object other) {
		return other instanceof NullLiteral;
	}

	/**
	 * Returns whether the given node and the other object match.
	 * <p>
	 * The default implementation provided by this class tests whether the
	 * other object is a node of the same type with structurally isomorphic
	 * child subtrees. Subclasses may override this method as needed.
	 * </p>
	 * 
	 * @param node the node
	 * @param other the other object, or <code>null</code>
	 * @return <code>true</code> if the subtree matches, or 
	 *   <code>false</code> if they do not match or the other object has a
	 *   different node type or is <code>null</code>
	 */
	public boolean match(BooleanLiteral node, Object other) {
		if (!(other instanceof BooleanLiteral)) {
			return false;
		}
		BooleanLiteral o = (BooleanLiteral) other;
		return node.booleanValue() == o.booleanValue();
	}

	/**
	 * Returns whether the given node and the other object match.
	 * <p>
	 * The default implementation provided by this class tests whether the
	 * other object is a node of the same type with structurally isomorphic
	 * child subtrees. Subclasses may override this method as needed.
	 * </p>
	 * 
	 * @param node the node
	 * @param other the other object, or <code>null</code>
	 * @return <code>true</code> if the subtree matches, or 
	 *   <code>false</code> if they do not match or the other object has a
	 *   different node type or is <code>null</code>
	 */
	public boolean match(DollarLiteral node, Object other) {
		return other instanceof DollarLiteral;
	}

	/**
	 * Returns whether the given node and the other object match.
	 * <p>
	 * The default implementation provided by this class tests whether the
	 * other object is a node of the same type with structurally isomorphic
	 * child subtrees. Subclasses may override this method as needed.
	 * </p>
	 * 
	 * @param node the node
	 * @param other the other object, or <code>null</code>
	 * @return <code>true</code> if the subtree matches, or 
	 *   <code>false</code> if they do not match or the other object has a
	 *   different node type or is <code>null</code>
	 */
	public boolean match(SuperLiteral literal, Object other) {
		return other instanceof SuperLiteral;
	}

	/**
	 * Returns whether the given node and the other object match.
	 * <p>
	 * The default implementation provided by this class tests whether the
	 * other object is a node of the same type with structurally isomorphic
	 * child subtrees. Subclasses may override this method as needed.
	 * </p>
	 * 
	 * @param node the node
	 * @param other the other object, or <code>null</code>
	 * @return <code>true</code> if the subtree matches, or 
	 *   <code>false</code> if they do not match or the other object has a
	 *   different node type or is <code>null</code>
	 */
	public boolean match(ThisLiteral literal, Object other) {
		return other instanceof ThisLiteral;
	}

	/**
	 * Returns whether the given node and the other object match.
	 * <p>
	 * The default implementation provided by this class tests whether the
	 * other object is a node of the same type with structurally isomorphic
	 * child subtrees. Subclasses may override this method as needed.
	 * </p>
	 * 
	 * @param node the node
	 * @param other the other object, or <code>null</code>
	 * @return <code>true</code> if the subtree matches, or 
	 *   <code>false</code> if they do not match or the other object has a
	 *   different node type or is <code>null</code>
	 */
	public boolean match(StaticAssert node, Object other) {
		if (!(other instanceof StaticAssert)) {
			return false;
		}
		StaticAssert o = (StaticAssert) other;
		return (
			safeSubtreeMatch(node.getExpression(), o.getExpression())
			&& safeSubtreeMatch(node.getMessage(), o.getMessage())
			);
	}

	/**
	 * Returns whether the given node and the other object match.
	 * <p>
	 * The default implementation provided by this class tests whether the
	 * other object is a node of the same type with structurally isomorphic
	 * child subtrees. Subclasses may override this method as needed.
	 * </p>
	 * 
	 * @param node the node
	 * @param other the other object, or <code>null</code>
	 * @return <code>true</code> if the subtree matches, or 
	 *   <code>false</code> if they do not match or the other object has a
	 *   different node type or is <code>null</code>
	 */
	public boolean match(DeleteExpression node, Object other) {
		if (!(other instanceof DeleteExpression)) {
			return false;
		}
		DeleteExpression o = (DeleteExpression) other;
		return (
			safeSubtreeMatch(node.getExpression(), o.getExpression())
			);
	}

	/**
	 * Returns whether the given node and the other object match.
	 * <p>
	 * The default implementation provided by this class tests whether the
	 * other object is a node of the same type with structurally isomorphic
	 * child subtrees. Subclasses may override this method as needed.
	 * </p>
	 * 
	 * @param node the node
	 * @param other the other object, or <code>null</code>
	 * @return <code>true</code> if the subtree matches, or 
	 *   <code>false</code> if they do not match or the other object has a
	 *   different node type or is <code>null</code>
	 */
	public boolean match(DoStatement node, Object other) {
		if (!(other instanceof DoStatement)) {
			return false;
		}
		DoStatement o = (DoStatement) other;
		return (
			safeSubtreeMatch(node.getExpression(), o.getExpression())
			&& safeSubtreeMatch(node.getBody(), o.getBody())
			);
	}

	/**
	 * Returns whether the given node and the other object match.
	 * <p>
	 * The default implementation provided by this class tests whether the
	 * other object is a node of the same type with structurally isomorphic
	 * child subtrees. Subclasses may override this method as needed.
	 * </p>
	 * 
	 * @param node the node
	 * @param other the other object, or <code>null</code>
	 * @return <code>true</code> if the subtree matches, or 
	 *   <code>false</code> if they do not match or the other object has a
	 *   different node type or is <code>null</code>
	 */
	public boolean match(GotoStatement node, Object other) {
		if (!(other instanceof GotoStatement)) {
			return false;
		}
		GotoStatement o = (GotoStatement) other;
		return (
			safeSubtreeMatch(node.getLabel(), o.getLabel())
			);
	}

	/**
	 * Returns whether the given node and the other object match.
	 * <p>
	 * The default implementation provided by this class tests whether the
	 * other object is a node of the same type with structurally isomorphic
	 * child subtrees. Subclasses may override this method as needed.
	 * </p>
	 * 
	 * @param node the node
	 * @param other the other object, or <code>null</code>
	 * @return <code>true</code> if the subtree matches, or 
	 *   <code>false</code> if they do not match or the other object has a
	 *   different node type or is <code>null</code>
	 */
	public boolean match(CaseStatement node, Object other) {
		if (!(other instanceof CaseStatement)) {
			return false;
		}
		CaseStatement o = (CaseStatement) other;
		return (
			safeSubtreeMatch(node.getExpression(), o.getExpression())
			&& safeSubtreeMatch(node.getBody(), o.getBody())
			);
	}

	/**
	 * Returns whether the given node and the other object match.
	 * <p>
	 * The default implementation provided by this class tests whether the
	 * other object is a node of the same type with structurally isomorphic
	 * child subtrees. Subclasses may override this method as needed.
	 * </p>
	 * 
	 * @param node the node
	 * @param other the other object, or <code>null</code>
	 * @return <code>true</code> if the subtree matches, or 
	 *   <code>false</code> if they do not match or the other object has a
	 *   different node type or is <code>null</code>
	 */
	public boolean match(DefaultStatement node, Object other) {
		if (!(other instanceof DefaultStatement)) {
			return false;
		}
		DefaultStatement o = (DefaultStatement) other;
		return (
			safeSubtreeMatch(node.getBody(), o.getBody())
			);
	}

	/**
	 * Returns whether the given node and the other object match.
	 * <p>
	 * The default implementation provided by this class tests whether the
	 * other object is a node of the same type with structurally isomorphic
	 * child subtrees. Subclasses may override this method as needed.
	 * </p>
	 * 
	 * @param node the node
	 * @param other the other object, or <code>null</code>
	 * @return <code>true</code> if the subtree matches, or 
	 *   <code>false</code> if they do not match or the other object has a
	 *   different node type or is <code>null</code>
	 */
	public boolean match(ExpressionStatement node, Object other) {
		if (!(other instanceof ExpressionStatement)) {
			return false;
		}
		ExpressionStatement o = (ExpressionStatement) other;
		return (
			safeSubtreeMatch(node.getExpression(), o.getExpression())
			);
	}

	/**
	 * Returns whether the given node and the other object match.
	 * <p>
	 * The default implementation provided by this class tests whether the
	 * other object is a node of the same type with structurally isomorphic
	 * child subtrees. Subclasses may override this method as needed.
	 * </p>
	 * 
	 * @param node the node
	 * @param other the other object, or <code>null</code>
	 * @return <code>true</code> if the subtree matches, or 
	 *   <code>false</code> if they do not match or the other object has a
	 *   different node type or is <code>null</code>
	 */
	public boolean match(GotoCaseStatement node, Object other) {
		if (!(other instanceof GotoCaseStatement)) {
			return false;
		}
		GotoCaseStatement o = (GotoCaseStatement) other;
		return (
			safeSubtreeMatch(node.getLabel(), o.getLabel())
			);
	}

	/**
	 * Returns whether the given node and the other object match.
	 * <p>
	 * The default implementation provided by this class tests whether the
	 * other object is a node of the same type with structurally isomorphic
	 * child subtrees. Subclasses may override this method as needed.
	 * </p>
	 * 
	 * @param node the node
	 * @param other the other object, or <code>null</code>
	 * @return <code>true</code> if the subtree matches, or 
	 *   <code>false</code> if they do not match or the other object has a
	 *   different node type or is <code>null</code>
	 */
	public boolean match(Block node, Object other) {
		if (!(other instanceof Block)) {
			return false;
		}
		Block o = (Block) other;
		return (
			safeSubtreeListMatch(node.statements(), o.statements())
			);
	}

	/**
	 * Returns whether the given node and the other object match.
	 * <p>
	 * The default implementation provided by this class tests whether the
	 * other object is a node of the same type with structurally isomorphic
	 * child subtrees. Subclasses may override this method as needed.
	 * </p>
	 * 
	 * @param node the node
	 * @param other the other object, or <code>null</code>
	 * @return <code>true</code> if the subtree matches, or 
	 *   <code>false</code> if they do not match or the other object has a
	 *   different node type or is <code>null</code>
	 */
	public boolean match(DebugStatement node, Object other) {
		if (!(other instanceof DebugStatement)) {
			return false;
		}
		DebugStatement o = (DebugStatement) other;
		return (
			node.getName() == o.getName()
			&& safeSubtreeMatch(node.getBody(), o.getBody())
			&& safeSubtreeMatch(node.getElseBody(), o.getElseBody())
			);
	}

	/**
	 * Returns whether the given node and the other object match.
	 * <p>
	 * The default implementation provided by this class tests whether the
	 * other object is a node of the same type with structurally isomorphic
	 * child subtrees. Subclasses may override this method as needed.
	 * </p>
	 * 
	 * @param node the node
	 * @param other the other object, or <code>null</code>
	 * @return <code>true</code> if the subtree matches, or 
	 *   <code>false</code> if they do not match or the other object has a
	 *   different node type or is <code>null</code>
	 */
	public boolean match(VersionStatement node, Object other) {
		if (!(other instanceof VersionStatement)) {
			return false;
		}
		VersionStatement o = (VersionStatement) other;
		return (
			node.getName() == o.getName()
			&& safeSubtreeMatch(node.getBody(), o.getBody())
			&& safeSubtreeMatch(node.getElseBody(), o.getElseBody())
			);
	}

	/**
	 * Returns whether the given node and the other object match.
	 * <p>
	 * The default implementation provided by this class tests whether the
	 * other object is a node of the same type with structurally isomorphic
	 * child subtrees. Subclasses may override this method as needed.
	 * </p>
	 * 
	 * @param node the node
	 * @param other the other object, or <code>null</code>
	 * @return <code>true</code> if the subtree matches, or 
	 *   <code>false</code> if they do not match or the other object has a
	 *   different node type or is <code>null</code>
	 */
	public boolean match(EnumMember node, Object other) {
		if (!(other instanceof EnumMember)) {
			return false;
		}
		EnumMember o = (EnumMember) other;
		return (
			safeSubtreeMatch(node.getName(), o.getName())
			&& safeSubtreeMatch(node.getValue(), o.getValue())
			);
	}

	/**
	 * Returns whether the given node and the other object match.
	 * <p>
	 * The default implementation provided by this class tests whether the
	 * other object is a node of the same type with structurally isomorphic
	 * child subtrees. Subclasses may override this method as needed.
	 * </p>
	 * 
	 * @param node the node
	 * @param other the other object, or <code>null</code>
	 * @return <code>true</code> if the subtree matches, or 
	 *   <code>false</code> if they do not match or the other object has a
	 *   different node type or is <code>null</code>
	 */
	public boolean match(ForStatement node, Object other) {
		if (!(other instanceof ForStatement)) {
			return false;
		}
		ForStatement o = (ForStatement) other;
		return (
			safeSubtreeMatch(node.getInitializer(), o.getInitializer())
			&& safeSubtreeMatch(node.getCondition(), o.getCondition())
			&& safeSubtreeMatch(node.getIncrement(), o.getIncrement())
			&& safeSubtreeMatch(node.getBody(), o.getBody())
			);
	}

	/**
	 * Returns whether the given node and the other object match.
	 * <p>
	 * The default implementation provided by this class tests whether the
	 * other object is a node of the same type with structurally isomorphic
	 * child subtrees. Subclasses may override this method as needed.
	 * </p>
	 * 
	 * @param node the node
	 * @param other the other object, or <code>null</code>
	 * @return <code>true</code> if the subtree matches, or 
	 *   <code>false</code> if they do not match or the other object has a
	 *   different node type or is <code>null</code>
	 */
	public boolean match(AggregateDeclaration node, Object other) {
		if (!(other instanceof AggregateDeclaration)) {
			return false;
		}
		AggregateDeclaration o = (AggregateDeclaration) other;
		return (
			node.getModifierFlags() == o.getModifierFlags()
			&& node.getKind() == o.getKind()
			&& safeSubtreeMatch(node.getName(), o.getName())
			&& safeSubtreeListMatch(node.templateParameters(), o.templateParameters())
			&& safeSubtreeListMatch(node.baseClasses(), o.baseClasses())
			&& safeSubtreeListMatch(node.declarations(), o.declarations())
			&& safeSubtreeListMatch(node.docComments(), o.docComments())
			);
	}

	/**
	 * Returns whether the given node and the other object match.
	 * <p>
	 * The default implementation provided by this class tests whether the
	 * other object is a node of the same type with structurally isomorphic
	 * child subtrees. Subclasses may override this method as needed.
	 * </p>
	 * 
	 * @param node the node
	 * @param other the other object, or <code>null</code>
	 * @return <code>true</code> if the subtree matches, or 
	 *   <code>false</code> if they do not match or the other object has a
	 *   different node type or is <code>null</code>
	 */
	public boolean match(AliasDeclaration node, Object other) {
		if (!(other instanceof AliasDeclaration)) {
			return false;
		}
		AliasDeclaration o = (AliasDeclaration) other;
		return (
			node.getModifierFlags() == o.getModifierFlags()
			&& safeSubtreeMatch(node.getName(), o.getName())
			&& safeSubtreeMatch(node.getType(), o.getType())
			);
	}

	/**
	 * Returns whether the given node and the other object match.
	 * <p>
	 * The default implementation provided by this class tests whether the
	 * other object is a node of the same type with structurally isomorphic
	 * child subtrees. Subclasses may override this method as needed.
	 * </p>
	 * 
	 * @param node the node
	 * @param other the other object, or <code>null</code>
	 * @return <code>true</code> if the subtree matches, or 
	 *   <code>false</code> if they do not match or the other object has a
	 *   different node type or is <code>null</code>
	 */
	public boolean match(ArrayLiteral node, Object other) {
		if (!(other instanceof ArrayLiteral)) {
			return false;
		}
		ArrayLiteral o = (ArrayLiteral) other;
		return (
			safeSubtreeListMatch(node.arguments(), o.arguments())
			);
	}

	/**
	 * Returns whether the given node and the other object match.
	 * <p>
	 * The default implementation provided by this class tests whether the
	 * other object is a node of the same type with structurally isomorphic
	 * child subtrees. Subclasses may override this method as needed.
	 * </p>
	 * 
	 * @param node the node
	 * @param other the other object, or <code>null</code>
	 * @return <code>true</code> if the subtree matches, or 
	 *   <code>false</code> if they do not match or the other object has a
	 *   different node type or is <code>null</code>
	 */
	public boolean match(BaseClass node, Object other) {
		if (!(other instanceof BaseClass)) {
			return false;
		}
		BaseClass o = (BaseClass) other;
		return (
			node.getModifierFlags() == o.getModifierFlags()
			&& safeSubtreeMatch(node.getType(), o.getType())
			);
	}

	/**
	 * Returns whether the given node and the other object match.
	 * <p>
	 * The default implementation provided by this class tests whether the
	 * other object is a node of the same type with structurally isomorphic
	 * child subtrees. Subclasses may override this method as needed.
	 * </p>
	 * 
	 * @param node the node
	 * @param other the other object, or <code>null</code>
	 * @return <code>true</code> if the subtree matches, or 
	 *   <code>false</code> if they do not match or the other object has a
	 *   different node type or is <code>null</code>
	 */
	public boolean match(AlignDeclaration node, Object other) {
		if (!(other instanceof AlignDeclaration)) {
			return false;
		}
		AlignDeclaration o = (AlignDeclaration) other;
		return (
			node.getModifierFlags() == o.getModifierFlags()
			&& node.getAlign() == o.getAlign()
			&& safeSubtreeListMatch(node.declarations(), o.declarations())
			);
	}

	/**
	 * Returns whether the given node and the other object match.
	 * <p>
	 * The default implementation provided by this class tests whether the
	 * other object is a node of the same type with structurally isomorphic
	 * child subtrees. Subclasses may override this method as needed.
	 * </p>
	 * 
	 * @param node the node
	 * @param other the other object, or <code>null</code>
	 * @return <code>true</code> if the subtree matches, or 
	 *   <code>false</code> if they do not match or the other object has a
	 *   different node type or is <code>null</code>
	 */
	public boolean match(CallExpression node, Object other) {
		if (!(other instanceof CallExpression)) {
			return false;
		}
		CallExpression o = (CallExpression) other;
		return (
			safeSubtreeMatch(node.getExpression(), o.getExpression())
			&& safeSubtreeListMatch(node.arguments(), o.arguments())
			);
	}

	/**
	 * Returns whether the given node and the other object match.
	 * <p>
	 * The default implementation provided by this class tests whether the
	 * other object is a node of the same type with structurally isomorphic
	 * child subtrees. Subclasses may override this method as needed.
	 * </p>
	 * 
	 * @param node the node
	 * @param other the other object, or <code>null</code>
	 * @return <code>true</code> if the subtree matches, or 
	 *   <code>false</code> if they do not match or the other object has a
	 *   different node type or is <code>null</code>
	 */
	public boolean match(CastExpression node, Object other) {
		if (!(other instanceof CastExpression)) {
			return false;
		}
		CastExpression o = (CastExpression) other;
		return (
			safeSubtreeMatch(node.getExpression(), o.getExpression())
			&& safeSubtreeMatch(node.getType(), o.getType())
			);
	}

	/**
	 * Returns whether the given node and the other object match.
	 * <p>
	 * The default implementation provided by this class tests whether the
	 * other object is a node of the same type with structurally isomorphic
	 * child subtrees. Subclasses may override this method as needed.
	 * </p>
	 * 
	 * @param node the node
	 * @param other the other object, or <code>null</code>
	 * @return <code>true</code> if the subtree matches, or 
	 *   <code>false</code> if they do not match or the other object has a
	 *   different node type or is <code>null</code>
	 */
	public boolean match(CatchClause node, Object other) {
		if (!(other instanceof CatchClause)) {
			return false;
		}
		CatchClause o = (CatchClause) other;
		return (
			safeSubtreeMatch(node.getType(), o.getType())
			&& safeSubtreeMatch(node.getName(), o.getName())
			&& safeSubtreeMatch(node.getBody(), o.getBody())
			);
	}

	/**
	 * Returns whether the given node and the other object match.
	 * <p>
	 * The default implementation provided by this class tests whether the
	 * other object is a node of the same type with structurally isomorphic
	 * child subtrees. Subclasses may override this method as needed.
	 * </p>
	 * 
	 * @param node the node
	 * @param other the other object, or <code>null</code>
	 * @return <code>true</code> if the subtree matches, or 
	 *   <code>false</code> if they do not match or the other object has a
	 *   different node type or is <code>null</code>
	 */
	public boolean match(ConditionalExpression node, Object other) {
		if (!(other instanceof ConditionalExpression)) {
			return false;
		}
		ConditionalExpression o = (ConditionalExpression) other;
		return (
			safeSubtreeMatch(node.getExpression(), o.getExpression())
			&& safeSubtreeMatch(node.getThenExpression(), o.getThenExpression())
			&& safeSubtreeMatch(node.getElseExpression(), o.getElseExpression())
			);
	}
	
	/**
	 * Returns whether the given node and the other object match.
	 * <p>
	 * The default implementation provided by this class tests whether the
	 * other object is a node of the same type with structurally isomorphic
	 * child subtrees. Subclasses may override this method as needed.
	 * </p>
	 * 
	 * @param node the node
	 * @param other the other object, or <code>null</code>
	 * @return <code>true</code> if the subtree matches, or 
	 *   <code>false</code> if they do not match or the other object has a
	 *   different node type or is <code>null</code>
	 */
	public boolean match(FunctionDeclaration node, Object other) {
		if (!(other instanceof FunctionDeclaration)) {
			return false;
		}
		FunctionDeclaration o = (FunctionDeclaration) other;
		return (
			node.getModifierFlags() == o.getModifierFlags()
			&& node.getKind() == o.getKind()
			&& safeSubtreeMatch(node.getReturnType(), o.getReturnType())
			&& safeSubtreeMatch(node.getName(), o.getName())
			&& safeSubtreeListMatch(node.templateParameters(), o.templateParameters())
			&& safeSubtreeListMatch(node.arguments(), o.arguments())
			&& node.isVariadic() == o.isVariadic()
			&& safeSubtreeMatch(node.getPrecondition(), o.getPrecondition())
			&& safeSubtreeMatch(node.getPostcondition(), o.getPostcondition())
			&& safeSubtreeMatch(node.getPostconditionVariableName(), o.getPostconditionVariableName())
			&& safeSubtreeMatch(node.getBody(), o.getBody())
			);
	}
	
	/**
	 * Returns whether the given node and the other object match.
	 * <p>
	 * The default implementation provided by this class tests whether the
	 * other object is a node of the same type with structurally isomorphic
	 * child subtrees. Subclasses may override this method as needed.
	 * </p>
	 * 
	 * @param node the node
	 * @param other the other object, or <code>null</code>
	 * @return <code>true</code> if the subtree matches, or 
	 *   <code>false</code> if they do not match or the other object has a
	 *   different node type or is <code>null</code>
	 */
	public boolean match(EnumDeclaration node, Object other) {
		if (!(other instanceof EnumDeclaration)) {
			return false;
		}
		EnumDeclaration o = (EnumDeclaration) other;
		return (
			node.getModifierFlags() == o.getModifierFlags()
			&& safeSubtreeMatch(node.getName(), o.getName())
			&& safeSubtreeMatch(node.getBaseType(), o.getBaseType())
			&& safeSubtreeListMatch(node.enumMembers(), o.enumMembers())
			);
	}
	
	/**
	 * Returns whether the given node and the other object match.
	 * <p>
	 * The default implementation provided by this class tests whether the
	 * other object is a node of the same type with structurally isomorphic
	 * child subtrees. Subclasses may override this method as needed.
	 * </p>
	 * 
	 * @param node the node
	 * @param other the other object, or <code>null</code>
	 * @return <code>true</code> if the subtree matches, or 
	 *   <code>false</code> if they do not match or the other object has a
	 *   different node type or is <code>null</code>
	 */
	public boolean match(ExpressionInitializer node, Object other) {
		if (!(other instanceof ExpressionInitializer)) {
			return false;
		}
		ExpressionInitializer o = (ExpressionInitializer) other;
		return (
			safeSubtreeMatch(node.getExpression(), o.getExpression())
			);
	}


}

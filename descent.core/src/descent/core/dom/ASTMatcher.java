package descent.core.dom;

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
	public boolean match(Version node, Object other) {
		if (!(other instanceof Version)) {
			return false;
		}
		Version o = (Version) other;
		return node.getValue().equals(o.getValue());
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
			safeSubtreeListMatch(node.modifiers(), o.modifiers())
			&& safeSubtreeMatch(node.getExpression(), o.getExpression())
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
			safeSubtreeMatch(node.getVersion(), o.getVersion())
			&& safeSubtreeMatch(node.getThenBody(), o.getThenBody())
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
			safeSubtreeMatch(node.getVersion(), o.getVersion())
			&& safeSubtreeMatch(node.getThenBody(), o.getThenBody())
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
			safeSubtreeListMatch(node.modifiers(), o.modifiers())
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
			safeSubtreeListMatch(node.modifiers(), o.modifiers())
			&& safeSubtreeMatch(node.getType(), o.getType())
			&& safeSubtreeListMatch(node.fragments(), o.fragments())
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
			safeSubtreeMatch(node.getModifier(), o.getModifier())
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
			safeSubtreeListMatch(node.modifiers(), o.modifiers())
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
			safeSubtreeListMatch(node.modifiers(), o.modifiers())
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
			safeSubtreeListMatch(node.modifiers(), o.modifiers())
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
	public boolean match(InvariantDeclaration node, Object other) {
		if (!(other instanceof InvariantDeclaration)) {
			return false;
		}
		InvariantDeclaration o = (InvariantDeclaration) other;
		return (
			safeSubtreeListMatch(node.modifiers(), o.modifiers())
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
	public boolean match(LabelStatement node, Object other) {
		if (!(other instanceof LabelStatement)) {
			return false;
		}
		LabelStatement o = (LabelStatement) other;
		return (
			safeSubtreeMatch(node.getLabel(), o.getLabel())
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
	public boolean match(IfStatement node, Object other) {
		if (!(other instanceof IfStatement)) {
			return false;
		}
		IfStatement o = (IfStatement) other;
		return (
			safeSubtreeMatch(node.getArgument(), o.getArgument())
			&& safeSubtreeMatch(node.getExpression(), o.getExpression())
			&& safeSubtreeMatch(node.getThenBody(), o.getThenBody())
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
	public boolean match(ImportDeclaration node, Object other) {
		if (!(other instanceof ImportDeclaration)) {
			return false;
		}
		ImportDeclaration o = (ImportDeclaration) other;
		return (
			safeSubtreeListMatch(node.modifiers(), o.modifiers())
			&& node.isStatic() == o.isStatic()
			&& safeSubtreeListMatch(node.imports(), o.imports())
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
	public boolean match(VoidInitializer node, Object other) {
		return other instanceof VoidInitializer;
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
	public boolean match(UnitTestDeclaration node, Object other) {
		if (!(other instanceof UnitTestDeclaration)) {
			return false;
		}
		UnitTestDeclaration o = (UnitTestDeclaration) other;
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
	public boolean match(SliceExpression node, Object other) {
		if (!(other instanceof SliceExpression)) {
			return false;
		}
		SliceExpression o = (SliceExpression) other;
		return (
			safeSubtreeMatch(node.getExpression(), o.getExpression())
			&& safeSubtreeMatch(node.getFromExpression(), o.getFromExpression())
			&& safeSubtreeMatch(node.getToExpression(), o.getToExpression())
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
	public boolean match(ParenthesizedExpression node, Object other) {
		if (!(other instanceof ParenthesizedExpression)) {
			return false;
		}
		ParenthesizedExpression o = (ParenthesizedExpression) other;
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
	public boolean match(ReturnStatement node, Object other) {
		if (!(other instanceof ReturnStatement)) {
			return false;
		}
		ReturnStatement o = (ReturnStatement) other;
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
	public boolean match(ThrowStatement node, Object other) {
		if (!(other instanceof ThrowStatement)) {
			return false;
		}
		ThrowStatement o = (ThrowStatement) other;
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
	public boolean match(StaticAssertStatement node, Object other) {
		if (!(other instanceof StaticAssertStatement)) {
			return false;
		}
		StaticAssertStatement o = (StaticAssertStatement) other;
		return (
			safeSubtreeMatch(node.getStaticAssert(), o.getStaticAssert())
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
	public boolean match(GotoDefaultStatement node, Object other) {
		return other instanceof GotoDefaultStatement;
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
	public boolean match(SwitchStatement node, Object other) {
		if (!(other instanceof SwitchStatement)) {
			return false;
		}
		SwitchStatement o = (SwitchStatement) other;
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
	public boolean match(ArrayInitializerFragment node, Object other) {
		if (!(other instanceof ArrayInitializerFragment)) {
			return false;
		}
		ArrayInitializerFragment o = (ArrayInitializerFragment) other;
		return (
			safeSubtreeMatch(node.getExpression(), o.getExpression())
			&& safeSubtreeMatch(node.getInitializer(), o.getInitializer())
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
	public boolean match(ArrayInitializer node, Object other) {
		if (!(other instanceof ArrayInitializer)) {
			return false;
		}
		ArrayInitializer o = (ArrayInitializer) other;
		return (
			safeSubtreeListMatch(node.fragments(), o.fragments())
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
	public boolean match(DebugAssignment node, Object other) {
		if (!(other instanceof DebugAssignment)) {
			return false;
		}
		DebugAssignment o = (DebugAssignment) other;
		return (
			safeSubtreeListMatch(node.modifiers(), o.modifiers())
			&& safeSubtreeMatch(node.getVersion(), o.getVersion())
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
	public boolean match(VersionAssignment node, Object other) {
		if (!(other instanceof VersionAssignment)) {
			return false;
		}
		VersionAssignment o = (VersionAssignment) other;
		return (
			safeSubtreeMatch(node.getVersion(), o.getVersion())
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
	public boolean match(DeclarationStatement node, Object other) {
		if (!(other instanceof DeclarationStatement)) {
			return false;
		}
		DeclarationStatement o = (DeclarationStatement) other;
		return (
			safeSubtreeMatch(node.getDeclaration(), o.getDeclaration())
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
	public boolean match(ForeachStatement node, Object other) {
		if (!(other instanceof ForeachStatement)) {
			return false;
		}
		ForeachStatement o = (ForeachStatement) other;
		return (
			node.isReverse() == o.isReverse()
			&& safeSubtreeListMatch(node.arguments(), o.arguments())
			&& safeSubtreeMatch(node.getExpression(), o.getExpression())
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
	public boolean match(Import node, Object other) {
		if (!(other instanceof Import)) {
			return false;
		}
		Import o = (Import) other;
		return (
			safeSubtreeMatch(node.getAlias(), o.getAlias())
			&& safeSubtreeMatch(node.getName(), o.getName())
			&& safeSubtreeListMatch(node.selectiveImports(), o.selectiveImports())
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
	public boolean match(SelectiveImport node, Object other) {
		if (!(other instanceof SelectiveImport)) {
			return false;
		}
		SelectiveImport o = (SelectiveImport) other;
		return (
			safeSubtreeMatch(node.getAlias(), o.getAlias())
			&& safeSubtreeMatch(node.getName(), o.getName())
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
	public boolean match(ModuleDeclaration node, Object other) {
		if (!(other instanceof ModuleDeclaration)) {
			return false;
		}
		ModuleDeclaration o = (ModuleDeclaration) other;
		return (
			safeSubtreeMatch(node.getName(), o.getName())
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
	public boolean match(ScopeStatement node, Object other) {
		if (!(other instanceof ScopeStatement)) {
			return false;
		}
		ScopeStatement o = (ScopeStatement) other;
		return (
			node.getEvent() == o.getEvent()
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
	public boolean match(PrimitiveType node, Object other) {
		if (!(other instanceof PrimitiveType)) {
			return false;
		}
		PrimitiveType o = (PrimitiveType) other;
		return (
			node.getPrimitiveTypeCode() == o.getPrimitiveTypeCode()
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
	public boolean match(PragmaStatement node, Object other) {
		if (!(other instanceof PragmaStatement)) {
			return false;
		}
		PragmaStatement o = (PragmaStatement) other;
		return (
			safeSubtreeMatch(node.getName(), o.getName())
			&& safeSubtreeListMatch(node.arguments(), o.arguments())
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
	public boolean match(PragmaDeclaration node, Object other) {
		if (!(other instanceof PragmaDeclaration)) {
			return false;
		}
		PragmaDeclaration o = (PragmaDeclaration) other;
		return (
			safeSubtreeListMatch(node.modifiers(), o.modifiers())
			&& safeSubtreeMatch(node.getName(), o.getName())
			&& safeSubtreeListMatch(node.arguments(), o.arguments())
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
	public boolean match(SynchronizedStatement node, Object other) {
		if (!(other instanceof SynchronizedStatement)) {
			return false;
		}
		SynchronizedStatement o = (SynchronizedStatement) other;
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
	public boolean match(DotIdentifierExpression node, Object other) {
		if (!(other instanceof DotIdentifierExpression)) {
			return false;
		}
		DotIdentifierExpression o = (DotIdentifierExpression) other;
		return (
			safeSubtreeMatch(node.getExpression(), o.getExpression())
			&& safeSubtreeMatch(node.getName(), o.getName())
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
	public boolean match(StaticIfStatement node, Object other) {
		if (!(other instanceof StaticIfStatement)) {
			return false;
		}
		StaticIfStatement o = (StaticIfStatement) other;
		return (
			safeSubtreeMatch(node.getExpression(), o.getExpression())
			&& safeSubtreeMatch(node.getThenBody(), o.getThenBody())
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
	public boolean match(StaticIfDeclaration node, Object other) {
		if (!(other instanceof StaticIfDeclaration)) {
			return false;
		}
		StaticIfDeclaration o = (StaticIfDeclaration) other;
		return (
			safeSubtreeListMatch(node.modifiers(), o.modifiers())
			&& safeSubtreeMatch(node.getExpression(), o.getExpression())
			&& safeSubtreeListMatch(node.thenDeclarations(), o.thenDeclarations())
			&& safeSubtreeListMatch(node.elseDeclarations(), o.elseDeclarations())
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
	public boolean match(IftypeDeclaration node, Object other) {
		if (!(other instanceof IftypeDeclaration)) {
			return false;
		}
		IftypeDeclaration o = (IftypeDeclaration) other;
		return (
			safeSubtreeListMatch(node.modifiers(), o.modifiers())
			&& node.getKind() == o.getKind()
			&& safeSubtreeMatch(node.getName(), o.getName())
			&& safeSubtreeMatch(node.getTestType(), o.getTestType())
			&& safeSubtreeMatch(node.getMatchingType(), o.getMatchingType())
			&& safeSubtreeListMatch(node.thenDeclarations(), o.thenDeclarations())
			&& safeSubtreeListMatch(node.elseDeclarations(), o.elseDeclarations())
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
	public boolean match(DebugDeclaration node, Object other) {
		if (!(other instanceof DebugDeclaration)) {
			return false;
		}
		DebugDeclaration o = (DebugDeclaration) other;
		return (
			safeSubtreeListMatch(node.modifiers(), o.modifiers())
			&& safeSubtreeMatch(node.getVersion(), o.getVersion())
			&& safeSubtreeListMatch(node.thenDeclarations(), o.thenDeclarations())
			&& safeSubtreeListMatch(node.elseDeclarations(), o.elseDeclarations())
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
	public boolean match(VersionDeclaration node, Object other) {
		if (!(other instanceof VersionDeclaration)) {
			return false;
		}
		VersionDeclaration o = (VersionDeclaration) other;
		return (
			safeSubtreeMatch(node.getVersion(), o.getVersion())
			&& safeSubtreeListMatch(node.thenDeclarations(), o.thenDeclarations())
			&& safeSubtreeListMatch(node.elseDeclarations(), o.elseDeclarations())
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
	public boolean match(IftypeStatement node, Object other) {
		if (!(other instanceof IftypeStatement)) {
			return false;
		}
		IftypeStatement o = (IftypeStatement) other;
		return (
			node.getKind() == o.getKind()
			&& safeSubtreeMatch(node.getName(), o.getName())
			&& safeSubtreeMatch(node.getTestType(), o.getTestType())
			&& safeSubtreeMatch(node.getMatchingType(), o.getMatchingType())
			&& safeSubtreeMatch(node.getThenBody(), o.getThenBody())
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
	public boolean match(AliasTemplateParameter node, Object other) {
		if (!(other instanceof AliasTemplateParameter)) {
			return false;
		}
		AliasTemplateParameter o = (AliasTemplateParameter) other;
		return (
			safeSubtreeMatch(node.getName(), o.getName())
			&& safeSubtreeMatch(node.getSpecificType(), o.getSpecificType())
			&& safeSubtreeMatch(node.getDefaultType(), o.getDefaultType())
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
	public boolean match(TypeTemplateParameter node, Object other) {
		if (!(other instanceof TypeTemplateParameter)) {
			return false;
		}
		TypeTemplateParameter o = (TypeTemplateParameter) other;
		return (
			safeSubtreeMatch(node.getName(), o.getName())
			&& safeSubtreeMatch(node.getSpecificType(), o.getSpecificType())
			&& safeSubtreeMatch(node.getDefaultType(), o.getDefaultType())
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
	public boolean match(TupleTemplateParameter node, Object other) {
		if (!(other instanceof TupleTemplateParameter)) {
			return false;
		}
		TupleTemplateParameter o = (TupleTemplateParameter) other;
		return (
			safeSubtreeMatch(node.getName(), o.getName())
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
	public boolean match(ValueTemplateParameter node, Object other) {
		if (!(other instanceof ValueTemplateParameter)) {
			return false;
		}
		ValueTemplateParameter o = (ValueTemplateParameter) other;
		return (
			safeSubtreeMatch(node.getType(), o.getType())
			&& safeSubtreeMatch(node.getName(), o.getName())
			&& safeSubtreeMatch(node.getSpecificValue(), o.getSpecificValue())
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
	public boolean match(DynamicArrayType node, Object other) {
		if (!(other instanceof DynamicArrayType)) {
			return false;
		}
		DynamicArrayType o = (DynamicArrayType) other;
		return (
			safeSubtreeMatch(node.getComponentType(), o.getComponentType())
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
	public boolean match(StaticArrayType node, Object other) {
		if (!(other instanceof StaticArrayType)) {
			return false;
		}
		StaticArrayType o = (StaticArrayType) other;
		return (
			safeSubtreeMatch(node.getComponentType(), o.getComponentType())
			&& safeSubtreeMatch(node.getSize(), o.getSize())
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
	public boolean match(AssociativeArrayType node, Object other) {
		if (!(other instanceof AssociativeArrayType)) {
			return false;
		}
		AssociativeArrayType o = (AssociativeArrayType) other;
		return (
			safeSubtreeMatch(node.getComponentType(), o.getComponentType())
			&& safeSubtreeMatch(node.getKeyType(), o.getKeyType())
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
	public boolean match(TypedefDeclaration node, Object other) {
		if (!(other instanceof TypedefDeclaration)) {
			return false;
		}
		TypedefDeclaration o = (TypedefDeclaration) other;
		return (
			safeSubtreeListMatch(node.modifiers(), o.modifiers())
			&& safeSubtreeMatch(node.getType(), o.getType())
			&& safeSubtreeListMatch(node.fragments(), o.fragments())
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
	public boolean match(TypeofType node, Object other) {
		if (!(other instanceof TypeofType)) {
			return false;
		}
		TypeofType o = (TypeofType) other;
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
	public boolean match(TemplateDeclaration node, Object other) {
		if (!(other instanceof TemplateDeclaration)) {
			return false;
		}
		TemplateDeclaration o = (TemplateDeclaration) other;
		return (
			safeSubtreeListMatch(node.modifiers(), o.modifiers())
			&& safeSubtreeMatch(node.getName(), o.getName())
			&& safeSubtreeListMatch(node.templateParameters(), o.templateParameters())
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
	public boolean match(SliceType node, Object other) {
		if (!(other instanceof SliceType)) {
			return false;
		}
		SliceType o = (SliceType) other;
		return (
			safeSubtreeMatch(node.getComponentType(), o.getComponentType())
			&& safeSubtreeMatch(node.getFromExpression(), o.getFromExpression())
			&& safeSubtreeMatch(node.getToExpression(), o.getToExpression())
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
	public boolean match(ModifierDeclaration node, Object other) {
		if (!(other instanceof ModifierDeclaration)) {
			return false;
		}
		ModifierDeclaration o = (ModifierDeclaration) other;
		return (
			safeSubtreeListMatch(node.modifiers(), o.modifiers())
			&& node.getSyntax() == o.getSyntax()
			&& safeSubtreeMatch(node.getModifier(), o.getModifier())
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
	public boolean match(ExternDeclaration node, Object other) {
		if (!(other instanceof ExternDeclaration)) {
			return false;
		}
		ExternDeclaration o = (ExternDeclaration) other;
		return (
			safeSubtreeListMatch(node.modifiers(), o.modifiers())
			&& node.getLinkage() == o.getLinkage()
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
	public boolean match(IsTypeExpression node, Object other) {
		if (!(other instanceof IsTypeExpression)) {
			return false;
		}
		IsTypeExpression o = (IsTypeExpression) other;
		return (
			node.isSameComparison() == o.isSameComparison()
			&& safeSubtreeMatch(node.getName(), o.getName())
			&& safeSubtreeMatch(node.getType(), o.getType())
			&& safeSubtreeMatch(node.getSpecialization(), o.getSpecialization())
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
	public boolean match(IsTypeSpecializationExpression node, Object other) {
		if (!(other instanceof IsTypeSpecializationExpression)) {
			return false;
		}
		IsTypeSpecializationExpression o = (IsTypeSpecializationExpression) other;
		return (
			node.isSameComparison() == o.isSameComparison()
			&& safeSubtreeMatch(node.getName(), o.getName())
			&& safeSubtreeMatch(node.getType(), o.getType())
			&& node.getSpecialization() == o.getSpecialization()
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
	public boolean match(PointerType node, Object other) {
		if (!(other instanceof PointerType)) {
			return false;
		}
		PointerType o = (PointerType) other;
		return (
			safeSubtreeMatch(node.getComponentType(), o.getComponentType())
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
	public boolean match(DelegateType node, Object other) {
		if (!(other instanceof DelegateType)) {
			return false;
		}
		DelegateType o = (DelegateType) other;
		return (
			node.isVariadic() == o.isVariadic()
			&& node.isFunctionPointer() == o.isFunctionPointer()
			&& safeSubtreeMatch(node.getReturnType(), o.getReturnType())
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
	public boolean match(FunctionLiteralDeclarationExpression node, Object other) {
		if (!(other instanceof FunctionLiteralDeclarationExpression)) {
			return false;
		}
		FunctionLiteralDeclarationExpression o = (FunctionLiteralDeclarationExpression) other;
		return (
			node.getSyntax() == o.getSyntax()
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
	public boolean match(TryStatement node, Object other) {
		if (!(other instanceof TryStatement)) {
			return false;
		}
		TryStatement o = (TryStatement) other;
		return (
			safeSubtreeMatch(node.getBody(), o.getBody())
			&& safeSubtreeListMatch(node.catchClauses(), o.catchClauses())
			&& safeSubtreeMatch(node.getFinally(), o.getFinally())
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
	public boolean match(PrefixExpression node, Object other) {
		if (!(other instanceof PrefixExpression)) {
			return false;
		}
		PrefixExpression o = (PrefixExpression) other;
		return (
			node.getOperator() == o.getOperator()
			&& safeSubtreeMatch(node.getExpression(), o.getExpression())
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
	public boolean match(PostfixExpression node, Object other) {
		if (!(other instanceof PostfixExpression)) {
			return false;
		}
		PostfixExpression o = (PostfixExpression) other;
		return (
			node.getOperator() == o.getOperator()
			&& safeSubtreeMatch(node.getExpression(), o.getExpression())
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
	public boolean match(AliasDeclarationFragment node, Object other) {
		if (!(other instanceof AliasDeclarationFragment)) {
			return false;
		}
		AliasDeclarationFragment o = (AliasDeclarationFragment) other;
		return (
			safeSubtreeMatch(node.getName(), o.getName())
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
	public boolean match(Modifier node, Object other) {
		if (!(other instanceof Modifier)) {
			return false;
		}
		Modifier o = (Modifier) other;
		return (
			node.getModifierKeyword() == o.getModifierKeyword()
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
	public boolean match(StructInitializer node, Object other) {
		if (!(other instanceof StructInitializer)) {
			return false;
		}
		StructInitializer o = (StructInitializer) other;
		return (
			safeSubtreeListMatch(node.fragments(), o.fragments())
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
	public boolean match(StructInitializerFragment node, Object other) {
		if (!(other instanceof StructInitializerFragment)) {
			return false;
		}
		StructInitializerFragment o = (StructInitializerFragment) other;
		return (
			safeSubtreeMatch(node.getName(), o.getName())
			&& safeSubtreeMatch(node.getInitializer(), o.getInitializer())
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
	public boolean match(NumberLiteral node, Object other) {
		if (!(other instanceof NumberLiteral)) {
			return false;
		}
		NumberLiteral o = (NumberLiteral) other;
		return safeEquals(node.getToken(), o.getToken());
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
	public boolean match(CharacterLiteral node, Object other) {
		if (!(other instanceof CharacterLiteral)) {
			return false;
		}
		CharacterLiteral o = (CharacterLiteral) other;
		return safeEquals(node.getEscapedValue(), o.getEscapedValue());
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
	public boolean match(StringLiteral node, Object other) {
		if (!(other instanceof StringLiteral)) {
			return false;
		}
		StringLiteral o = (StringLiteral) other;
		return safeEquals(node.getEscapedValue(), o.getEscapedValue());
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
	public boolean match(StringsExpression node, Object other) {
		if (!(other instanceof StringsExpression)) {
			return false;
		}
		StringsExpression o = (StringsExpression) other;
		return (
			safeSubtreeListMatch(node.stringLiterals(), o.stringLiterals())
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
	public boolean match(TypedefDeclarationFragment node, Object other) {
		if (!(other instanceof TypedefDeclarationFragment)) {
			return false;
		}
		TypedefDeclarationFragment o = (TypedefDeclarationFragment) other;
		return (
			safeSubtreeMatch(node.getName(), o.getName())
			&& safeSubtreeMatch(node.getInitializer(), o.getInitializer())
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
	public boolean match(VariableDeclaration node, Object other) {
		if (!(other instanceof VariableDeclaration)) {
			return false;
		}
		VariableDeclaration o = (VariableDeclaration) other;
		return (
			safeSubtreeListMatch(node.modifiers(), o.modifiers())
			&& safeSubtreeMatch(node.getType(), o.getType())
			&& safeSubtreeListMatch(node.fragments(), o.fragments())
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
	public boolean match(VariableDeclarationFragment node, Object other) {
		if (!(other instanceof VariableDeclarationFragment)) {
			return false;
		}
		VariableDeclarationFragment o = (VariableDeclarationFragment) other;
		return (
			safeSubtreeMatch(node.getName(), o.getName())
			&& safeSubtreeMatch(node.getInitializer(), o.getInitializer())
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
	public boolean match(NewAnonymousClassExpression node, Object other) {
		if (!(other instanceof NewAnonymousClassExpression)) {
			return false;
		}
		NewAnonymousClassExpression o = (NewAnonymousClassExpression) other;
		return (
			safeSubtreeMatch(node.getExpression(), o.getExpression())
			&& safeSubtreeListMatch(node.newArguments(), o.newArguments())
			&& safeSubtreeListMatch(node.constructorArguments(), o.constructorArguments())
			&& safeSubtreeListMatch(node.baseClasses(), o.baseClasses())
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
	public boolean match(NewExpression node, Object other) {
		if (!(other instanceof NewExpression)) {
			return false;
		}
		NewExpression o = (NewExpression) other;
		return (
			safeSubtreeMatch(node.getExpression(), o.getExpression())
			&& safeSubtreeMatch(node.getType(), o.getType())
			&& safeSubtreeListMatch(node.newArguments(), o.newArguments())
			&& safeSubtreeListMatch(node.constructorArguments(), o.constructorArguments())
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
	public boolean match(TypeExpression node, Object other) {
		if (!(other instanceof TypeExpression)) {
			return false;
		}
		TypeExpression o = (TypeExpression) other;
		return (
			safeSubtreeMatch(node.getType(), o.getType())
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
	public boolean match(TypeDotIdentifierExpression node, Object other) {
		if (!(other instanceof TypeDotIdentifierExpression)) {
			return false;
		}
		TypeDotIdentifierExpression o = (TypeDotIdentifierExpression) other;
		return (
			safeSubtreeMatch(node.getType(), o.getType())
			&& safeSubtreeMatch(node.getName(), o.getName())
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
	public boolean match(QualifiedType node, Object other) {
		if (!(other instanceof QualifiedType)) {
			return false;
		}
		QualifiedType o = (QualifiedType) other;
		return (
			safeSubtreeMatch(node.getQualifier(), o.getQualifier())
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
	public boolean match(SimpleType node, Object other) {
		if (!(other instanceof SimpleType)) {
			return false;
		}
		SimpleType o = (SimpleType) other;
		return (
			safeSubtreeMatch(node.getName(), o.getName())
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
	public boolean match(TemplateType node, Object other) {
		if (!(other instanceof TemplateType)) {
			return false;
		}
		TemplateType o = (TemplateType) other;
		return (
			safeSubtreeMatch(node.getName(), o.getName())
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
	public boolean match(DotTemplateTypeExpression node, Object other) {
		if (!(other instanceof DotTemplateTypeExpression)) {
			return false;
		}
		DotTemplateTypeExpression o = (DotTemplateTypeExpression) other;
		return (
			safeSubtreeMatch(node.getExpression(), o.getExpression())
			&& safeSubtreeMatch(node.getTemplateType(), o.getTemplateType())
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
	public boolean match(MixinDeclaration node, Object other) {
		if (!(other instanceof MixinDeclaration)) {
			return false;
		}
		MixinDeclaration o = (MixinDeclaration) other;
		return (
			safeSubtreeListMatch(node.modifiers(), o.modifiers())
			&& safeSubtreeMatch(node.getType(), o.getType())
			&& safeSubtreeMatch(node.getName(), o.getName())
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
	public boolean match(TypeidExpression node, Object other) {
		if (!(other instanceof TypeidExpression)) {
			return false;
		}
		TypeidExpression o = (TypeidExpression) other;
		return (
			safeSubtreeMatch(node.getType(), o.getType())
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
	public boolean match(CompilationUnit node, Object other) {
		if (!(other instanceof CompilationUnit)) {
			return false;
		}
		CompilationUnit o = (CompilationUnit) other;
		return (
			safeSubtreeMatch(node.getModuleDeclaration(), o.getModuleDeclaration())
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
	public boolean match(Assignment node, Object other) {
		if (!(other instanceof Assignment)) {
			return false;
		}
		Assignment o = (Assignment) other;
		return (
			safeSubtreeMatch(node.getLeftHandSize(), o.getLeftHandSize())
			&& node.getOperator() == o.getOperator()
			&& safeSubtreeMatch(node.getRightHandSize(), o.getRightHandSize())
			);
	}

}

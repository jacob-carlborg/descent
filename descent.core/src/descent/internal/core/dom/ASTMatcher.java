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
	 * @since 3.0
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
	 * @since 3.0
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

	public boolean match(InfixExpression expression, Object other) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean match(WithStatement statement, Object other) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean match(WhileStatement statement, Object other) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean match(VolatileStatement statement, Object other) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean match(SimpleName name, Object other) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean match(Argument argument, Object other) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean match(ArrayAccess access, Object other) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean match(AssertExpression expression, Object other) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean match(ContinueStatement statement, Object other) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean match(BreakStatement statement, Object other) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean match(NullLiteral literal, Object other) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean match(BooleanLiteral literal, Object other) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean match(DollarLiteral literal, Object other) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean match(SuperLiteral literal, Object other) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean match(ThisLiteral literal, Object other) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean match(StaticAssert assert1, Object other) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean match(DeleteExpression expression, Object other) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean match(DoStatement statement, Object other) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean match(GotoStatement statement, Object other) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean match(CaseStatement statement, Object other) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean match(DefaultStatement statement, Object other) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean match(ExpressionStatement statement, Object other) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean match(GotoCaseStatement statement, Object other) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean match(Block block, Object other) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean match(DebugStatement statement, Object other) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean match(VersionStatement statement, Object other) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean match(EnumMember member, Object other) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean match(ForStatement statement, Object other) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean match(AggregateDeclaration declaration, Object other) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean match(AliasDeclaration declaration, Object other) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean match(ArrayLiteral literal, Object other) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean match(BaseClass class1, Object other) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean match(AlignDeclaration declaration, Object other) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean match(CallExpression expression, Object other) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean match(CastExpression expression, Object other) {
		// TODO Auto-generated method stub
		return false;
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
	 * @since 3.1
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

	public boolean match(ConditionalExpression expression, Object other) {
		// TODO Auto-generated method stub
		return false;
	}

}

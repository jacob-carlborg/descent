package descent.core.dom;

import java.util.ArrayList;
import java.util.List;


/**
 * Switch case range statement AST node type.
 *
 * <pre>
 * SwitchCaseRange:
 *    <b>case</b> Expression <b>:</b> <b>..</b> <b>case</b> Expression <b>:</b> { Statement }
 * </pre>
 */
public class SwitchCaseRange extends Statement {
	
	/**
	 * The "fromExpression" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor FROM_EXPRESSION_PROPERTY =
		new ChildPropertyDescriptor(SwitchCaseRange.class, "fromExpression", Expression.class, MANDATORY, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "toExpression" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor TO_EXPRESSION_PROPERTY =
		new ChildPropertyDescriptor(SwitchCaseRange.class, "toExpression", Expression.class, MANDATORY, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "statements" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor STATEMENTS_PROPERTY =
		new ChildListPropertyDescriptor(SwitchCaseRange.class, "statements", Statement.class, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;

	static {
		List properyList = new ArrayList(2);
		createPropertyList(SwitchCaseRange.class, properyList);
		addProperty(FROM_EXPRESSION_PROPERTY, properyList);
		addProperty(TO_EXPRESSION_PROPERTY, properyList);
		addProperty(STATEMENTS_PROPERTY, properyList);
		PROPERTY_DESCRIPTORS = reapPropertyList(properyList);
	}

	/**
	 * Returns a list of structural property descriptors for this node type.
	 * Clients must not modify the result.
	 * 
	 * @param apiLevel the API level; one of the
	 * <code>AST.JLS*</code> constants

	 * @return a list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor})
	 * @since 3.0
	 */
	public static List propertyDescriptors(int apiLevel) {
		return PROPERTY_DESCRIPTORS;
	}
	
	/**
	 * The from expression.
	 */
	private Expression fromExpression;
	
	/**
	 * The from expression.
	 */
	private Expression toExpression;
	
	/**
	 * The statements
	 * (element type: <code>Statement</code>).
	 * Defaults to an empty list.
	 */
	private ASTNode.NodeList statements =
		new ASTNode.NodeList(STATEMENTS_PROPERTY);

	/**
	 * Creates a new unparented switch case node owned by the given 
	 * AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	SwitchCaseRange(AST ast) {
		super(ast);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final List internalStructuralPropertiesForType(int apiLevel) {
		return propertyDescriptors(apiLevel);
	}
	
	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final ASTNode internalGetSetChildProperty(ChildPropertyDescriptor property, boolean get, ASTNode child) {
		if (property == FROM_EXPRESSION_PROPERTY) {
			if (get) {
				return getFromExpression();
			} else {
				setFromExpression((Expression) child);
				return null;
			}
		}
		if (property == TO_EXPRESSION_PROPERTY) {
			if (get) {
				return getToExpression();
			} else {
				setToExpression((Expression) child);
				return null;
			}
		}
		// allow default implementation to flag the error
		return super.internalGetSetChildProperty(property, get, child);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final List internalGetChildListProperty(ChildListPropertyDescriptor property) {
		if (property == STATEMENTS_PROPERTY) {
			return statements();
		}
		// allow default implementation to flag the error
		return super.internalGetChildListProperty(property);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final int getNodeType0() {
		return SWITCH_CASE;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		SwitchCaseRange result = new SwitchCaseRange(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.setFromExpression((Expression) ASTNode.copySubtree(target, getFromExpression()));
		result.setToExpression((Expression) ASTNode.copySubtree(target, getToExpression()));
		result.statements.addAll(ASTNode.copySubtrees(target, statements()));
		return result;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final boolean subtreeMatch0(ASTMatcher matcher, Object other) {
		// dispatch to correct overloaded match method
		return matcher.match(this, other);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	void accept0(ASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if (visitChildren) {
			// visit children in normal left to right reading order
			acceptChild(visitor, getFromExpression());
			acceptChild(visitor, getToExpression());
			acceptChildren(visitor, this.statements);
		}
		visitor.endVisit(this);
	}
	
	/**
	 * Returns the from expression of this slice expression.
	 * 
	 * @return the from expression
	 */ 
	public Expression getFromExpression() {
		return this.fromExpression;
	}

	/**
	 * Sets the from expression of this slice expression.
	 * 
	 * @param fromExpression the from expression
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setFromExpression(Expression fromExpression) {
		ASTNode oldChild = this.fromExpression;
		preReplaceChild(oldChild, fromExpression, FROM_EXPRESSION_PROPERTY);
		this.fromExpression = fromExpression;
		postReplaceChild(oldChild, fromExpression, FROM_EXPRESSION_PROPERTY);
	}

	/**
	 * Returns the to expression of this slice expression.
	 * 
	 * @return the to expression
	 */ 
	public Expression getToExpression() {
		return this.toExpression;
	}

	/**
	 * Sets the to expression of this slice expression.
	 * 
	 * @param toExpression the to expression
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setToExpression(Expression toExpression) {
		ASTNode oldChild = this.toExpression;
		preReplaceChild(oldChild, toExpression, TO_EXPRESSION_PROPERTY);
		this.toExpression = toExpression;
		postReplaceChild(oldChild, toExpression, TO_EXPRESSION_PROPERTY);
	}

	/**
	 * Returns the live ordered list of statements for this
	 * switch case.
	 * 
	 * @return the live list of switch case
	 *    (element type: <code>Statement</code>)
	 */ 
	public List<Statement> statements() {
		return this.statements;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int memSize() {
		return BASE_NODE_SIZE + 3 * 4;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int treeSize() {
		return
			memSize()
			+ (this.fromExpression == null ? 0 : getFromExpression().treeSize())
			+ (this.toExpression == null ? 0 : getToExpression().treeSize())
			+ (this.statements.listSize())
	;
	}

}

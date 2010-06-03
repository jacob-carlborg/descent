package descent.core.dom;

import java.util.ArrayList;
import java.util.List;

/**
 * Foreach range statement AST node.
 * 
 * <pre>
 * ForeachRangeStatement:
 *    [ <b>foreach</b> | <b>foreach_reverse</b> ] <b>(</b> Argument <b>;</b> Expression <b>..</b> Expression <b>)</b> Statement 
 * </pre>
 */
public class ForeachRangeStatement extends Statement {

	/**
	 * The "reverse" structural property of this node type.
	 */
	public static final SimplePropertyDescriptor REVERSE_PROPERTY =
		new SimplePropertyDescriptor(ForeachRangeStatement.class, "reverse", boolean.class, OPTIONAL); //$NON-NLS-1$

	/**
	 * The "argument" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor ARGUMENT_PROPERTY =
		new ChildPropertyDescriptor(ForeachRangeStatement.class, "argument", Argument.class, MANDATORY, NO_CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "fromExpression" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor FROM_EXPRESSION_PROPERTY =
		new ChildPropertyDescriptor(ForeachRangeStatement.class, "fromExpression", Expression.class, MANDATORY, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "toExpression" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor TO_EXPRESSION_PROPERTY =
		new ChildPropertyDescriptor(ForeachRangeStatement.class, "toExpression", Expression.class, MANDATORY, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "body" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor BODY_PROPERTY =
		new ChildPropertyDescriptor(ForeachRangeStatement.class, "body", Statement.class, MANDATORY, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;

	static {
		List properyList = new ArrayList(5);
		createPropertyList(ForeachRangeStatement.class, properyList);
		addProperty(REVERSE_PROPERTY, properyList);
		addProperty(ARGUMENT_PROPERTY, properyList);
		addProperty(FROM_EXPRESSION_PROPERTY, properyList);
		addProperty(TO_EXPRESSION_PROPERTY, properyList);
		addProperty(BODY_PROPERTY, properyList);
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
	 * The reverse.
	 */
	private boolean reverse;

	/**
	 * The argument.
	 */
	private Argument argument;

	/**
	 * The fromExpression.
	 */
	private Expression fromExpression;

	/**
	 * The toExpression.
	 */
	private Expression toExpression;

	/**
	 * The body.
	 */
	private Statement body;


	/**
	 * Creates a new unparented foreach range statement node owned by the given 
	 * AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	ForeachRangeStatement(AST ast) {
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
	final boolean internalGetSetBooleanProperty(SimplePropertyDescriptor property, boolean get, boolean value) {
		if (property == REVERSE_PROPERTY) {
			if (get) {
				return isReverse();
			} else {
				setReverse(value);
				return false;
			}
		}
		// allow default implementation to flag the error
		return super.internalGetSetBooleanProperty(property, get, value);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final ASTNode internalGetSetChildProperty(ChildPropertyDescriptor property, boolean get, ASTNode child) {
		if (property == ARGUMENT_PROPERTY) {
			if (get) {
				return getArgument();
			} else {
				setArgument((Argument) child);
				return null;
			}
		}
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
		if (property == BODY_PROPERTY) {
			if (get) {
				return getBody();
			} else {
				setBody((Statement) child);
				return null;
			}
		}
		// allow default implementation to flag the error
		return super.internalGetSetChildProperty(property, get, child);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final int getNodeType0() {
		return FOREACH_RANGE_STATEMENT;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		ForeachRangeStatement result = new ForeachRangeStatement(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.setReverse(isReverse());
		result.setArgument((Argument) getArgument().clone(target));
		result.setFromExpression((Expression) getFromExpression().clone(target));
		result.setToExpression((Expression) getToExpression().clone(target));
		result.setBody((Statement) getBody().clone(target));
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
			acceptChild(visitor, getArgument());
			acceptChild(visitor, getFromExpression());
			acceptChild(visitor, getToExpression());
			acceptChild(visitor, getBody());
		}
		visitor.endVisit(this);
	}

	/**
	 * Returns the reverse of this foreach range statement.
	 * 
	 * @return the reverse
	 */ 
	public boolean isReverse() {
		return this.reverse;
	}

	/**
	 * Sets the reverse of this foreach range statement.
	 * 
	 * @param reverse the reverse
	 * @exception IllegalArgumentException if the argument is incorrect
	 */ 
	public void setReverse(boolean reverse) {
		preValueChange(REVERSE_PROPERTY);
		this.reverse = reverse;
		postValueChange(REVERSE_PROPERTY);
	}

	/**
	 * Returns the argument of this foreach range statement.
	 * 
	 * @return the argument
	 */ 
	public Argument getArgument() {
		if (this.argument == null) {
			// lazy init must be thread-safe for readers
			synchronized (this) {
				if (this.argument == null) {
					preLazyInit();
					this.argument = new Argument(this.ast);
					postLazyInit(this.argument, ARGUMENT_PROPERTY);
				}
			}
		}
		return this.argument;
	}

	/**
	 * Sets the argument of this foreach range statement.
	 * 
	 * @param argument the argument
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setArgument(Argument argument) {
		if (argument == null) {
			throw new IllegalArgumentException();
		}
		ASTNode oldChild = this.argument;
		preReplaceChild(oldChild, argument, ARGUMENT_PROPERTY);
		this.argument = argument;
		postReplaceChild(oldChild, argument, ARGUMENT_PROPERTY);
	}

	/**
	 * Returns the from expression of this foreach range statement.
	 * 
	 * @return the from expression
	 */ 
	public Expression getFromExpression() {
		if (this.fromExpression == null) {
			// lazy init must be thread-safe for readers
			synchronized (this) {
				if (this.fromExpression == null) {
					preLazyInit();
					this.fromExpression = new SimpleName(this.ast);
					postLazyInit(this.fromExpression, FROM_EXPRESSION_PROPERTY);
				}
			}
		}
		return this.fromExpression;
	}

	/**
	 * Sets the from expression of this foreach range statement.
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
		if (fromExpression == null) {
			throw new IllegalArgumentException();
		}
		ASTNode oldChild = this.fromExpression;
		preReplaceChild(oldChild, fromExpression, FROM_EXPRESSION_PROPERTY);
		this.fromExpression = fromExpression;
		postReplaceChild(oldChild, fromExpression, FROM_EXPRESSION_PROPERTY);
	}

	/**
	 * Returns the to expression of this foreach range statement.
	 * 
	 * @return the to expression
	 */ 
	public Expression getToExpression() {
		if (this.toExpression == null) {
			// lazy init must be thread-safe for readers
			synchronized (this) {
				if (this.toExpression == null) {
					preLazyInit();
					this.toExpression = new SimpleName(this.ast);
					postLazyInit(this.toExpression, TO_EXPRESSION_PROPERTY);
				}
			}
		}
		return this.toExpression;
	}

	/**
	 * Sets the to expression of this foreach range statement.
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
		if (toExpression == null) {
			throw new IllegalArgumentException();
		}
		ASTNode oldChild = this.toExpression;
		preReplaceChild(oldChild, toExpression, TO_EXPRESSION_PROPERTY);
		this.toExpression = toExpression;
		postReplaceChild(oldChild, toExpression, TO_EXPRESSION_PROPERTY);
	}

	/**
	 * Returns the body of this foreach range statement.
	 * 
	 * @return the body
	 */ 
	public Statement getBody() {
		if (this.body == null) {
			// lazy init must be thread-safe for readers
			synchronized (this) {
				if (this.body == null) {
					preLazyInit();
					this.body = new Block(this.ast);
					postLazyInit(this.body, BODY_PROPERTY);
				}
			}
		}
		return this.body;
	}

	/**
	 * Sets the body of this foreach range statement.
	 * 
	 * @param body the body
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setBody(Statement body) {
		if (body == null) {
			throw new IllegalArgumentException();
		}
		ASTNode oldChild = this.body;
		preReplaceChild(oldChild, body, BODY_PROPERTY);
		this.body = body;
		postReplaceChild(oldChild, body, BODY_PROPERTY);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int memSize() {
		return BASE_NODE_SIZE + 5 * 4;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int treeSize() {
		return
			memSize()
			+ (this.argument == null ? 0 : getArgument().treeSize())
			+ (this.fromExpression == null ? 0 : getFromExpression().treeSize())
			+ (this.toExpression == null ? 0 : getToExpression().treeSize())
			+ (this.body == null ? 0 : getBody().treeSize())
	;
	}

}

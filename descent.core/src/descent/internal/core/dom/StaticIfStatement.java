package descent.internal.core.dom;

import java.util.ArrayList;
import java.util.List;

import descent.core.dom.ASTVisitor;
import descent.core.dom.IStaticIfStatement;

/**
 * Static if statement AST node type.
 *
 * <pre>
 * StaticIfStatement:
 *    <b>static</b> <b>if</b> <b>(</b> Expression <b>)</b> Statement [ <b>else</b> Statement ]
 * </pre>
 */
public class StaticIfStatement extends Statement implements IStaticIfStatement {
	
	/**
	 * The "expression" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor EXPRESSION_PROPERTY =
		new ChildPropertyDescriptor(StaticIfStatement.class, "expression", Expression.class, MANDATORY, NO_CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "thenBody" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor THENBODY_PROPERTY =
		new ChildPropertyDescriptor(StaticIfStatement.class, "thenBody", Statement.class, MANDATORY, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "elseBody" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor ELSEBODY_PROPERTY =
		new ChildPropertyDescriptor(StaticIfStatement.class, "elseBody", Statement.class, OPTIONAL, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;

	static {
		List properyList = new ArrayList(3);
		createPropertyList(StaticIfStatement.class, properyList);
		addProperty(EXPRESSION_PROPERTY, properyList);
		addProperty(THENBODY_PROPERTY, properyList);
		addProperty(ELSEBODY_PROPERTY, properyList);
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
	 * The expression.
	 */
	private Expression expression;

	/**
	 * The thenBody.
	 */
	private Statement thenBody;

	/**
	 * The elseBody.
	 */
	private Statement elseBody;


	/**
	 * Creates a new unparented static if statement node owned by the given 
	 * AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	StaticIfStatement(AST ast) {
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
		if (property == EXPRESSION_PROPERTY) {
			if (get) {
				return getExpression();
			} else {
				setExpression((Expression) child);
				return null;
			}
		}
		if (property == THENBODY_PROPERTY) {
			if (get) {
				return getThenBody();
			} else {
				setThenBody((Statement) child);
				return null;
			}
		}
		if (property == ELSEBODY_PROPERTY) {
			if (get) {
				return getElseBody();
			} else {
				setElseBody((Statement) child);
				return null;
			}
		}
		// allow default implementation to flag the error
		return super.internalGetSetChildProperty(property, get, child);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 * TODO make it package
	 */
	public final int getNodeType0() {
		return STATIC_IF_STATEMENT;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		StaticIfStatement result = new StaticIfStatement(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.setExpression((Expression) getExpression().clone(target));
		result.setThenBody((Statement) getThenBody().clone(target));
	result.setElseBody((Statement) ASTNode.copySubtree(target, getElseBody()));
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
			acceptChild(visitor, getExpression());
			acceptChild(visitor, getThenBody());
			acceptChild(visitor, getElseBody());
		}
		visitor.endVisit(this);
	}

	/**
	 * Returns the expression of this static if statement.
	 * 
	 * @return the expression
	 */ 
	public Expression getExpression() {
		if (this.expression == null) {
			// lazy init must be thread-safe for readers
			synchronized (this) {
				if (this.expression == null) {
					preLazyInit();
					this.expression = new SimpleName(this.ast);
					postLazyInit(this.expression, EXPRESSION_PROPERTY);
				}
			}
		}
		return this.expression;
	}

	/**
	 * Sets the expression of this static if statement.
	 * 
	 * @param expression the expression
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setExpression(Expression expression) {
		if (expression == null) {
			throw new IllegalArgumentException();
		}
		ASTNode oldChild = this.expression;
		preReplaceChild(oldChild, expression, EXPRESSION_PROPERTY);
		this.expression = expression;
		postReplaceChild(oldChild, expression, EXPRESSION_PROPERTY);
	}

	/**
	 * Returns the thenBody of this static if statement.
	 * 
	 * @return the thenBody
	 */ 
	public Statement getThenBody() {
		if (this.thenBody == null) {
			// lazy init must be thread-safe for readers
			synchronized (this) {
				if (this.thenBody == null) {
					preLazyInit();
					this.thenBody = new Block(this.ast);
					postLazyInit(this.thenBody, THENBODY_PROPERTY);
				}
			}
		}
		return this.thenBody;
	}

	/**
	 * Sets the thenBody of this static if statement.
	 * 
	 * @param thenBody the thenBody
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setThenBody(Statement thenBody) {
		if (thenBody == null) {
			throw new IllegalArgumentException();
		}
		ASTNode oldChild = this.thenBody;
		preReplaceChild(oldChild, thenBody, THENBODY_PROPERTY);
		this.thenBody = thenBody;
		postReplaceChild(oldChild, thenBody, THENBODY_PROPERTY);
	}

	/**
	 * Returns the elseBody of this static if statement.
	 * 
	 * @return the elseBody
	 */ 
	public Statement getElseBody() {
		return this.elseBody;
	}

	/**
	 * Sets the elseBody of this static if statement.
	 * 
	 * @param elseBody the elseBody
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setElseBody(Statement elseBody) {
		ASTNode oldChild = this.elseBody;
		preReplaceChild(oldChild, elseBody, ELSEBODY_PROPERTY);
		this.elseBody = elseBody;
		postReplaceChild(oldChild, elseBody, ELSEBODY_PROPERTY);
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
			+ (this.expression == null ? 0 : getExpression().treeSize())
			+ (this.thenBody == null ? 0 : getThenBody().treeSize())
			+ (this.elseBody == null ? 0 : getElseBody().treeSize())
	;
	}

}

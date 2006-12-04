package descent.internal.core.dom;

import java.util.ArrayList;
import java.util.List;

import descent.core.dom.ASTVisitor;
import descent.core.dom.IForStatement;

/**
 * For statement AST node type.
 *
 * <pre>
 * ForStatement:
 *    <b>for</b> <b>(</b>
 * 		[ Statement ]<b>;</b>
 * 		[ Expression ] <b>;</b>
 * 		[ Expression ] <b>)</b>
 * 			Statement
 * </pre>
 */
public class ForStatement extends Statement implements IForStatement {
	
	/**
	 * The "initializer" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor INITIALIZER_PROPERTY =
		new ChildPropertyDescriptor(ForStatement.class, "initializer", Statement.class, OPTIONAL, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "condition" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor CONDITION_PROPERTY =
		new ChildPropertyDescriptor(ForStatement.class, "condition", Expression.class, OPTIONAL, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "increment" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor INCREMENT_PROPERTY =
		new ChildPropertyDescriptor(ForStatement.class, "increment", Expression.class, OPTIONAL, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "body" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor BODY_PROPERTY =
		new ChildPropertyDescriptor(ForStatement.class, "body", Statement.class, MANDATORY, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;

	static {
		List properyList = new ArrayList(4);
		createPropertyList(ForStatement.class, properyList);
		addProperty(INITIALIZER_PROPERTY, properyList);
		addProperty(CONDITION_PROPERTY, properyList);
		addProperty(INCREMENT_PROPERTY, properyList);
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
	 * The initializer.
	 */
	private Statement initializer;

	/**
	 * The condition.
	 */
	private Expression condition;

	/**
	 * The increment.
	 */
	private Expression increment;

	/**
	 * The body.
	 */
	private Statement body;


	/**
	 * Creates a new unparented for statement node owned by the given 
	 * AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	ForStatement(AST ast) {
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
		if (property == INITIALIZER_PROPERTY) {
			if (get) {
				return getInitializer();
			} else {
				setInitializer((Statement) child);
				return null;
			}
		}
		if (property == CONDITION_PROPERTY) {
			if (get) {
				return getCondition();
			} else {
				setCondition((Expression) child);
				return null;
			}
		}
		if (property == INCREMENT_PROPERTY) {
			if (get) {
				return getIncrement();
			} else {
				setIncrement((Expression) child);
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
	 * TODO make it package
	 */
	public final int getNodeType0() {
		return FOR_STATEMENT;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		ForStatement result = new ForStatement(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
	result.setInitializer((Statement) ASTNode.copySubtree(target, getInitializer()));
	result.setCondition((Expression) ASTNode.copySubtree(target, getCondition()));
	result.setIncrement((Expression) ASTNode.copySubtree(target, getIncrement()));
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
			acceptChild(visitor, getInitializer());
			acceptChild(visitor, getCondition());
			acceptChild(visitor, getIncrement());
			acceptChild(visitor, getBody());
		}
		visitor.endVisit(this);
	}

	/**
	 * Returns the initializer of this for statement.
	 * 
	 * @return the initializer
	 */ 
	public Statement getInitializer() {
		return this.initializer;
	}

	/**
	 * Sets the initializer of this for statement.
	 * 
	 * @param initializer the initializer
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setInitializer(Statement initializer) {
		if (initializer == null) {
			throw new IllegalArgumentException();
		}
		ASTNode oldChild = this.initializer;
		preReplaceChild(oldChild, initializer, INITIALIZER_PROPERTY);
		this.initializer = initializer;
		postReplaceChild(oldChild, initializer, INITIALIZER_PROPERTY);
	}

	/**
	 * Returns the condition of this for statement.
	 * 
	 * @return the condition
	 */ 
	public Expression getCondition() {
		return this.condition;
	}

	/**
	 * Sets the condition of this for statement.
	 * 
	 * @param condition the condition
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setCondition(Expression condition) {
		if (condition == null) {
			throw new IllegalArgumentException();
		}
		ASTNode oldChild = this.condition;
		preReplaceChild(oldChild, condition, CONDITION_PROPERTY);
		this.condition = condition;
		postReplaceChild(oldChild, condition, CONDITION_PROPERTY);
	}

	/**
	 * Returns the increment of this for statement.
	 * 
	 * @return the increment
	 */ 
	public Expression getIncrement() {
		return this.increment;
	}

	/**
	 * Sets the increment of this for statement.
	 * 
	 * @param increment the increment
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setIncrement(Expression increment) {
		if (increment == null) {
			throw new IllegalArgumentException();
		}
		ASTNode oldChild = this.increment;
		preReplaceChild(oldChild, increment, INCREMENT_PROPERTY);
		this.increment = increment;
		postReplaceChild(oldChild, increment, INCREMENT_PROPERTY);
	}

	/**
	 * Returns the body of this for statement.
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
	 * Sets the body of this for statement.
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
		return BASE_NODE_SIZE + 4 * 4;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int treeSize() {
		return
			memSize()
			+ (this.initializer == null ? 0 : getInitializer().treeSize())
			+ (this.condition == null ? 0 : getCondition().treeSize())
			+ (this.increment == null ? 0 : getIncrement().treeSize())
			+ (this.body == null ? 0 : getBody().treeSize())
	;
	}

	public ForStatement(Statement init, Expression condition, Expression increment, Statement body) {
		this.initializer = init;
		this.condition = condition;
		this.increment = increment;
		this.body = body;
	}

}

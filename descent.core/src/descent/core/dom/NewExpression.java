package descent.core.dom;

import java.util.ArrayList;
import java.util.List;


/**
 * New expression AST node.
 * 
 * <pre>
 * NewExpression:
 *    [ Expression <b>.</b> ] 
 *       <b>new</b> [ <b>(</b> Expression { <b>,</b> Expression } <b>)</b> ] 
 *       Type [ <b>(</b> Expression { <b>,</b> Expression } <b>)</b> ]
 * </pre>
 */
public class NewExpression extends Expression {
	
	/**
	 * The "expression" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor EXPRESSION_PROPERTY =
		new ChildPropertyDescriptor(NewExpression.class, "expression", Expression.class, OPTIONAL, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "type" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor TYPE_PROPERTY =
		new ChildPropertyDescriptor(NewExpression.class, "type", Type.class, MANDATORY, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "newArguments" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor NEW_ARGUMENTS_PROPERTY =
		new ChildListPropertyDescriptor(NewExpression.class, "newArguments", Expression.class, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "constructorArguments" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor CONSTRUCTOR_ARGUMENTS_PROPERTY =
		new ChildListPropertyDescriptor(NewExpression.class, "constructorArguments", Expression.class, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;

	static {
		List properyList = new ArrayList(4);
		createPropertyList(NewExpression.class, properyList);
		addProperty(EXPRESSION_PROPERTY, properyList);
		addProperty(TYPE_PROPERTY, properyList);
		addProperty(NEW_ARGUMENTS_PROPERTY, properyList);
		addProperty(CONSTRUCTOR_ARGUMENTS_PROPERTY, properyList);
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
	 * The type.
	 */
	private Type type;

	/**
	 * The new arguments
	 * (element type: <code>Expression</code>).
	 * Defaults to an empty list.
	 */
	private ASTNode.NodeList newArguments =
		new ASTNode.NodeList(NEW_ARGUMENTS_PROPERTY);
	/**
	 * The constructor arguments
	 * (element type: <code>Expression</code>).
	 * Defaults to an empty list.
	 */
	private ASTNode.NodeList constructorArguments =
		new ASTNode.NodeList(CONSTRUCTOR_ARGUMENTS_PROPERTY);

	/**
	 * Creates a new unparented new expression node owned by the given 
	 * AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	NewExpression(AST ast) {
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
		if (property == TYPE_PROPERTY) {
			if (get) {
				return getType();
			} else {
				setType((Type) child);
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
		if (property == NEW_ARGUMENTS_PROPERTY) {
			return newArguments();
		}
		if (property == CONSTRUCTOR_ARGUMENTS_PROPERTY) {
			return constructorArguments();
		}
		// allow default implementation to flag the error
		return super.internalGetChildListProperty(property);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final int getNodeType0() {
		return NEW_EXPRESSION;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		NewExpression result = new NewExpression(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
	result.setExpression((Expression) ASTNode.copySubtree(target, getExpression()));
		result.setType((Type) getType().clone(target));
		result.newArguments.addAll(ASTNode.copySubtrees(target, newArguments()));
		result.constructorArguments.addAll(ASTNode.copySubtrees(target, constructorArguments()));
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
			acceptChild(visitor, getType());
			acceptChildren(visitor, newArguments());
			acceptChildren(visitor, constructorArguments());
		}
		visitor.endVisit(this);
	}

	/**
	 * Returns the expression of this new expression.
	 * 
	 * @return the expression
	 */ 
	public Expression getExpression() {
		return this.expression;
	}

	/**
	 * Sets the expression of this new expression.
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
		ASTNode oldChild = this.expression;
		preReplaceChild(oldChild, expression, EXPRESSION_PROPERTY);
		this.expression = expression;
		postReplaceChild(oldChild, expression, EXPRESSION_PROPERTY);
	}

	/**
	 * Returns the type of this new expression.
	 * 
	 * @return the type
	 */ 
	public Type getType() {
		if (this.type == null) {
			// lazy init must be thread-safe for readers
			synchronized (this) {
				if (this.type == null) {
					preLazyInit();
					this.type = new PrimitiveType(this.ast);
					postLazyInit(this.type, TYPE_PROPERTY);
				}
			}
		}
		return this.type;
	}

	/**
	 * Sets the type of this new expression.
	 * 
	 * @param type the type
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setType(Type type) {
		if (type == null) {
			throw new IllegalArgumentException();
		}
		ASTNode oldChild = this.type;
		preReplaceChild(oldChild, type, TYPE_PROPERTY);
		this.type = type;
		postReplaceChild(oldChild, type, TYPE_PROPERTY);
	}

	/**
	 * Returns the live ordered list of new arguments for this
	 * new expression.
	 * 
	 * @return the live list of new expression
	 *    (element type: <code>Expression</code>)
	 */ 
	public List<Expression> newArguments() {
		return this.newArguments;
	}

	/**
	 * Returns the live ordered list of constructor arguments for this
	 * new expression.
	 * 
	 * @return the live list of new expression
	 *    (element type: <code>Expression</code>)
	 */ 
	public List<Expression> constructorArguments() {
		return this.constructorArguments;
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
			+ (this.expression == null ? 0 : getExpression().treeSize())
			+ (this.type == null ? 0 : getType().treeSize())
			+ (this.newArguments.listSize())
			+ (this.constructorArguments.listSize())
	;
	}

}

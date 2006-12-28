package descent.core.dom;

import java.util.ArrayList;
import java.util.List;


/**
 * New anonymous class expression AST node.
 * 
 * <pre>
 * NewAnonymousClassExpression:
 *    [ Expression <b>.</b> ] 
 *       <b>new</b> [ <b>(</b> Expression { <b>,</b> Expression } <b>)</b>
 *       <b>class</b> [ <b>(</b> Expression { <b>,</b> Expression } <b>)</b> { BaseClass } 
 *       <b>{</b>
 *          { Declaration }
 *       <b>}</b>
 * </pre>
 */
public class NewAnonymousClassExpression extends Expression {

	/**
	 * The "expression" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor EXPRESSION_PROPERTY =
		new ChildPropertyDescriptor(NewAnonymousClassExpression.class, "expression", Expression.class, OPTIONAL, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "newArguments" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor NEW_ARGUMENTS_PROPERTY =
		new ChildListPropertyDescriptor(NewAnonymousClassExpression.class, "newArguments", Expression.class, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "constructorArguments" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor CONSTRUCTOR_ARGUMENTS_PROPERTY =
		new ChildListPropertyDescriptor(NewAnonymousClassExpression.class, "constructorArguments", Expression.class, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "baseClasses" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor BASE_CLASSES_PROPERTY =
		new ChildListPropertyDescriptor(NewAnonymousClassExpression.class, "baseClasses", BaseClass.class, NO_CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "declarations" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor DECLARATIONS_PROPERTY =
		new ChildListPropertyDescriptor(NewAnonymousClassExpression.class, "declarations", Declaration.class, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;

	static {
		List properyList = new ArrayList(5);
		createPropertyList(NewAnonymousClassExpression.class, properyList);
		addProperty(EXPRESSION_PROPERTY, properyList);
		addProperty(NEW_ARGUMENTS_PROPERTY, properyList);
		addProperty(CONSTRUCTOR_ARGUMENTS_PROPERTY, properyList);
		addProperty(BASE_CLASSES_PROPERTY, properyList);
		addProperty(DECLARATIONS_PROPERTY, properyList);
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
	 * The base classes
	 * (element type: <code>BaseClass</code>).
	 * Defaults to an empty list.
	 */
	private ASTNode.NodeList baseClasses =
		new ASTNode.NodeList(BASE_CLASSES_PROPERTY);
	/**
	 * The declarations
	 * (element type: <code>Declaration</code>).
	 * Defaults to an empty list.
	 */
	private ASTNode.NodeList declarations =
		new ASTNode.NodeList(DECLARATIONS_PROPERTY);

	/**
	 * Creates a new unparented new anonymous class expression node owned by the given 
	 * AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	NewAnonymousClassExpression(AST ast) {
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
		if (property == BASE_CLASSES_PROPERTY) {
			return baseClasses();
		}
		if (property == DECLARATIONS_PROPERTY) {
			return declarations();
		}
		// allow default implementation to flag the error
		return super.internalGetChildListProperty(property);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final int getNodeType0() {
		return NEW_ANONYMOUS_CLASS_EXPRESSION;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		NewAnonymousClassExpression result = new NewAnonymousClassExpression(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
	result.setExpression((Expression) ASTNode.copySubtree(target, getExpression()));
		result.newArguments.addAll(ASTNode.copySubtrees(target, newArguments()));
		result.constructorArguments.addAll(ASTNode.copySubtrees(target, constructorArguments()));
		result.baseClasses.addAll(ASTNode.copySubtrees(target, baseClasses()));
		result.declarations.addAll(ASTNode.copySubtrees(target, declarations()));
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
			acceptChildren(visitor, newArguments);
			acceptChildren(visitor, constructorArguments);
			acceptChildren(visitor, baseClasses);
			acceptChildren(visitor, declarations);
		}
		visitor.endVisit(this);
	}

	/**
	 * Returns the expression of this new anonymous class expression.
	 * 
	 * @return the expression
	 */ 
	public Expression getExpression() {
		return this.expression;
	}

	/**
	 * Sets the expression of this new anonymous class expression.
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
	 * Returns the live ordered list of new arguments for this
	 * new anonymous class expression.
	 * 
	 * @return the live list of new anonymous class expression
	 *    (element type: <code>Expression</code>)
	 */ 
	public List<Expression> newArguments() {
		return this.newArguments;
	}

	/**
	 * Returns the live ordered list of constructor arguments for this
	 * new anonymous class expression.
	 * 
	 * @return the live list of new anonymous class expression
	 *    (element type: <code>Expression</code>)
	 */ 
	public List<Expression> constructorArguments() {
		return this.constructorArguments;
	}

	/**
	 * Returns the live ordered list of base classes for this
	 * new anonymous class expression.
	 * 
	 * @return the live list of new anonymous class expression
	 *    (element type: <code>BaseClass</code>)
	 */ 
	public List<BaseClass> baseClasses() {
		return this.baseClasses;
	}

	/**
	 * Returns the live ordered list of declarations for this
	 * new anonymous class expression.
	 * 
	 * @return the live list of new anonymous class expression
	 *    (element type: <code>Declaration</code>)
	 */ 
	public List<Declaration> declarations() {
		return this.declarations;
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
			+ (this.expression == null ? 0 : getExpression().treeSize())
			+ (this.newArguments.listSize())
			+ (this.constructorArguments.listSize())
			+ (this.baseClasses.listSize())
			+ (this.declarations.listSize())
	;
	}

}

package descent.internal.core.dom;

import java.util.ArrayList;
import java.util.List;

import descent.core.dom.ASTVisitor;
import descent.core.dom.IStaticIfDeclaration;

/**
 * Static if declaration AST node type.
 *
 * <pre>
 * StaticIfDeclaration:
 *    <b>static</b> <b>if</b> <b>(</b> Expression <b>)</b> { Declaration } [ <b>else</b> { Declaration } ]
 * </pre>
 */
public class StaticIfDeclaration extends Declaration implements IStaticIfDeclaration {
	
	/**
	 * The "expression" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor EXPRESSION_PROPERTY =
		new ChildPropertyDescriptor(StaticIfDeclaration.class, "expression", Expression.class, MANDATORY, NO_CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "thenDeclarations" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor THENDECLARATIONS_PROPERTY =
		new ChildListPropertyDescriptor(StaticIfDeclaration.class, "thenDeclarations", Declaration.class, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "elseDeclarations" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor ELSEDECLARATIONS_PROPERTY =
		new ChildListPropertyDescriptor(StaticIfDeclaration.class, "elseDeclarations", Declaration.class, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;

	static {
		List properyList = new ArrayList(3);
		createPropertyList(StaticIfDeclaration.class, properyList);
		addProperty(EXPRESSION_PROPERTY, properyList);
		addProperty(THENDECLARATIONS_PROPERTY, properyList);
		addProperty(ELSEDECLARATIONS_PROPERTY, properyList);
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
	 * The thenDeclarations
	 * (element type: <code>Declaration</code>).
	 * Defaults to an empty list.
	 */
	private ASTNode.NodeList thenDeclarations =
		new ASTNode.NodeList(THENDECLARATIONS_PROPERTY);
	/**
	 * The elseDeclarations
	 * (element type: <code>Declaration</code>).
	 * Defaults to an empty list.
	 */
	private ASTNode.NodeList elseDeclarations =
		new ASTNode.NodeList(ELSEDECLARATIONS_PROPERTY);

	/**
	 * Creates a new unparented static if declaration node owned by the given 
	 * AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	StaticIfDeclaration(AST ast) {
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
		if (property == THENDECLARATIONS_PROPERTY) {
			return thenDeclarations();
		}
		if (property == ELSEDECLARATIONS_PROPERTY) {
			return elseDeclarations();
		}
		// allow default implementation to flag the error
		return super.internalGetChildListProperty(property);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 * TODO make it package
	 */
	public final int getNodeType0() {
		return STATIC_IF_DECLARATION;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		StaticIfDeclaration result = new StaticIfDeclaration(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.setExpression((Expression) getExpression().clone(target));
		result.thenDeclarations.addAll(ASTNode.copySubtrees(target, thenDeclarations()));
		result.elseDeclarations.addAll(ASTNode.copySubtrees(target, elseDeclarations()));
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
			acceptChildren(visitor, thenDeclarations());
			acceptChildren(visitor, elseDeclarations());
		}
		visitor.endVisit(this);
	}

	/**
	 * Returns the expression of this static if declaration.
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
	 * Sets the expression of this static if declaration.
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
	 * Returns the live ordered list of thenDeclarations for this
	 * static if declaration.
	 * 
	 * @return the live list of static if declaration
	 *    (element type: <code>Declaration</code>)
	 */ 
	public List<Declaration> thenDeclarations() {
		return this.thenDeclarations;
	}

	/**
	 * Returns the live ordered list of elseDeclarations for this
	 * static if declaration.
	 * 
	 * @return the live list of static if declaration
	 *    (element type: <code>Declaration</code>)
	 */ 
	public List<Declaration> elseDeclarations() {
		return this.elseDeclarations;
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
			+ (this.thenDeclarations.listSize())
			+ (this.elseDeclarations.listSize())
	;
	}

}

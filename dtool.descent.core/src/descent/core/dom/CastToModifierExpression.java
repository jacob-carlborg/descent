package descent.core.dom;

import java.util.ArrayList;
import java.util.List;

/**
 * Cast to modifier expression AST node.
 * 
 * <pre>
 * CastToModifierExpression:
 *    CastExpression:
 *    <b>cast</b> <b>(</b> Modifier <b>)</b> Expression
 * </pre>
 */
public class CastToModifierExpression extends Expression {

	/**
	 * The "modifier" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor MODIFIER_PROPERTY =
		new ChildPropertyDescriptor(CastToModifierExpression.class, "modifier", Modifier.class, MANDATORY, NO_CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "expression" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor EXPRESSION_PROPERTY =
		new ChildPropertyDescriptor(CastToModifierExpression.class, "expression", Expression.class, MANDATORY, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;

	static {
		List properyList = new ArrayList(2);
		createPropertyList(CastToModifierExpression.class, properyList);
		addProperty(MODIFIER_PROPERTY, properyList);
		addProperty(EXPRESSION_PROPERTY, properyList);
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
	 * The modifier.
	 */
	private Modifier modifier;

	/**
	 * The expression.
	 */
	private Expression expression;


	/**
	 * Creates a new unparented cast to modifier expression node owned by the given 
	 * AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	CastToModifierExpression(AST ast) {
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
		if (property == MODIFIER_PROPERTY) {
			if (get) {
				return getModifier();
			} else {
				setModifier((Modifier) child);
				return null;
			}
		}
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
	final int getNodeType0() {
		return CAST_TO_MODIFIER_EXPRESSION;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		CastToModifierExpression result = new CastToModifierExpression(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.setModifier((Modifier) getModifier().clone(target));
		result.setExpression((Expression) getExpression().clone(target));
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
			acceptChild(visitor, getModifier());
			acceptChild(visitor, getExpression());
		}
		visitor.endVisit(this);
	}

	/**
	 * Returns the modifier of this cast to modifier expression.
	 * 
	 * @return the modifier
	 */ 
	public Modifier getModifier() {
		if (this.modifier == null) {
			// lazy init must be thread-safe for readers
			synchronized (this) {
				if (this.modifier == null) {
					preLazyInit();
					this.modifier = new Modifier(this.ast);
					postLazyInit(this.modifier, MODIFIER_PROPERTY);
				}
			}
		}
		return this.modifier;
	}

	/**
	 * Sets the modifier of this cast to modifier expression.
	 * 
	 * @param modifier the modifier
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setModifier(Modifier modifier) {
		if (modifier == null) {
			throw new IllegalArgumentException();
		}
		ASTNode oldChild = this.modifier;
		preReplaceChild(oldChild, modifier, MODIFIER_PROPERTY);
		this.modifier = modifier;
		postReplaceChild(oldChild, modifier, MODIFIER_PROPERTY);
	}

	/**
	 * Returns the expression of this cast to modifier expression.
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
	 * Sets the expression of this cast to modifier expression.
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

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int memSize() {
		return BASE_NODE_SIZE + 2 * 4;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int treeSize() {
		return
			memSize()
			+ (this.modifier == null ? 0 : getModifier().treeSize())
			+ (this.expression == null ? 0 : getExpression().treeSize())
	;
	}

}

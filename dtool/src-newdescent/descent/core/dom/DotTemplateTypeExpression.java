package descent.core.dom;

import java.util.ArrayList;
import java.util.List;


/**
 * Dot template type expression AST node.
 * 
 * <pre>
 * DotTemplateTypeExpression:
 *    [ Expression ] <b>.</b> TemplateType
 * </pre>
 */
public class DotTemplateTypeExpression extends Expression {
	
	/**
	 * The "expression" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor EXPRESSION_PROPERTY =
		new ChildPropertyDescriptor(DotTemplateTypeExpression.class, "expression", Expression.class, OPTIONAL, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "templateType" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor TEMPLATE_TYPE_PROPERTY =
		new ChildPropertyDescriptor(DotTemplateTypeExpression.class, "templateType", TemplateType.class, MANDATORY, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;

	static {
		List properyList = new ArrayList(2);
		createPropertyList(DotTemplateTypeExpression.class, properyList);
		addProperty(EXPRESSION_PROPERTY, properyList);
		addProperty(TEMPLATE_TYPE_PROPERTY, properyList);
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
	 * The templateType.
	 */
	private TemplateType templateType;


	/**
	 * Creates a new unparented dot template type expression node owned by the given 
	 * AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	DotTemplateTypeExpression(AST ast) {
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
		if (property == TEMPLATE_TYPE_PROPERTY) {
			if (get) {
				return getTemplateType();
			} else {
				setTemplateType((TemplateType) child);
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
		return DOT_TEMPLATE_TYPE_EXPRESSION;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		DotTemplateTypeExpression result = new DotTemplateTypeExpression(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
	result.setExpression((Expression) ASTNode.copySubtree(target, getExpression()));
		result.setTemplateType((TemplateType) getTemplateType().clone(target));
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
			acceptChild(visitor, getTemplateType());
		}
		visitor.endVisit(this);
	}

	/**
	 * Returns the expression of this dot template type expression.
	 * 
	 * @return the expression
	 */ 
	public Expression getExpression() {
		return this.expression;
	}

	/**
	 * Sets the expression of this dot template type expression.
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
	 * Returns the template type of this dot template type expression.
	 * 
	 * @return the template type
	 */ 
	public TemplateType getTemplateType() {
		if (this.templateType == null) {
			// lazy init must be thread-safe for readers
			synchronized (this) {
				if (this.templateType == null) {
					preLazyInit();
					this.templateType = new TemplateType(this.ast);
					postLazyInit(this.templateType, TEMPLATE_TYPE_PROPERTY);
				}
			}
		}
		return this.templateType;
	}

	/**
	 * Sets the template type of this dot template type expression.
	 * 
	 * @param templateType the template type
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setTemplateType(TemplateType templateType) {
		if (templateType == null) {
			throw new IllegalArgumentException();
		}
		ASTNode oldChild = this.templateType;
		preReplaceChild(oldChild, templateType, TEMPLATE_TYPE_PROPERTY);
		this.templateType = templateType;
		postReplaceChild(oldChild, templateType, TEMPLATE_TYPE_PROPERTY);
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
			+ (this.expression == null ? 0 : getExpression().treeSize())
			+ (this.templateType == null ? 0 : getTemplateType().treeSize())
	;
	}

}

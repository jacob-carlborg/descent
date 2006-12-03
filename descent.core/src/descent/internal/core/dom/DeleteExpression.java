package descent.internal.core.dom;

import java.util.ArrayList;
import java.util.List;

import descent.core.dom.ASTVisitor;
import descent.core.dom.IDeleteExpression;

/**
 * Delete expression AST node type.
 *
 * <pre>
 * DeleteExpression:
 *    <b>delete</b> Expression
 * </pre>
 */
public class DeleteExpression extends Expression implements IDeleteExpression {
	
	/**
	 * The "expression" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor EXPRESSION_PROPERTY = 
		new ChildPropertyDescriptor(DeleteExpression.class, "expression", Expression.class, MANDATORY, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;
	
	static {
		List propertyList = new ArrayList(3);
		createPropertyList(DeleteExpression.class, propertyList);
		addProperty(EXPRESSION_PROPERTY, propertyList);
		PROPERTY_DESCRIPTORS = reapPropertyList(propertyList);
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
	 * The expression; lazily initialized; defaults to an unspecified, but 
	 * legal, expression.
	 */
	private Expression expression = null;
	
	/**
	 * Creates a new unparented delete expression node owned by the given 
	 * AST. By default, the expresssion is unspecified, but legal.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	DeleteExpression(AST ast) {
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
	 * TODO make it package
	 */
	public final int getNodeType0() {
		return DELETE_EXPRESSION;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		WithStatement result = new WithStatement(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
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
			acceptChild(visitor, getExpression());
		}
		visitor.endVisit(this);
	}
	
	/**
	 * Returns the expression of this with statement.
	 * 
	 * @return the expression node
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
	 * Sets the expression of this delete expression.
	 * 
	 * @param expression the expression node
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

	// TODO Descent remove
	public DeleteExpression(Expression e) {
		this.expression = e;
	}

}

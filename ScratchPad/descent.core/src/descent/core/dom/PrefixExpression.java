package descent.core.dom;

import java.util.ArrayList;
import java.util.List;


/**
 * Prefix expression AST node type.
 * <pre>
 * PrefixExpression:
 *    PrefixOperator Expression 
 * </pre>
 */
public class PrefixExpression extends Expression {
	
	/**
	 * Prefix operators.
	 * <pre>
	 * PrefixOperator:<code>
	 *    <b>&</b>	 ADDRESS
	 *    <b>++</b>  INCREMENT
	 *    <b>--</b>  DECREMENT
	 *    <b>*</b>   POINTER
	 *    <b>-</b>   NEGATIVE
	 *    <b>+</b>   POSITIVE
	 *    <b>!</b>   NOT
	 *    <b>~</b>   INVERT
	 * </pre>
	 */
	public static enum Operator {
		
		/** Address "&" operator. */
		ADDRESS("&"),
		/** Pre increment "++" operator. */
		INCREMENT("++"),
		/** Pre decrement "++" operator. */
		DECREMENT("--"),
		/** Pointer "*" operator. */
		POINTER("*"),
		/** Negative "-" operator. */
		NEGATIVE("-"),
		/** Positive "+" operator. */
		POSITIVE("+"),
		/** Not "!" operator. */
		NOT("!"),
		/** Invert "~" operator. */
		INVERT("~"),
		;
		
		/**
		 * The token for the operator.
		 */
		private String token;
		
		/**
		 * Creates a new unary operator with the given token.
		 * <p>
		 * Note: this constructor is private. The only instances
		 * ever created are the ones for the standard operators.
		 * </p>
		 * 
		 * @param token the character sequence for the operator
		 */
		private Operator(String token) {
			this.token = token;
		}
		
		/**
		 * Returns the character sequence for the operator.
		 * 
		 * @return the character sequence for the operator
		 */
		public String toString() {
			return token;
		}
		
	}
	
	/**
	 * The "operator" structural property of this node type.
	 */
	public static final SimplePropertyDescriptor OPERATOR_PROPERTY =
		new SimplePropertyDescriptor(PrefixExpression.class, "operator", Operator.class, OPTIONAL); //$NON-NLS-1$

	/**
	 * The "operand" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor OPERAND_PROPERTY =
		new ChildPropertyDescriptor(PrefixExpression.class, "operand", Expression.class, MANDATORY, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;

	static {
		List properyList = new ArrayList(2);
		createPropertyList(PrefixExpression.class, properyList);
		addProperty(OPERATOR_PROPERTY, properyList);
		addProperty(OPERAND_PROPERTY, properyList);
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
	 * The operator.
	 */
	private Operator operator;

	/**
	 * The operand.
	 */
	private Expression operand;


	/**
	 * Creates a new unparented prefix expression node owned by the given 
	 * AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	PrefixExpression(AST ast) {
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
	final Object internalGetSetObjectProperty(SimplePropertyDescriptor property, boolean get, Object value) {
		if (property == OPERATOR_PROPERTY) {
			if (get) {
				return getOperator();
			} else {
				setOperator((Operator) value);
				return null;
			}
		}
		// allow default implementation to flag the error
		return super.internalGetSetObjectProperty(property, get, value);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final ASTNode internalGetSetChildProperty(ChildPropertyDescriptor property, boolean get, ASTNode child) {
		if (property == OPERAND_PROPERTY) {
			if (get) {
				return getOperand();
			} else {
				setOperand((Expression) child);
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
		return PREFIX_EXPRESSION;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		PrefixExpression result = new PrefixExpression(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.setOperator(getOperator());
		result.setOperand((Expression) getOperand().clone(target));
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
			acceptChild(visitor, getOperand());
		}
		visitor.endVisit(this);
	}

	/**
	 * Returns the operator of this prefix expression.
	 * 
	 * @return the operator
	 */ 
	public Operator getOperator() {
		return this.operator;
	}

	/**
	 * Sets the operator of this prefix expression.
	 * 
	 * @param operator the operator
	 * @exception IllegalArgumentException if the argument is incorrect
	 */ 
	public void setOperator(Operator operator) {
		if (operator == null) {
			throw new IllegalArgumentException();
		}
		preValueChange(OPERATOR_PROPERTY);
		this.operator = operator;
		postValueChange(OPERATOR_PROPERTY);
	}

	/**
	 * Returns the operand of this prefix expression.
	 * 
	 * @return the operand
	 */ 
	public Expression getOperand() {
		if (this.operand == null) {
			// lazy init must be thread-safe for readers
			synchronized (this) {
				if (this.operand == null) {
					preLazyInit();
					this.operand = new SimpleName(this.ast);
					postLazyInit(this.operand, OPERAND_PROPERTY);
				}
			}
		}
		return this.operand;
	}

	/**
	 * Sets the operand of this prefix expression.
	 * 
	 * @param operand the operand
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setOperand(Expression operand) {
		if (operand == null) {
			throw new IllegalArgumentException();
		}
		ASTNode oldChild = this.operand;
		preReplaceChild(oldChild, operand, OPERAND_PROPERTY);
		this.operand = operand;
		postReplaceChild(oldChild, operand, OPERAND_PROPERTY);
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
			+ (this.operand == null ? 0 : getOperand().treeSize())
	;
	}

}

package descent.core.dom;

import java.util.ArrayList;
import java.util.List;


/**
 * Infix expression AST node type.
 * <pre>
 * InfixExpression:
 *    Expression InfixOperator Expression 
 * </pre>
 */
public class InfixExpression extends Expression {

	/**
	 * Binary operators.
	 * <pre>
	 * BinaryOperator:<code>
	 *    <b>*</b>	TIMES
	 *    <b>/</b>  DIVIDE
	 *    <b>%</b>  REMAINDER
	 *    <b>+</b>  PLUS
	 *    <b>-</b>  MINUS
	 *    <b>~</b>  CONCATENATE
	 *    <b>&lt;&lt;</b>  LEFT_SHIFT
	 *    <b>&gt;&gt;</b>  RIGHT_SHIFT_SIGNED
	 *    <b>&gt;&gt;&gt;</b>  RIGHT_SHIFT_UNSIGNED
	 *    <b>&lt;</b>  LESS
	 *    <b>&gt;</b>  GREATER
	 *    <b>&lt;=</b>  LESS_EQUALS
	 *    <b>&gt;=</b>  GREATER_EQUALS
	 *    <b>==</b>  EQUALS
	 *    <b>!=</b>  NOT_EQUALS
	 *    <b>^</b>  XOR
	 *    <b>&amp;</b>  AND
	 *    <b>|</b>  OR
	 *    <b>&amp;&amp;</b>  AND_AND
	 *    <b>||</b>  OR_OR
	 *    <b>,</b>  COMMA
	 *    <b>in</b>  IN
	 *    <b>===</b>  IDENTITY (deprecated)
	 *    <b>!==</b>  NOT_IDENTITY (deprecated)
	 *    <b>!<>=</b>  NOT_LESS_GREATER_EQUALS
	 *    <b>!<></b>  NOT_LESS_GREATER
	 *    <b><></b>  LESS_GREATER
	 *    <b><>=</b>  LESS_GREATER_EQUALS
	 *    <b>!<=</b>  NOT_LESS_EQUALS
	 *    <b>!</b>  NOT_LESS
	 *    <b>!>=</b>  NOT_GREATER_EQUALS
	 *    <b>!></b>  NOT_GREATER
	 *    <b>!<></b>  NOT_EQUALS
	 * </pre>
	 */
	public static enum Operator {
		
		/** Multiplication "*" operator. */
		TIMES("*"),
		/** Division "/" operator. */
		DIVIDE("/"),
		/** Remainder "%" operator. */
		REMAINDER("%"),
		/** Addition "+" operator. */
		PLUS("+"),
		/** Subtraction "-" operator. */
		MINUS("-"),
		/** Conctatenation "~" operator. */
		CONCATENATE("~"),
		/** Left shift "&lt;&lt;" operator. */
		LEFT_SHIFT("<<"),
		/** Signed right shift "&gt;&gt;" operator. */
		RIGHT_SHIFT_SIGNED(">>"),
		/** Unsigned right shift "&gt;&gt;&gt;" operator. */
		RIGHT_SHIFT_UNSIGNED(">>>"),
		/** Less than "&lt;" operator. */
		LESS("<"),
		/** Greater than "&gt;" operator. */
		GREATER(">"),
		/** Less than or equals "&lt;=" operator. */
		LESS_EQUALS("<="),
		/** Greater than or equals "&gt=;" operator. */
		GREATER_EQUALS(">="),
		/** Equals "==" operator. */
		EQUALS("=="),
		/** Not equals "!=" operator. */
		NOT_EQUALS("!="),
		/** Exclusive OR "^" operator. */
		XOR("^"),
		/** AND "&amp;" operator. */
		AND("&"),
		/** Inclusive OR "|" operator. */
		OR("|"),
		/** Conditinal AND "&amp;&amp;" operator. */
		AND_AND("&&"),
		/** Conditinal OR "||" operator. */
		OR_OR("||"),
		/** Comma "," operator. */
		COMMA(","),
		/** In "in" operator. */
		IN("in"),
		/** Is "is" operator. */
		IS("is"),
		/** Not identity "!is" operator. */
		NOT_IS("!is"),
		/** Identity "===" (deprecated) operator. */
		IDENTITY("==="),
		/** Not identity "!==" (deprecated) operator. */
		NOT_IDENTITY("!=="),
		/** Not less greater equals "!<>=" operator. */
		NOT_LESS_GREATER_EQUALS("!<>="),
		/** Less greater "<>" operator. */
		LESS_GREATER("<>"),
		/** Less greater equals "<>=" operator. */
		LESS_GREATER_EQUALS("<>="),
		/** Not less equals "!<=" operator. */
		NOT_LESS_EQUALS("!<="),
		/** Not less greater equals "!<" operator. */
		NOT_LESS("!<"),
		/** Not greater equals "!>=" operator. */
		NOT_GREATER_EQUALS("!>="),
		/** Not greater "!>" operator. */
		NOT_GREATER("!>"),
		/** Not less greater "!<>" operator. */
		NOT_LESS_GREATER("!<>"),
		;
		
		/**
		 * The token for the operator.
		 */
		private String token;
		
		/**
		 * Creates a new binary operator with the given token.
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
	 * The "leftOperand" structural property of this node type.
	 * @since 3.0
	 */
	public static final ChildPropertyDescriptor LEFT_OPERAND_PROPERTY = 
		new ChildPropertyDescriptor(InfixExpression.class, "leftOperand", Expression.class, MANDATORY, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "operator" structural property of this node type.
	 * @since 3.0
	 */
	public static final SimplePropertyDescriptor OPERATOR_PROPERTY = 
		new SimplePropertyDescriptor(InfixExpression.class, "operator", InfixExpression.Operator.class, MANDATORY); //$NON-NLS-1$
	
	/**
	 * The "rightOperand" structural property of this node type.
	 * @since 3.0
	 */
	public static final ChildPropertyDescriptor RIGHT_OPERAND_PROPERTY = 
		new ChildPropertyDescriptor(InfixExpression.class, "rightOperand", Expression.class, MANDATORY, CYCLE_RISK); //$NON-NLS-1$
	
	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;
	
	static {
		List properyList = new ArrayList(5);
		createPropertyList(InfixExpression.class, properyList);
		addProperty(LEFT_OPERAND_PROPERTY, properyList);
		addProperty(OPERATOR_PROPERTY, properyList);
		addProperty(RIGHT_OPERAND_PROPERTY, properyList);
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
	 * The infix operator; defaults to InfixExpression.Operator.PLUS.
	 */
	private InfixExpression.Operator operator = InfixExpression.Operator.PLUS;
	
	/**
	 * The left operand; lazily initialized; defaults to an unspecified,
	 * but legal, simple name.
	 */
	private Expression leftOperand;
	
	/**
	 * The right operand; lazily initialized; defaults to an unspecified,
	 * but legal, simple name.
	 */
	private Expression rightOperand;
	
	/**
	 * Creates a new AST node for an infix expression owned by the given 
	 * AST. By default, the node has unspecified (but legal) operator,
	 * left and right operands.
	 * 
	 * @param ast the AST that is to own this node
	 */
	InfixExpression(AST ast) {
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
				setOperator((InfixExpression.Operator) value);
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
		if (property == LEFT_OPERAND_PROPERTY) {
			if (get) {
				return getLeftOperand();
			} else {
				setLeftOperand((Expression) child);
				return null;
			}
		}
		if (property == RIGHT_OPERAND_PROPERTY) {
			if (get) {
				return getRightOperand();
			} else {
				setRightOperand((Expression) child);
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
		return INFIX_EXPRESSION;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		InfixExpression result = new InfixExpression(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.setOperator(getOperator());
		result.setLeftOperand((Expression) getLeftOperand().clone(target));
		result.setRightOperand((Expression) getRightOperand().clone(target));
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
			acceptChild(visitor, getLeftOperand());
			acceptChild(visitor, getRightOperand());
		}
		visitor.endVisit(this);
	}
	
	/**
	 * Returns the operator of this infix expression.
	 * 
	 * @return the infix operator
	 */ 
	public InfixExpression.Operator getOperator() {
		return this.operator;
	}

	/**
	 * Sets the operator of this infix expression.
	 * 
	 * @param operator the infix operator
	 * @exception IllegalArgumentException if the argument is incorrect
	 */ 
	public void setOperator(InfixExpression.Operator operator) {
		if (operator == null) {
			throw new IllegalArgumentException();
		}
		preValueChange(OPERATOR_PROPERTY);
		this.operator = operator;
		postValueChange(OPERATOR_PROPERTY);
	}
	
	/**
	 * Returns the left operand of this infix expression.
	 * 
	 * @return the left operand node
	 */ 
	public Expression getLeftOperand() {
		if (this.leftOperand  == null) {
			// lazy init must be thread-safe for readers
			synchronized (this) {
				if (this.leftOperand == null) {
					preLazyInit();
					this.leftOperand= new SimpleName(this.ast);
					postLazyInit(this.leftOperand, LEFT_OPERAND_PROPERTY);
				}
			}
		}
		return this.leftOperand;
	}
		
	/**
	 * Sets the left operand of this infix expression.
	 * 
	 * @param expression the left operand node
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setLeftOperand(Expression expression) {
		if (expression == null) {
			throw new IllegalArgumentException();
		}
		ASTNode oldChild = this.leftOperand;
		preReplaceChild(oldChild, expression, LEFT_OPERAND_PROPERTY);
		this.leftOperand = expression;
		postReplaceChild(oldChild, expression, LEFT_OPERAND_PROPERTY);
	}

	/**
	 * Returns the right operand of this infix expression.
	 * 
	 * @return the right operand node
	 */ 
	public Expression getRightOperand() {
		if (this.rightOperand  == null) {
			// lazy init must be thread-safe for readers
			synchronized (this) {
				if (this.rightOperand  == null) {
					preLazyInit();
					this.rightOperand= new SimpleName(this.ast);
					postLazyInit(this.rightOperand, RIGHT_OPERAND_PROPERTY);
				}
			}
		}
		return this.rightOperand;
	}
		
	/**
	 * Sets the right operand of this infix expression.
	 * 
	 * @param expression the right operand node
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setRightOperand(Expression expression) {
		if (expression == null) {
			throw new IllegalArgumentException();
		}
		ASTNode oldChild = this.rightOperand;
		preReplaceChild(oldChild, expression, RIGHT_OPERAND_PROPERTY);
		this.rightOperand = expression;
		postReplaceChild(oldChild, expression, RIGHT_OPERAND_PROPERTY);
	}
	
	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int memSize() {
		// treat Operator as free
		return BASE_NODE_SIZE + 3 * 4;
	}
	
	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int treeSize() {
		return 
			memSize()
			+ (this.leftOperand == null ? 0 : getLeftOperand().treeSize())
			+ (this.rightOperand == null ? 0 : getRightOperand().treeSize());
	}

}

package descent.core.dom;

import java.util.ArrayList;
import java.util.List;

/**
 * Assignment expression AST node type.
 *
 * <pre>
 * Assignment:
 *    Expression AssignmentOperator Expression
 * </pre>
 */
public class Assignment extends Expression {
	
	/**
	 * Assignment operators.
	 * <pre>
	 * AssignmentOperator:<code>
	 *    <b>=</b>  ASSIGN
	 *    <b>*=</b>	TIMES_ASSIGN
	 *    <b>/=</b>  DIVIDE_ASSIGN
	 *    <b>%=</b>  REMAINDER_ASSIGN
	 *    <b>+=</b>  PLUS_ASSIGN
	 *    <b>-=</b>  MINUS_ASSIGN
	 *    <b>~=</b>  CONCATENATE_ASSIGN
	 *    <b>&=</b>  AND_ASSIGN
	 *    <b>^=</b>  XOR_ASSIGN
	 *    <b>|=</b>  OR_ASSIGN
	 *    <b>&lt;&lt;=</b>  LEFT_SHIFT_ASSIGN
	 *    <b>&gt;&gt;=</b>  RIGHT_SHIFT_SIGNED_ASSIGN
	 *    <b>&gt;&gt;&gt;=</b>  RIGHT_SHIFT_UNSIGNED_ASSIGN
	 * </pre>
	 */
	public static enum Operator {
		
		/** Assign "=" operator. */
		ASSIGN("="),
		/** Multiplication and assign "*=" operator. */
		TIMES_ASSIGN("*="),
		/** Division and assign "/=" operator. */
		DIVIDE_ASSIGN("/="),
		/** Remainder and assign "%=" operator. */
		REMAINDER_ASSIGN("%="),
		/** Addition and assign "+=" operator. */
		PLUS_ASSIGN("+="),
		/** Minus and assign "-=" operator. */
		MINUS_ASSIGN("-="),
		/** Concatenate and assign "~=" operator. */
		CONCATENATE_ASSIGN("~="),
		/** AND and assign "^=" operator. */
		XOR_ASSIGN("^="),
		/** AND and assign "~=" operator. */
		AND_ASSIGN("&="),
		/** OR and assign "¬=" operator. */
		OR_ASSIGN("|="),
		/** Signed left shift and assign "<<=" operator. */
		LEFT_SHIFT_ASSIGN("<<="),
		/** Signed right shift and assign "&gt;&gt;=" operator. */
		RIGHT_SHIFT_SIGNED_ASSIGN(">>="),
		/** Unsigned right shift and assign "&gt;&gt;&gt;=" operator. */
		RIGHT_SHIFT_UNSIGNED_ASSIGN(">>>="), 
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
	 * The "leftHandSide" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor LEFT_HAND_SIDE_PROPERTY =
		new ChildPropertyDescriptor(Assignment.class, "leftHandSide", Expression.class, MANDATORY, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "operator" structural property of this node type.
	 */
	public static final SimplePropertyDescriptor OPERATOR_PROPERTY =
		new SimplePropertyDescriptor(Assignment.class, "operator", Operator.class, MANDATORY); //$NON-NLS-1$

	/**
	 * The "rightHandSide" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor RIGHT_HAND_SIDE_PROPERTY =
		new ChildPropertyDescriptor(Assignment.class, "rightHandSide", Expression.class, MANDATORY, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;

	static {
		List properyList = new ArrayList(3);
		createPropertyList(Assignment.class, properyList);
		addProperty(LEFT_HAND_SIDE_PROPERTY, properyList);
		addProperty(OPERATOR_PROPERTY, properyList);
		addProperty(RIGHT_HAND_SIDE_PROPERTY, properyList);
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
	 * The leftHandSide.
	 */
	private Expression leftHandSide;

	/**
	 * The operator.
	 */
	private Operator operator;

	/**
	 * The rightHandSide.
	 */
	private Expression rightHandSide;


	/**
	 * Creates a new unparented assignment node owned by the given 
	 * AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	Assignment(AST ast) {
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
		if (property == LEFT_HAND_SIDE_PROPERTY) {
			if (get) {
				return getLeftHandSide();
			} else {
				setLeftHandSide((Expression) child);
				return null;
			}
		}
		if (property == RIGHT_HAND_SIDE_PROPERTY) {
			if (get) {
				return getRightHandSide();
			} else {
				setRightHandSide((Expression) child);
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
		return ASSIGNMENT;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		Assignment result = new Assignment(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.setLeftHandSide((Expression) getLeftHandSide().clone(target));
		result.setOperator(getOperator());
		result.setRightHandSide((Expression) getRightHandSide().clone(target));
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
			acceptChild(visitor, getLeftHandSide());
			acceptChild(visitor, getRightHandSide());
		}
		visitor.endVisit(this);
	}

	/**
	 * Returns the left hand side of this assignment.
	 * 
	 * @return the left hand side
	 */ 
	public Expression getLeftHandSide() {
		if (this.leftHandSide == null) {
			// lazy init must be thread-safe for readers
			synchronized (this) {
				if (this.leftHandSide == null) {
					preLazyInit();
					this.leftHandSide = new SimpleName(this.ast);
					postLazyInit(this.leftHandSide, LEFT_HAND_SIDE_PROPERTY);
				}
			}
		}
		return this.leftHandSide;
	}

	/**
	 * Sets the left hand side of this assignment.
	 * 
	 * @param leftHandSide the left hand side
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setLeftHandSide(Expression leftHandSide) {
		if (leftHandSide == null) {
			throw new IllegalArgumentException();
		}
		ASTNode oldChild = this.leftHandSide;
		preReplaceChild(oldChild, leftHandSide, LEFT_HAND_SIDE_PROPERTY);
		this.leftHandSide = leftHandSide;
		postReplaceChild(oldChild, leftHandSide, LEFT_HAND_SIDE_PROPERTY);
	}

	/**
	 * Returns the operator of this assignment.
	 * 
	 * @return the operator
	 */ 
	public Operator getOperator() {
		return this.operator;
	}

	/**
	 * Sets the operator of this assignment.
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
	 * Returns the right hand side of this assignment.
	 * 
	 * @return the right hand side
	 */ 
	public Expression getRightHandSide() {
		if (this.rightHandSide == null) {
			// lazy init must be thread-safe for readers
			synchronized (this) {
				if (this.rightHandSide == null) {
					preLazyInit();
					this.rightHandSide = new SimpleName(this.ast);
					postLazyInit(this.rightHandSide, RIGHT_HAND_SIDE_PROPERTY);
				}
			}
		}
		return this.rightHandSide;
	}

	/**
	 * Sets the right hand side of this assignment.
	 * 
	 * @param rightHandSide the right hand side
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setRightHandSide(Expression rightHandSide) {
		if (rightHandSide == null) {
			throw new IllegalArgumentException();
		}
		ASTNode oldChild = this.rightHandSide;
		preReplaceChild(oldChild, rightHandSide, RIGHT_HAND_SIDE_PROPERTY);
		this.rightHandSide = rightHandSide;
		postReplaceChild(oldChild, rightHandSide, RIGHT_HAND_SIDE_PROPERTY);
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
			+ (this.leftHandSide == null ? 0 : getLeftHandSide().treeSize())
			+ (this.rightHandSide == null ? 0 : getRightHandSide().treeSize())
	;
	}

}

package descent.core.dom;



/**
 * A binary expression.
 */
public interface IBinaryExpression extends IExpression {
	
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
	 *    <b>=</b>  ASSIGN
	 *    <b>in</b>  IN
	 *    <b>===</b>  IDENTITY (deprecated, returned as IS)
	 *    <b>!==</b>  NOT_IDENTITY (deprecated, returned as NOT_IS)
	 *    <b>*=</b>	TIMES_ASSIGN
	 *    <b>/=</b>  DIVIDE_ASSIGN
	 *    <b>%=</b>  REMAINDER_ASSIGN
	 *    <b>+=</b>  PLUS_ASSIGN
	 *    <b>-=</b>  MINUS_ASSIGN
	 *    <b>~=</b>  CONCATENATE_ASSIGN
	 *    <b>&=</b>  AND_ASSIGN
	 *    <b>|=</b>  OR_ASSIGN
	 *    <b>&lt;&lt;=</b>  LEFT_SHIFT_ASSIGN
	 *    <b>&gt;&gt;=</b>  RIGHT_SHIFT_SIGNED_ASSIGN
	 *    <b>&gt;&gt;&gt;=</b>  RIGHT_SHIFT_UNSIGNED_ASSIGN
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
		/** Assign "=" operator. */
		ASSIGN("="),
		/** In "in" operator. */
		IN("in"),
		/** Is "is" operator. */
		IS("is"),
		/** Not identity "!is" operator. */
		NOT_IS("!is"),
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
	 * Returns the operator of this binary expression.
	 * @return
	 */
	Operator getOperator();
	
	/**
	 * Sets the operator of this binary expression.
	 */ 
	public void setOperator(Operator operator);
	
	/**
	 * Returns the left operand of this binary expression.
	 */
	IExpression getLeftOperand();
	
	/**
	 * Sets the left operand of this binary expression.
	 */
	void setLefOperand(IExpression leftOperand);	
	
	/**
	 * Returns the expression positioned at the right of the
	 * binary operator.
	 */
	IExpression getRightOperand();
	
	/**
	 * Sets the right operand of this binary expression.
	 */
	void setRightOperand(IExpression rightOperand);	

}

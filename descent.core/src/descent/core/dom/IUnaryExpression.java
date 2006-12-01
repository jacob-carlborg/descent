package descent.core.dom;

/**
 * A unary expression.
 */
public interface IUnaryExpression extends IExpression {
	
	/**
 	 * Unary operators.
 	 * <pre>
	 * UnaryOperator:<code>
	 *    <b>&x</b>	ADDRESS
	 *    <b>++x</b>  PRE_INCREMENT
	 *    <b>x++</b>  POST_INCREMENT
	 *    <b>--x</b>  PRE_DECREMENT
	 *    <b>x--</b>  POST_DECREMENT
	 *    <b>*x</b>  POINTER
	 *    <b>-x</b>  NEGATIVE
	 *    <b>+x</b>  POSITIVE
	 *    <b>!x</b>  NOT
	 *    <b>~x</b>  INVERT
	 * </pre>
	 */
	public static enum Operator {
		
		/** Address "&" operator. */
		ADDRESS("&"),
		/** Pre increment "++" operator. */
		PRE_INCREMENT("++"),
		/** Post increment "++" operator. */
		POST_INCREMENT("++"),
		/** Pre decrement "++" operator. */
		PRE_DECREMENT("--"),
		/** Post decrement "--" operator. */
		POST_DECREMENT("--"),
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
	 * Returns the type of the unary expression. Check the constants
	 * defined in this interface.
	 */
	Operator getOperator();
	
	/**
	 * Returns the inner expression of this unary expression.
	 */
	IExpression getInnerExpression();

}

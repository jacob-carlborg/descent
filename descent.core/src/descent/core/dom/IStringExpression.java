package descent.core.dom;

/**
 * Any string expression.
 */
public interface IStringExpression extends IExpression {
	
	/**
	 * Returns the string.
	 */
	String getString();
	
	/**
	 * Returns the postfix used in the string.
	 */
	char getPostfix();

}

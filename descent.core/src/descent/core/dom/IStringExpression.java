package descent.core.dom;

/**
 * Any string expression.
 */
public interface IStringExpression extends IExpression {
	
	/**
	 * Returns the string.
	 */
	String getEscapedValue();

}

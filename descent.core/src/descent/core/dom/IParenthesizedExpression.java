package descent.core.dom;

/**
 * A parenthesized expression:
 * 
 * <pre>
 * ( expr )
 * </pre>
 */
public interface IParenthesizedExpression extends IExpression {
	
	/**
	 * Returns the expression between the parenthesis.
	 */
	IExpression getExpression();

}

package descent.core.dom;

/**
 * A dot id expression:
 * 
 * <pre>
 * expr.name
 * </pre>
 */
public interface IDotIdentifierExpression extends IExpression {
	
	/**
	 * Returns the expression. May be <code>null</code>.
	 */
	IExpression getExpression();
	
	/**
	 * Returns the name.
	 */
	ISimpleName getName();

}

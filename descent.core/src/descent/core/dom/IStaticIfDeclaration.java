package descent.core.dom;

/**
 * A static if conditional declaration.
 */
public interface IStaticIfDeclaration extends IConditionalDeclaration {
	
	/**
	 * Returns the static condition to check.
	 */
	IExpression getExpression();

}

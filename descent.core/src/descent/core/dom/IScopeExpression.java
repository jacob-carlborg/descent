package descent.core.dom;

/**
 * A scope expression:
 * 
 * <pre>
 * name!(arg1, arg2, ..., argN)
 * </pre>
 * 
 * TODO: give a better name?
 */
public interface IScopeExpression extends IExpression {
	
	/**
	 * Returns the name.
	 */
	ISimpleName getName();
	
	/**
	 * Returns the arguments.
	 */
	IElement[] getArguments();

}

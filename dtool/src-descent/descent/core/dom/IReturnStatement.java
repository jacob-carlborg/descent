package descent.core.dom;

/**
 * A return statement:
 * 
 * <pre>
 * return value;
 * </pre>
 * 
 * where "value" is optional.
 */
public interface IReturnStatement extends IStatement {
	
	/**
	 * Returns the return value, if any, or <code>null</code>.
	 */
	IExpression getReturnValue();

}

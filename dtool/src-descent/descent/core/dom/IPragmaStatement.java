package descent.core.dom;

/**
 * A pragma statement:
 * 
 * <pre>
 * pragma(name, arg1, arg2, ..., argN) { }
 * </pre>
 */
public interface IPragmaStatement extends IDescentStatement {
	
	/**
	 * Returns the name of the pragma.
	 */
	IName getIdentifier();
	
	/**
	 * Returns the arguments of the pragma.
	 */
	IExpression[] getArguments();
	
	/**
	 * Returns the body of the pragma, if any, or null.
	 */
	IDescentStatement getBody();

}

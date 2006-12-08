package descent.core.dom;

import java.util.List;

import descent.internal.core.dom.Expression;

/**
 * A pragma statement:
 * 
 * <pre>
 * pragma(name, arg1, arg2, ..., argN) { }
 * </pre>
 */
public interface IPragmaStatement extends IStatement {
	
	/**
	 * Returns the name of the pragma.
	 */
	ISimpleName getName();
	
	/**
	 * Returns the arguments of the pragma.
	 */
	List<Expression> arguments();
	
	/**
	 * Returns the body of the pragma, if any, or null.
	 */
	IStatement getBody();

}

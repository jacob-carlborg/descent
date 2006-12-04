package descent.core.dom;

import java.util.List;

import descent.internal.core.dom.Expression;

/**
 * A call expression:
 * 
 * <pre>
 * expr(arg1, arg2, ..., argN)
 * </pre>
 *
 */
public interface ICallExpression extends IExpression {
	
	/**
	 * Returns the expression on which the call is made.
	 */
	IExpression getExpression();
	
	/**
	 * Returns the arguments of the call.
	 */
	List<Expression> arguments();

}

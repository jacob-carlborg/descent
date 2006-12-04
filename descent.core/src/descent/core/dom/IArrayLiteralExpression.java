package descent.core.dom;

import java.util.List;

import descent.internal.core.dom.Expression;

/**
 * An array literal expression:
 * 
 * <pre>
 * [arg1, arg2, ..., argN]
 * </pre>
 */
public interface IArrayLiteralExpression extends IExpression {
	
	/**
	 * Returns the arguments.
	 */
	List<Expression> arguments();

}

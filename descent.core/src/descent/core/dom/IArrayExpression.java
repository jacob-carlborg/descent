package descent.core.dom;

import java.util.List;

import descent.internal.core.dom.Expression;

/**
 * An array expression:
 * 
 * <pre>
 * expr[arg1, arg2, ..., argN]
 * </pre>
 */
public interface IArrayExpression extends IExpression {
	
	/**
	 * Returns the expression on which the array is operating.
	 */
	IExpression getArray();
	
	/**
	 * Returns the arguments of the array.
	 */
	List<Expression> indexes();

}

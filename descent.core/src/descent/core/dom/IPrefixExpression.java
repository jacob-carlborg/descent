package descent.core.dom;

import descent.internal.core.dom.PrefixExpression.Operator;

/**
 * A unary expression.
 */
public interface IPrefixExpression extends IExpression {
	
	/**
	 * Returns the type of the unary expression. Check the constants
	 * defined in this interface.
	 */
	Operator getOperator();
	
	/**
	 * Returns the inner expression of this unary expression.
	 */
	IExpression getExpression();

}

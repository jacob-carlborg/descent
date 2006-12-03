package descent.core.dom;

import descent.internal.core.dom.Expression;
import descent.internal.core.dom.InfixExpression;



/**
 * A binary expression.
 */
public interface IInfixExpression extends IExpression {
	
	/**
	 * Returns the operator of this binary expression.
	 * @return
	 */
	InfixExpression.Operator getOperator();
	
	/**
	 * Sets the operator of this binary expression.
	 */ 
	public void setOperator(InfixExpression.Operator operator);
	
	/**
	 * Returns the left operand of this binary expression.
	 */
	Expression getLeftOperand();
	
	/**
	 * Sets the left operand of this binary expression.
	 */
	void setLeftOperand(Expression leftOperand);	
	
	/**
	 * Returns the expression positioned at the right of the
	 * binary operator.
	 */
	Expression getRightOperand();
	
	/**
	 * Sets the right operand of this binary expression.
	 */
	void setRightOperand(Expression rightOperand);	

}

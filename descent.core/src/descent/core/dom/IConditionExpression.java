package descent.core.dom;

/**
 * A contidion expression:
 * 
 * <pre>
 * condition ? true : false
 * </pre>
 */
public interface IConditionExpression extends IExpression {
	
	/**
	 * Returns the condition to evaluate.
	 */
	IExpression getCondition();
	
	/**
	 * Returns the expression to evaluate in case the
	 * condition is met.
	 */
	IExpression getTrue();
	
	/**
	 * Returns the expression to evaluate in case the
	 * condition is not met.
	 */
	IExpression getFalse();

}

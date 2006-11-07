package descent.core.dom;

/**
 * A unary expression.
 */
public interface IUnaryExpression extends IExpression {
	
	int ADDRESS = 1;
	int PRE_INCREMENT = 2;
	int PRE_DECREMENT = 3;
	int POINTER = 4;
	int NEGATIVE = 5;
	int POSITIVE = 6;
	int NOT = 7;
	int INVERT = 8;
	int POST_INCREMENT = 9;
	int POST_DECREMENT = 10;
	
	/**
	 * Returns the type of the unary expression. Check the constants
	 * defined in this interface.
	 */
	int getUnaryExpressionType();
	
	/**
	 * Returns the inner expression of this unary expression.
	 */
	IExpression getInnerExpression();

}

package descent.internal.core.dom;


public class PostDecExp extends UnaryExpression {

	public PostDecExp(Expression e) {
		super(e);
	}
	
	public int getUnaryExpressionType() {
		return UnaryExpression.IUnaryExpression2.POST_DECREMENT;
	}

}

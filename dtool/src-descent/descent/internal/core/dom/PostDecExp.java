package descent.internal.core.dom;

import descent.internal.core.dom.UnaryExpression.IUnaryExpression2;

public class PostDecExp extends UnaryExpression {

	public PostDecExp(Expression e) {
		super(e);
	}
	
	public int getUnaryExpressionType() {
		return UnaryExpression.IUnaryExpression2.POST_DECREMENT;
	}

}

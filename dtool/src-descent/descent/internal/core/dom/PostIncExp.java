package descent.internal.core.dom;

import descent.internal.core.dom.UnaryExpression.IUnaryExpression2;


public class PostIncExp extends UnaryExpression {

	public PostIncExp(Expression e) {
		super(e);
	}
	
	public int getUnaryExpressionType() {
		return UnaryExpression.IUnaryExpression2.POST_INCREMENT;
	}

}

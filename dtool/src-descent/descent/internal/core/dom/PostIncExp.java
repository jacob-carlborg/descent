package descent.internal.core.dom;



public class PostIncExp extends UnaryExpression {

	public PostIncExp(Expression e) {
		super(e);
	}
	
	public int getUnaryExpressionType() {
		return UnaryExpression.IUnaryExpression2.POST_INCREMENT;
	}

}

package descent.internal.core.dom;


public class PostIncExp extends UnaryExpression {

	public PostIncExp(Expression e) {
		super(e);
	}
	
	public int getUnaryExpressionType() {
		return POST_INCREMENT;
	}

}

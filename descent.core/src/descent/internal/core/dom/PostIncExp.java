package descent.internal.core.dom;


public class PostIncExp extends UnaryExpression {

	public PostIncExp(Loc loc, Expression e) {
		super(e);
	}
	
	public int getUnaryExpressionType() {
		return POST_INCREMENT;
	}

}

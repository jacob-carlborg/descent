package descent.internal.core.dom;

public class PostDecExp extends UnaryExpression {

	public PostDecExp(Loc loc, Expression e) {
		super(e);
	}
	
	public int getUnaryExpressionType() {
		return POST_DECREMENT;
	}

}

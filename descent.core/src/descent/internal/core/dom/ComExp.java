package descent.internal.core.dom;

public class ComExp extends UnaryExpression {

	public ComExp(Expression e) {
		super(e);
	}
	
	public int getUnaryExpressionType() {
		return INVERT;
	}

}

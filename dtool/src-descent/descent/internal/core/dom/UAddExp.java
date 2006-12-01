package descent.internal.core.dom;

public class UAddExp extends UnaryExpression {

	public UAddExp(Expression e) {
		super(e);
	}
	
	public int getUnaryExpressionType() {
		return POSITIVE;
	}

}

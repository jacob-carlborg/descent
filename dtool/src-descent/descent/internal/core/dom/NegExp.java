package descent.internal.core.dom;


public class NegExp extends UnaryExpression {

	public NegExp(Expression e) {
		super(e);
	}
	
	public int getUnaryExpressionType() {
		return UnaryExpression.IUnaryExpression2.NEGATIVE;
	}

}

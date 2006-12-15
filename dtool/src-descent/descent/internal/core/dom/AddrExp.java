package descent.internal.core.dom;


public class AddrExp extends UnaryExpression {
	
	public AddrExp(Expression e) {
		super(e);
	}
	
	public int getUnaryExpressionType() {
		return UnaryExpression.IUnaryExpression2.ADDRESS;
	}

}

package descent.internal.core.dom;

import descent.internal.core.dom.UnaryExpression.IUnaryExpression2;

public class AddrExp extends UnaryExpression {
	
	public AddrExp(Expression e) {
		super(e);
	}
	
	public int getUnaryExpressionType() {
		return UnaryExpression.IUnaryExpression2.ADDRESS;
	}

}

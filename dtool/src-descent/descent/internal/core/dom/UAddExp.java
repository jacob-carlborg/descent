package descent.internal.core.dom;

import descent.internal.core.dom.UnaryExpression.IUnaryExpression2;

public class UAddExp extends UnaryExpression {

	public UAddExp(Expression e) {
		super(e);
	}
	
	public int getUnaryExpressionType() {
		return UnaryExpression.IUnaryExpression2.POSITIVE;
	}

}

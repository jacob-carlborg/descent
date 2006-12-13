package descent.internal.core.dom;

import descent.internal.core.dom.UnaryExpression.IUnaryExpression2;

public class PtrExp extends UnaryExpression {

	public PtrExp(Expression e) {
		super(e);
	}
	
	public int getUnaryExpressionType() {
		return UnaryExpression.IUnaryExpression2.POINTER;
	}

}

package descent.internal.core.dom;

import descent.internal.core.dom.UnaryExpression.IUnaryExpression2;

public class NotExp extends UnaryExpression {

	public NotExp(Expression e) {
		super(e);
	}
	
	public int getUnaryExpressionType() {
		return UnaryExpression.IUnaryExpression2.NOT;
	}

}

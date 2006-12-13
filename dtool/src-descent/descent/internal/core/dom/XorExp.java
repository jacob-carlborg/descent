package descent.internal.core.dom;

import descent.internal.core.dom.BinaryExpression.BinaryExpressionTypes;

public class XorExp extends BinaryExpression {

	public XorExp(Expression e, Expression e2) {
		super(e, e2);
	}
	
	public int getBinaryExpressionType() {
		return BinaryExpressionTypes.XOR;
	}

}

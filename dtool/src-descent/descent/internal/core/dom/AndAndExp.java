package descent.internal.core.dom;

import descent.internal.core.dom.BinaryExpression.BinaryExpressionTypes;

public class AndAndExp extends BinaryExpression {

	public AndAndExp(Expression e, Expression e2) {
		super(e, e2);
	}
	
	public int getBinaryExpressionType() {
		return BinaryExpressionTypes.AND_AND;
	}

}

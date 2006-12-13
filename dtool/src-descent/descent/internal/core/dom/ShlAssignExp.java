package descent.internal.core.dom;

import descent.internal.core.dom.BinaryExpression.BinaryExpressionTypes;

public class ShlAssignExp extends BinaryExpression {

	public ShlAssignExp(Expression e, Expression exp) {
		super(e, exp);
	}
	
	public int getBinaryExpressionType() {
		return BinaryExpressionTypes.SHIFT_LEFT_ASSIGN;
	}

}

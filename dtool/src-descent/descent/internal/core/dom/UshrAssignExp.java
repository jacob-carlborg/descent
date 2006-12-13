package descent.internal.core.dom;

import descent.internal.core.dom.BinaryExpression.BinaryExpressionTypes;

public class UshrAssignExp extends BinaryExpression {

	public UshrAssignExp(Expression e, Expression exp) {
		super(e, exp);
	}
	
	public int getBinaryExpressionType() {
		return BinaryExpressionTypes.UNSIGNED_SHIFT_RIGHT_ASSIGN;
	}

}

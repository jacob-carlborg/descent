package descent.internal.core.dom;


public class ShrAssignExp extends BinaryExpression {

	public ShrAssignExp(Expression e, Expression exp) {
		super(e, exp);
	}
	
	public int getBinaryExpressionType() {
		return BinaryExpressionTypes.SHIFT_RIGHT_ASSIGN;
	}

}

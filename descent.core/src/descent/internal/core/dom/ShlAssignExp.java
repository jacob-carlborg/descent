package descent.internal.core.dom;

public class ShlAssignExp extends BinaryExpression {

	public ShlAssignExp(Loc loc, Expression e, Expression exp) {
		super(e, exp);
	}
	
	public int getBinaryExpressionType() {
		return SHIFT_LEFT_ASSIGN;
	}

}

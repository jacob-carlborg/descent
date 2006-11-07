package descent.internal.core.dom;

public class UshrAssignExp extends BinaryExpression {

	public UshrAssignExp(Loc loc, Expression e, Expression exp) {
		super(e, exp);
	}
	
	public int getBinaryExpressionType() {
		return UNSIGNED_SHIFT_RIGHT_ASSIGN;
	}

}

package descent.internal.core.dom;

public class MulAssignExp extends BinaryExpression {

	public MulAssignExp(Loc loc, Expression e, Expression e2) {
		super(e, e2);
	}
	
	public int getBinaryExpressionType() {
		return MUL_ASSIGN;
	}

}

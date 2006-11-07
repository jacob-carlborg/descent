package descent.internal.core.dom;

public class AssignExp extends BinaryExpression {

	public AssignExp(Loc loc, Expression e, Expression e2) {
		super(e, e2);
	}

	public int getBinaryExpressionType() {
		return ASSIGN;
	}

}

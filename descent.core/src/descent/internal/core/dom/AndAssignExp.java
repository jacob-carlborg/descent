package descent.internal.core.dom;

public class AndAssignExp extends BinaryExpression {

	public AndAssignExp(Loc loc, Expression e, Expression exp) {
		super(e, exp);
	}
	
	public int getBinaryExpressionType() {
		return AND_ASSIGN;
	}

}

package descent.internal.core.dom;


public class AndAssignExp extends BinaryExpression {

	public AndAssignExp(Expression e, Expression exp) {
		super(e, exp);
	}
	
	public int getBinaryExpressionType() {
		return BinaryExpressionTypes.AND_ASSIGN;
	}

}

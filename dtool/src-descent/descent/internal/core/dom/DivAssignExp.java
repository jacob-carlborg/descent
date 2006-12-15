package descent.internal.core.dom;


public class DivAssignExp extends BinaryExpression {

	public DivAssignExp(Expression e, Expression e2) {
		super(e, e2);
	}
	
	public int getBinaryExpressionType() {
		return BinaryExpressionTypes.DIV_ASSIGN;
	}

}

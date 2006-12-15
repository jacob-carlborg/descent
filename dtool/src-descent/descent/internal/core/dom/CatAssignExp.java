package descent.internal.core.dom;


public class CatAssignExp extends BinaryExpression {

	public CatAssignExp(Expression e, Expression exp) {
		super(e, exp);
	}
	
	public int getBinaryExpressionType() {
		return BinaryExpressionTypes.CAT_ASSIGN;
	}

}

package descent.internal.core.dom;

public class CatAssignExp extends BinaryExpression {

	public CatAssignExp(Loc loc, Expression e, Expression exp) {
		super(e, exp);
	}
	
	public int getBinaryExpressionType() {
		return CAT_ASSIGN;
	}

}

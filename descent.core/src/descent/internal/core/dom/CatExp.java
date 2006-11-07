package descent.internal.core.dom;

public class CatExp extends BinaryExpression {

	public CatExp(Loc loc, Expression e, Expression e2) {
		super(e, e2);
	}
	
	public int getBinaryExpressionType() {
		return CAT;
	}

}

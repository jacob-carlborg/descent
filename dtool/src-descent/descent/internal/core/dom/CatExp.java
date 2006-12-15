package descent.internal.core.dom;


public class CatExp extends BinaryExpression {

	public CatExp(Expression e, Expression e2) {
		super(e, e2);
	}
	
	public int getBinaryExpressionType() {
		return BinaryExpressionTypes.CAT;
	}

}

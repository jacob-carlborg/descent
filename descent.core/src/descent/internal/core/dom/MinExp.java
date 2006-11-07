package descent.internal.core.dom;

public class MinExp extends BinaryExpression {

	public MinExp(Loc loc, Expression e, Expression e2) {
		super(e, e2);
	}
	
	public int getBinaryExpressionType() {
		return MIN;
	}

}
